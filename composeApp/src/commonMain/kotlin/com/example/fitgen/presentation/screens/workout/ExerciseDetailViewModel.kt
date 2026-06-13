package com.example.fitgen.presentation.screens.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitgen.domain.model.CustomRoutine
import com.example.fitgen.domain.model.RoutineExercise
import com.example.fitgen.domain.repository.CustomRoutineRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExerciseDetailViewModel(
    private val repository: CustomRoutineRepository
) : ViewModel() {

    val customRoutines: StateFlow<List<CustomRoutine>> = repository.getAllRoutines()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addExerciseToRoutine(routineId: Long, name: String, bodyPart: String, gifUrl: String, instructions: String) {
        viewModelScope.launch {
            val exercise = RoutineExercise(
                routineId = routineId,
                name = name,
                bodyPart = bodyPart,
                gifUrl = gifUrl,
                instructions = instructions
            )
            repository.addExerciseToRoutine(routineId, exercise)
        }
    }
}
