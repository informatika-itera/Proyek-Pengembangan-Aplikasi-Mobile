package com.example.pantaujompo.presentation.screens.profil

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.math.roundToInt

class ProfilViewModel : ViewModel() {
    // State form input
    var usia by mutableStateOf("20")
    var beratKg by mutableStateOf("55")
    var tinggiCm by mutableStateOf("160")

    // Perhitungan BMI Real-time
    val bmiScore: Double
        get() {
            val w = beratKg.toDoubleOrNull() ?: 0.0
            val h = tinggiCm.toDoubleOrNull() ?: 0.0
            if (h == 0.0) return 0.0
            val hMeter = h / 100
            // Membulatkan 1 angka di belakang koma
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
            // TODO (Sprint Berikutnya): Simpan ke DataStore/Database beneran
            // Untuk Sprint 2, UI sudah interaktif dan update state
            println("Data Profil Tersimpan: $usia thn, $beratKg kg, $tinggiCm cm")
        }
    }
}