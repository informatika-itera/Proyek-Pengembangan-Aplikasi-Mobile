package com.example.travelplanner.presentation.screens.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelplanner.domain.model.Expense
import com.example.travelplanner.domain.model.Trip
import com.example.travelplanner.domain.repository.TripRepository
import com.example.travelplanner.domain.repository.ExpenseRepository
import com.example.travelplanner.domain.usecase.ExtractExpenseUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.doubleOrNull
import kotlinx.datetime.Clock
import kotlin.random.Random

data class ExpenseUiState(
    val isLoading: Boolean = false,
    val trip: Trip? = null,
    val expenses: List<Expense> = emptyList(),
    val filteredExpenses: List<Expense> = emptyList(),
    val totalExpenses: Double = 0.0,
    val activeCategoryFilter: String = "Semua",
    val aiIsProcessing: Boolean = false,
    val errorMessage: String? = null
)

class ExpenseViewModel(
    private val tripRepository: TripRepository,
    private val expenseRepository: ExpenseRepository,
    private val extractExpenseUseCase: ExtractExpenseUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ExpenseUiState>(ExpenseUiState())
    val uiState: StateFlow<ExpenseUiState> = _uiState.asStateFlow()

    private var currentTripId: String = ""

    fun initializeTrip(tripId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val finalTripId = if (tripId.isBlank()) {
                val trips = tripRepository.getAllTrips().first()
                trips.firstOrNull()?.id ?: ""
            } else {
                tripId
            }

            if (finalTripId.isBlank()) {
                _uiState.value = ExpenseUiState(
                    isLoading = false,
                    trip = null,
                    expenses = emptyList(),
                    totalExpenses = 0.0
                )
                return@launch
            }

            currentTripId = finalTripId

            val tripFlow = tripRepository.getTripById(finalTripId)
            val expensesFlow = expenseRepository.getExpensesForTrip(finalTripId)
            val totalFlow = expenseRepository.getTotalExpensesForTrip(finalTripId)

            combine(tripFlow, expensesFlow, totalFlow) { trip, expenses, total ->
                ExpenseUiState(
                    isLoading = false,
                    trip = trip,
                    expenses = expenses,
                    filteredExpenses = filterExpenses(expenses, _uiState.value.activeCategoryFilter),
                    totalExpenses = total,
                    activeCategoryFilter = _uiState.value.activeCategoryFilter
                )
            }.catch { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Gagal memuat biaya: ${e.message}"
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun setCategoryFilter(category: String) {
        val currentExpenses = _uiState.value.expenses
        _uiState.value = _uiState.value.copy(
            activeCategoryFilter = category,
            filteredExpenses = filterExpenses(currentExpenses, category)
        )
    }

    private fun filterExpenses(expenses: List<Expense>, category: String): List<Expense> {
        return if (category == "Semua") {
            expenses
        } else {
            expenses.filter { it.kategori.equals(category, ignoreCase = true) }
        }
    }

    fun addExpenseAI(conversationalText: String) {
        if (conversationalText.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(aiIsProcessing = true, errorMessage = null)
            try {
                val jsonResult = extractExpenseUseCase.execute(conversationalText)
                val cleanedResult = cleanJson(jsonResult)
                
                // Parse extracted JSON array dengan aman
                val jsonArray = Json.parseToJsonElement(cleanedResult).jsonArray
                
                if (jsonArray.isEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        aiIsProcessing = false,
                        errorMessage = "AI tidak mendeteksi nominal atau nama barang dalam teks Anda."
                    )
                    return@launch
                }

                // Loop through extracted items and save to DB
                for (element in jsonArray) {
                    val obj = element.jsonObject
                    val namaItem = obj["nama_item"]?.jsonPrimitive?.content ?: "Pembelian"
                    val nominal = obj["nominal"]?.jsonPrimitive?.doubleOrNull ?: 0.0
                    val kategori = obj["kategori"]?.jsonPrimitive?.content ?: "Lainnya"

                    val expenseId = "exp_${Random.nextLong(100000, 999999)}"
                    val newExpense = Expense(
                        id = expenseId,
                        tripId = currentTripId,
                        namaItem = namaItem,
                        nominal = nominal,
                        kategori = kategori,
                        createdAt = Clock.System.now().toEpochMilliseconds()
                    )
                    expenseRepository.saveExpense(newExpense)
                }

                _uiState.value = _uiState.value.copy(aiIsProcessing = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    aiIsProcessing = false,
                    errorMessage = "Gagal mengurai teks dengan AI: ${e.message}"
                )
            }
        }
    }

    fun deleteExpense(expenseId: String) {
        viewModelScope.launch {
            try {
                expenseRepository.deleteExpense(expenseId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "Gagal menghapus item: ${e.message}")
            }
        }
    }

    fun addManualExpense(namaItem: String, nominal: Double, kategori: String) {
        viewModelScope.launch {
            try {
                val expenseId = "exp_${Random.nextLong(100000, 999999)}"
                val newExpense = Expense(
                    id = expenseId,
                    tripId = currentTripId,
                    namaItem = namaItem,
                    nominal = nominal,
                    kategori = kategori,
                    createdAt = Clock.System.now().toEpochMilliseconds()
                )
                expenseRepository.saveExpense(newExpense)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "Gagal menambahkan item: ${e.message}")
            }
        }
    }

    private fun cleanJson(rawText: String): String {
        val trimmed = rawText.trim()
        val startIndex = trimmed.indexOfAny(charArrayOf('[', '{'))
        if (startIndex == -1) return trimmed
        val endIndex = trimmed.lastIndexOfAny(charArrayOf(']', '}'))
        if (endIndex == -1 || endIndex < startIndex) return trimmed
        return trimmed.substring(startIndex, endIndex + 1)
    }
}
