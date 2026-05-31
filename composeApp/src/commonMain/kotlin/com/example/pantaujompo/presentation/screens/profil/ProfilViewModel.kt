package com.example.pantaujompo.presentation.screens.profil

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pantaujompo.data.local.datastore.UserPreferences
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.math.roundToInt

class ProfilViewModel(
    private val userPreferences: UserPreferences
) : ViewModel() {

    var nama by mutableStateOf("")
    var usia by mutableStateOf("")
    var beratKg by mutableStateOf("")
    var tinggiCm by mutableStateOf("")
    var gender by mutableStateOf("Laki-laki")

    init {
        // PAKE collectLatest BIAR DATANYA UPDATE REAL-TIME!
        viewModelScope.launch {
            userPreferences.userName.collectLatest { nama = it }
        }
        viewModelScope.launch {
            userPreferences.userAge.collectLatest { usia = if (it != 0) it.toString() else "" }
        }
        viewModelScope.launch {
            userPreferences.userWeight.collectLatest { beratKg = if (it != 0f) it.toString() else "" }
        }
        viewModelScope.launch {
            userPreferences.userHeight.collectLatest { tinggiCm = if (it != 0f) it.toString() else "" }
        }
        viewModelScope.launch {
            userPreferences.userGender.collectLatest { gender = it }
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
            userPreferences.saveProfile(
                name = nama,
                age = usia.toIntOrNull() ?: 0,
                weight = beratKg.toFloatOrNull() ?: 0f,
                height = tinggiCm.toFloatOrNull() ?: 0f,
                gender = gender
            )
        }
    }
}