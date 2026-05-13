package com.example.fitgen.presentation.screens.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitgen.domain.model.Exercise
import com.example.fitgen.domain.model.WorkoutLog
import com.example.fitgen.domain.usecase.LogWorkoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

// ─────────────────────────────────────────────────────────────────────────────
// State untuk form input satu gerakan
// ─────────────────────────────────────────────────────────────────────────────

data class ExerciseFormState(
    val nama: String = "",
    val sets: String = "",
    val reps: String = "",
    val beban: String = "",
    // Error messages — null = valid
    val namaError: String? = null,
    val setsError: String? = null,
    val repsError: String? = null,
    val bebanError: String? = null
) {
    val isAnyError: Boolean
        get() = namaError != null || setsError != null ||
                repsError != null || bebanError != null
}

// ─────────────────────────────────────────────────────────────────────────────
// State seluruh layar Add Workout
// ─────────────────────────────────────────────────────────────────────────────

data class AddWorkoutUiState(
    val tanggal: LocalDate = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).date,
    val catatan: String = "",
    val gerakanList: List<Exercise> = emptyList(),
    val exerciseForm: ExerciseFormState = ExerciseFormState(),
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val saveError: String? = null,
    // Validasi level sesi (bukan per-gerakan)
    val sessionError: String? = null
)

// ─────────────────────────────────────────────────────────────────────────────
// ViewModel
// ─────────────────────────────────────────────────────────────────────────────

class AddWorkoutViewModel(
    private val logWorkoutUseCase: LogWorkoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddWorkoutUiState())
    val uiState: StateFlow<AddWorkoutUiState> = _uiState.asStateFlow()

    // ── Form field updates ──

    fun onNamaChange(value: String) {
        _uiState.update { state ->
            state.copy(
                exerciseForm = state.exerciseForm.copy(
                    nama = value,
                    namaError = if (value.isBlank()) "Nama gerakan wajib diisi" else null
                )
            )
        }
    }

    fun onSetsChange(value: String) {
        _uiState.update { state ->
            state.copy(
                exerciseForm = state.exerciseForm.copy(
                    sets = value,
                    setsError = validatePositiveInt(value, "Sets")
                )
            )
        }
    }

    fun onRepsChange(value: String) {
        _uiState.update { state ->
            state.copy(
                exerciseForm = state.exerciseForm.copy(
                    reps = value,
                    repsError = validatePositiveInt(value, "Reps")
                )
            )
        }
    }

    fun onBebanChange(value: String) {
        _uiState.update { state ->
            state.copy(
                exerciseForm = state.exerciseForm.copy(
                    beban = value,
                    bebanError = validateNonNegativeDouble(value)
                )
            )
        }
    }

    fun onCatatanChange(value: String) {
        _uiState.update { it.copy(catatan = value) }
    }

    fun onTanggalChange(date: LocalDate) {
        _uiState.update { it.copy(tanggal = date) }
    }

    // ── Tambah gerakan ke daftar ──

    fun addExercise() {
        val form = _uiState.value.exerciseForm
        val validatedForm = validateForm(form)

        _uiState.update { it.copy(exerciseForm = validatedForm) }

        if (validatedForm.isAnyError) return

        val newExercise = Exercise(
            nama  = form.nama.trim(),
            sets  = form.sets.trim().toInt(),
            reps  = form.reps.trim().toInt(),
            beban = form.beban.trim().toDoubleOrNull() ?: 0.0
        )

        _uiState.update { state ->
            state.copy(
                gerakanList = state.gerakanList + newExercise,
                exerciseForm = ExerciseFormState(), // reset form
                sessionError = null
            )
        }
    }

    // ── Hapus gerakan dari daftar ──

    fun removeExercise(index: Int) {
        _uiState.update { state ->
            state.copy(gerakanList = state.gerakanList.toMutableList().also { it.removeAt(index) })
        }
    }

    // ── Simpan seluruh sesi latihan ──

    fun saveWorkout() {
        val state = _uiState.value

        if (state.gerakanList.isEmpty()) {
            _uiState.update { it.copy(sessionError = "Tambahkan minimal 1 gerakan sebelum menyimpan") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, saveError = null, sessionError = null) }

            val workout = WorkoutLog(
                tanggal  = state.tanggal,
                gerakan  = state.gerakanList,
                catatan  = state.catatan
            )

            val result = logWorkoutUseCase(workout)

            _uiState.update {
                if (result.isSuccess) {
                    it.copy(isSaving = false, saveSuccess = true)
                } else {
                    it.copy(
                        isSaving = false,
                        saveError = result.exceptionOrNull()?.message ?: "Gagal menyimpan sesi"
                    )
                }
            }
        }
    }

    fun clearSaveError() {
        _uiState.update { it.copy(saveError = null) }
    }

    // ── Private helpers ──

    private fun validateForm(form: ExerciseFormState): ExerciseFormState {
        return form.copy(
            namaError  = if (form.nama.isBlank()) "Nama gerakan wajib diisi" else null,
            setsError  = validatePositiveInt(form.sets, "Sets"),
            repsError  = validatePositiveInt(form.reps, "Reps"),
            bebanError = validateNonNegativeDouble(form.beban)
        )
    }

    private fun validatePositiveInt(value: String, fieldName: String): String? {
        if (value.isBlank()) return "$fieldName wajib diisi"
        val intVal = value.toIntOrNull() ?: return "$fieldName harus berupa angka bulat"
        if (intVal <= 0) return "$fieldName harus lebih dari 0"
        return null
    }

    private fun validateNonNegativeDouble(value: String): String? {
        if (value.isBlank()) return null  // beban boleh kosong (default 0)
        val dblVal = value.toDoubleOrNull() ?: return "Beban harus berupa angka"
        if (dblVal < 0) return "Beban tidak boleh negatif"
        return null
    }
}
