package com.example.fitgen.domain.model

/**
 * Merepresentasikan profil pengguna FitGen.
 * Digunakan untuk personalisasi saran AI dan metrik kesehatan.
 *
 * @property name      Nama pengguna
 * @property age       Umur dalam tahun
 * @property heightCm  Tinggi badan dalam centimeter
 * @property weightKg  Berat badan dalam kilogram
 * @property goal      Tujuan utama (misal: "Menurunkan Berat Badan", "Membangun Otot")
 */
data class UserProfile(
    val name: String = "",
    val age: Int = 0,
    val heightCm: Double = 0.0,
    val weightKg: Double = 0.0,
    val goal: String = ""
) {
    val isComplete: Boolean
        get() = name.isNotBlank() && age > 0 && heightCm > 0.0 && weightKg > 0.0 && goal.isNotBlank()
}
