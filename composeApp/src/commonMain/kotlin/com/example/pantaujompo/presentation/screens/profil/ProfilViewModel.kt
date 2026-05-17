package com.example.pantaujompo.presentation.screens.profil

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pantaujompo.data.local.datastore.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.math.roundToInt

class ProfilViewModel(
    private val userPreferences: UserPreferences
) : ViewModel() {

    var nama by mutableStateOf("Pradana Figo Ariasya")
    var usia by mutableStateOf("22")
    var beratKg by mutableStateOf("63")
    var tinggiCm by mutableStateOf("165")

    init {
        loadProfileFromStorage()
    }

    private fun loadProfileFromStorage() {
        viewModelScope.launch {
            nama = userPreferences.userName.first()
            usia = userPreferences.userAge.first()
            beratKg = userPreferences.userWeight.first()
            tinggiCm = userPreferences.userHeight.first()
        }
    }

    val bmiScore: Double
        get() {
            val w = beratKg.toDoubleOrNull() ?: 0.0
            val h = tinggiCm.toDoubleOrNull() ?: 0.0
            if (h == 0.0) return 0.0
            val hMeter = h / 100
            return ((w / hMeter.pow(2)) * 10.0).roundToInt() / 10.0
        }

    val bmiCategory: String
        get() {
            val score = bmiScore
            return when {
                score == 0.0 -> "-"
                score < 18.5 -> "Kurus"
                score in 18.5..24.9 -> "Normal"
                score in 25.0..29.9 -> "Gemuk"
                else -> "Obesitas"
            }
        }

    fun saveProfile() {
        viewModelScope.launch {
            userPreferences.saveProfile(nama, usia, beratKg, tinggiCm)
        }
    }
}