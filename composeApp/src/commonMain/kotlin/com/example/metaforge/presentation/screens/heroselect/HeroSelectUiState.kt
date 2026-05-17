package com.example.metaforge.presentation.screens.heroselect

import com.example.metaforge.domain.model.Hero
import com.example.metaforge.domain.model.HeroRole

sealed interface HeroSelectUiState {
    data object Loading : HeroSelectUiState
    data class Ready(
        val allHeroes: List<Hero>,
        val filteredHeroes: List<Hero>,
        val selectedRole: HeroRole? = null,
        val searchQuery: String = "",
        val pickedHeroNames: Set<String> = emptySet()
    ) : HeroSelectUiState
    data class Error(val message: String) : HeroSelectUiState
}