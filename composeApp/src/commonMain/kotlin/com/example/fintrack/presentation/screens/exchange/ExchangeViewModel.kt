package com.example.fintrack.presentation.screens.exchange

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintrack.core.network.NetworkResult
import com.example.fintrack.data.local.datastore.UserPreferences
import com.example.fintrack.data.remote.api.ExchangeApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// ==================== UI STATE ====================

sealed class ExchangeUiState {
    data object Loading : ExchangeUiState()
    data class Success(
        val base: String,
        val date: String,
        val rates: List<RateDisplayItem>,
        val isOffline: Boolean = false
    ) : ExchangeUiState()
    data class Error(val message: String) : ExchangeUiState()
}

data class RateDisplayItem(
    val currencyCode: String,
    val currencyName: String,
    val rate: Double,
    val pair: String
)

// ==================== CURRENCY NAME MAP ====================

private val CURRENCY_NAMES = mapOf(
    "USD" to "US Dollar",
    "IDR" to "Indonesian Rupiah",
    "EUR" to "Euro",
    "GBP" to "British Pound",
    "JPY" to "Japanese Yen",
    "SGD" to "Singapore Dollar",
    "AUD" to "Australian Dollar",
    "CAD" to "Canadian Dollar",
    "CHF" to "Swiss Franc",
    "CNY" to "Chinese Yuan",
    "MYR" to "Malaysian Ringgit"
)

// ==================== VIEWMODEL ====================

class ExchangeViewModel(
    private val apiService: ExchangeApiService,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow<ExchangeUiState>(ExchangeUiState.Loading)
    val uiState: StateFlow<ExchangeUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _inputAmount = MutableStateFlow("1")
    val inputAmount: StateFlow<String> = _inputAmount.asStateFlow()

    private val _baseCurrency = MutableStateFlow("USD")
    val baseCurrency: StateFlow<String> = _baseCurrency.asStateFlow()

    private val _targetCurrency = MutableStateFlow("IDR")
    val targetCurrency: StateFlow<String> = _targetCurrency.asStateFlow()

    init {
        fetchRates()
    }

    private fun parseCachedRates(ratesString: String): Map<String, Double> {
        if (ratesString.isBlank()) return emptyMap()
        return ratesString.split(";").mapNotNull {
            val parts = it.split(":")
            if (parts.size == 2) {
                val code = parts[0]
                val rate = parts[1].toDoubleOrNull()
                if (rate != null) code to rate else null
            } else null
        }.toMap()
    }

    fun fetchRates() {
        viewModelScope.launch {
            _isRefreshing.value = true
            if (_uiState.value !is ExchangeUiState.Success) {
                _uiState.value = ExchangeUiState.Loading
            }
            when (val result = apiService.getLatestRates(baseCurrency = _baseCurrency.value)) {
                is NetworkResult.Success -> {
                    val data = result.data
                    val rateItems = data.rates.map { (code, rate) ->
                        RateDisplayItem(
                            currencyCode = code,
                            currencyName = CURRENCY_NAMES[code] ?: code,
                            rate = rate,
                            pair = "${data.base}/$code"
                        )
                    }
                    data.rates["IDR"]?.let { idrRate ->
                        userPreferences.setLastIdrRate(idrRate)
                    }
                    
                    // Save to offline cache in UserPreferences
                    val ratesString = data.rates.entries.joinToString(";") { "${it.key}:${it.value}" }
                    userPreferences.saveCachedRates(data.base, data.date, ratesString)

                    _uiState.value = ExchangeUiState.Success(
                        base = data.base,
                        date = data.date,
                        rates = rateItems,
                        isOffline = false
                    )
                    _isRefreshing.value = false
                }
                is NetworkResult.Error -> {
                    // Try to load from offline cache
                    try {
                        val cachedBase = userPreferences.lastExchangeBase.first()
                        val cachedDate = userPreferences.lastExchangeDate.first()
                        val cachedRatesStr = userPreferences.lastExchangeRates.first()
                        val cachedRatesMap = parseCachedRates(cachedRatesStr).toMutableMap()

                        if (cachedRatesMap.isNotEmpty()) {
                            cachedRatesMap[cachedBase] = 1.0
                            val currentBase = _baseCurrency.value
                            val resolvedRates = mutableMapOf<String, Double>()

                            val baseToCurrentBase = cachedRatesMap[currentBase]
                            if (baseToCurrentBase != null && baseToCurrentBase > 0.0) {
                                cachedRatesMap.forEach { (code, rateVal) ->
                                    if (code != currentBase) {
                                        resolvedRates[code] = rateVal / baseToCurrentBase
                                    }
                                }
                            } else if (currentBase == cachedBase) {
                                cachedRatesMap.forEach { (code, rateVal) ->
                                    if (code != currentBase) {
                                        resolvedRates[code] = rateVal
                                    }
                                }
                            }

                            if (resolvedRates.isNotEmpty()) {
                                val rateItems = resolvedRates.map { (code, rate) ->
                                    RateDisplayItem(
                                        currencyCode = code,
                                        currencyName = CURRENCY_NAMES[code] ?: code,
                                        rate = rate,
                                        pair = "$currentBase/$code"
                                    )
                                }
                                _uiState.value = ExchangeUiState.Success(
                                    base = currentBase,
                                    date = "$cachedDate (Offline)",
                                    rates = rateItems,
                                    isOffline = true
                                )
                                _isRefreshing.value = false
                                return@launch
                            }
                        }
                    } catch (e: Exception) {
                        // Fallback to error state if cache read fails
                    }

                    _uiState.value = ExchangeUiState.Error(result.message)
                    _isRefreshing.value = false
                }
                is NetworkResult.Loading -> {
                    _uiState.value = ExchangeUiState.Loading
                }
            }
        }
    }

    fun onAmountChanged(amount: String) {
        if (amount.isEmpty() || amount.toDoubleOrNull() != null) {
            _inputAmount.value = amount
        }
    }

    fun updateBaseCurrency(currency: String) {
        if (_baseCurrency.value != currency) {
            _baseCurrency.value = currency
            if (_targetCurrency.value == currency) {
                _targetCurrency.value = "IDR"
            }
            fetchRates()
        }
    }

    fun updateTargetCurrency(currency: String) {
        _targetCurrency.value = currency
    }

    fun swapCurrencies() {
        val oldBase = _baseCurrency.value
        val oldTarget = _targetCurrency.value
        _baseCurrency.value = oldTarget
        _targetCurrency.value = oldBase
        fetchRates()
    }

    fun convertedAmount(): Double? {
        val rates = (_uiState.value as? ExchangeUiState.Success)?.rates ?: return null
        val rate = rates.find { it.currencyCode == _targetCurrency.value }?.rate ?: return null
        val amount = _inputAmount.value.toDoubleOrNull() ?: 1.0
        return amount * rate
    }
}
