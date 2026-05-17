package com.example.metaforge.presentation.screens.heroselect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.metaforge.domain.model.Hero
import com.example.metaforge.domain.model.HeroRole
import com.example.metaforge.domain.repository.DraftRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HeroSelectViewModel(
    private val draftRepository: DraftRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HeroSelectUiState>(HeroSelectUiState.Loading)
    val uiState: StateFlow<HeroSelectUiState> = _uiState.asStateFlow()

    private var allHeroes: List<Hero> = emptyList()
    private val _selectedRole = MutableStateFlow<HeroRole?>(null)
    private val _searchQuery = MutableStateFlow("")
    private var currentPickedHeroes: Set<String> = emptySet()

    init { observeDraftAndHeroes() }

    private fun observeDraftAndHeroes() {
        viewModelScope.launch {
            combine(
                draftRepository.getAllHeroes(),
                draftRepository.getDraftState()
            ) { heroes, draftState ->
                allHeroes = heroes
                currentPickedHeroes = (draftState.allySlots + draftState.enemySlots + draftState.allyBans + draftState.enemyBans)
                    .filterNotNull().map { it.name }.toSet()
                updateFilteredList()
            }.collect {}
        }
    }

    fun filterByRole(role: HeroRole?) {
        _selectedRole.value = role
        updateFilteredList()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        updateFilteredList()
    }

    private fun updateFilteredList() {
        val role = _selectedRole.value
        val query = _searchQuery.value

        var filtered = if (role == null) allHeroes else allHeroes.filter { it.role == role }
        if (query.isNotEmpty()) {
            filtered = filtered.filter { it.name.contains(query, ignoreCase = true) }
        }

        _uiState.value = HeroSelectUiState.Ready(
            allHeroes = allHeroes,
            filteredHeroes = filtered,
            selectedRole = role,
            searchQuery = query,
            pickedHeroNames = currentPickedHeroes
        )
    }

    fun pickHero(slotIndex: Int, isAlly: Boolean, isBan: Boolean, hero: Hero) {
        viewModelScope.launch {
            if (isBan) {
                draftRepository.banHero(slotIndex, isAlly, hero)
            } else {
                draftRepository.pickHero(slotIndex, isAlly, hero)
            }
        }
    }
}