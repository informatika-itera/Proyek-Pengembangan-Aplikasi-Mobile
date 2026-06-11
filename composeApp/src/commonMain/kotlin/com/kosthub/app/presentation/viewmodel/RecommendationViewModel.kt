package com.kosthub.app.presentation.viewmodel

import com.kosthub.app.data.remote.api.GeminiPromptBuilder
import com.kosthub.app.data.remote.api.GeminiService
import com.kosthub.app.domain.model.Kost
import com.kosthub.app.platform.PlatformConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class RecommendationState {
    object Idle : RecommendationState()
    object Loading : RecommendationState()
    data class Success(val result: String) : RecommendationState()
}

class RecommendationViewModel(
    private val geminiService: GeminiService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    private val _userPreference = MutableStateFlow("")
    val userPreference: StateFlow<String> = _userPreference

    private val _state = MutableStateFlow<RecommendationState>(RecommendationState.Idle)
    val state: StateFlow<RecommendationState> = _state

    private var lastSuccessResult: String? = null
    private var lastKosts: List<Kost> = emptyList()

    fun onPreferenceChange(text: String) {
        _userPreference.value = text
        if (_state.value is RecommendationState.Success) {
            _state.value = RecommendationState.Idle
        }
    }

    fun generateRecommendation(kosts: List<Kost>) {
        val preference = _userPreference.value.trim()
        lastKosts = kosts

        if (preference.isEmpty() || kosts.isEmpty()) {
            // Silently fall back to last success or show defaults
            fallbackToLastResult()
            return
        }

        val apiKey = PlatformConfig.geminiApiKey
        if (apiKey.isBlank()) {
            // Show default recommendations from available data
            _state.value = RecommendationState.Success(buildDefaultRecommendation(kosts))
            return
        }

        _state.value = RecommendationState.Loading

        scope.launch {
            val kostData = kosts.joinToString("\n\n") { kost ->
                GeminiPromptBuilder.formatKostForPrompt(
                    id = kost.id,
                    namaKos = kost.namaKos,
                    hargaTahunan = kost.hargaTahunan,
                    jarakKm = kost.jarakKm,
                    tipeKos = kost.tipeKos,
                    kamarMandi = kost.kamarMandi,
                    wifi = kost.wifi,
                    fasilitasPendingin = kost.fasilitasPendingin,
                    furniturKasur = kost.furniturKasur,
                    furniturLemari = kost.furniturLemari,
                    furniturMejaBelajar = kost.furniturMejaBelajar,
                    areaLaundry = kost.areaLaundry,
                    areaDapur = kost.areaDapur,
                    keamananCctv = kost.keamananCctv
                )
            }

            val prompt = GeminiPromptBuilder.buildRecommendationPrompt(
                kostData = kostData,
                userPreference = preference
            )

            val result = geminiService.generateRecommendation(prompt, apiKey)
            _state.value = result.fold(
                onSuccess = { text ->
                    lastSuccessResult = text
                    RecommendationState.Success(text)
                },
                onFailure = {
                    // On error, show cached result or generate defaults from kost data
                    val cached = lastSuccessResult
                    if (cached != null) {
                        RecommendationState.Success(cached)
                    } else {
                        RecommendationState.Success(buildDefaultRecommendation(kosts))
                    }
                }
            )
        }
    }

    private fun fallbackToLastResult() {
        val cached = lastSuccessResult
        _state.value = when {
            cached != null -> RecommendationState.Success(cached)
            lastKosts.isNotEmpty() -> RecommendationState.Success(buildDefaultRecommendation(lastKosts))
            else -> RecommendationState.Idle
        }
    }

    private fun buildDefaultRecommendation(kosts: List<Kost>): String {
        val topKosts = kosts
            .sortedWith(compareBy<Kost> { it.jarakKm }.thenBy { it.hargaTahunan })
            .take(3)

        return topKosts.joinToString("\n\n") { kost ->
            val fasilitas = mutableListOf<String>()
            if (kost.wifi == "Ada") fasilitas.add("WiFi")
            if (kost.fasilitasPendingin == "Ada") fasilitas.add("AC")
            if (kost.kamarMandi == "Dalam") fasilitas.add("Kamar Mandi Dalam")
            if (kost.furniturKasur == "Ada") fasilitas.add("Kasur")
            if (kost.furniturLemari == "Ada") fasilitas.add("Lemari")
            if (kost.furniturMejaBelajar == "Ada") fasilitas.add("Meja Belajar")
            if (kost.keamananCctv == "Ada") fasilitas.add("CCTV")

            val hargaFormatted = "Rp ${kost.hargaTahunan.formatNumber()}/tahun"
            val fasilitasText = if (fasilitas.isNotEmpty()) fasilitas.joinToString(", ") else "Fasilitas standar"

            "NAMA: ${kost.namaKos}\n" +
            "DETAIL: Tipe ${kost.tipeKos} • $hargaFormatted • Jarak ${kost.jarakKm} km dari kampus\n" +
            "ALASAN: Fasilitas: $fasilitasText"
        }
    }

    fun reset() {
        _state.value = RecommendationState.Idle
        _userPreference.value = ""
        lastSuccessResult = null
    }

    fun dispose() {
        scope.cancel()
    }
}

private fun Long.formatNumber(): String {
    val str = this.toString()
    val result = StringBuilder()
    var count = 0
    for (i in str.length - 1 downTo 0) {
        result.append(str[i])
        count++
        if (count % 3 == 0 && i > 0) {
            result.append('.')
        }
    }
    return result.reverse().toString()
}
