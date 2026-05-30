package com.kosthub.app.presentation.viewmodel

import com.kosthub.app.data.remote.api.GeminiPromptBuilder
import com.kosthub.app.data.remote.api.GeminiService
import com.kosthub.app.domain.model.Kost
import com.kosthub.app.platform.PlatformConfig
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
    data class Error(val message: String) : RecommendationState()
}

class RecommendationViewModel(
    private val geminiService: GeminiService
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _userPreference = MutableStateFlow("")
    val userPreference: StateFlow<String> = _userPreference

    private val _state = MutableStateFlow<RecommendationState>(RecommendationState.Idle)
    val state: StateFlow<RecommendationState> = _state

    fun onPreferenceChange(text: String) {
        _userPreference.value = text
        if (_state.value is RecommendationState.Success || _state.value is RecommendationState.Error) {
            _state.value = RecommendationState.Idle
        }
    }

    fun generateRecommendation(kosts: List<Kost>) {
        val preference = _userPreference.value.trim()
        if (preference.isEmpty()) {
            _state.value = RecommendationState.Error("Masukkan preferensi kost terlebih dahulu.")
            return
        }
        if (kosts.isEmpty()) {
            _state.value = RecommendationState.Error("Belum ada data kost untuk dianalisis.")
            return
        }

        val apiKey = PlatformConfig.geminiApiKey
        if (apiKey.isBlank()) {
            _state.value = RecommendationState.Error(
                "API key Gemini belum dikonfigurasi.\nTambahkan GEMINI_API_KEY ke local.properties."
            )
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
                onSuccess = { RecommendationState.Success(it) },
                onFailure = { RecommendationState.Error(it.message ?: "Terjadi kesalahan.") }
            )
        }
    }

    fun reset() {
        _state.value = RecommendationState.Idle
        _userPreference.value = ""
    }

    fun dispose() {
        scope.cancel()
    }
}
