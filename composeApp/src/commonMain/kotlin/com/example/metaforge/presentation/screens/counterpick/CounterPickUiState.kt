package com.example.metaforge.presentation.screens.counterpick

import com.example.metaforge.domain.model.Hero
import com.example.metaforge.domain.model.HeroRecommendation

sealed interface CounterPickUiState {
    data object Idle : CounterPickUiState
    data object Analyzing : CounterPickUiState
    data class Success(
        val targetHero: Hero,
        val counters: List<HeroRecommendation>
    ) : CounterPickUiState
    data class Error(val message: String) : CounterPickUiState
}