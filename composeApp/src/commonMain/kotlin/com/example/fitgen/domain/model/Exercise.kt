package com.example.fitgen.domain.model

/**
 * Merepresentasikan satu gerakan/latihan dalam sesi workout.
 *
 * @property nama      Nama gerakan, misalnya "Push Up", "Squat", "Deadlift"
 * @property sets      Jumlah set yang dilakukan
 * @property reps      Jumlah repetisi per set
 * @property beban     Beban yang digunakan dalam kilogram (0.0 jika tanpa beban)
 */
data class Exercise(
    val nama: String,
    val sets: Int,
    val reps: Int,
    val beban: Double = 0.0
) {
    /** Representasi ringkas untuk ditampilkan di UI, misalnya "3x12 @ 20.0 kg" */
    val ringkasan: String
        get() = if (beban > 0.0) "${sets}x${reps} @ ${beban} kg" else "${sets}x${reps}"

    /** Validasi dasar: nama tidak boleh kosong dan nilai numerik harus positif */
    val isValid: Boolean
        get() = nama.isNotBlank() && sets > 0 && reps > 0 && beban >= 0.0
}
