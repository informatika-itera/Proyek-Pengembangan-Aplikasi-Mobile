package com.example.metaforge.presentation.screens.heroselect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.metaforge.domain.model.Hero
import com.example.metaforge.domain.model.HeroRole
import com.example.metaforge.domain.repository.DraftRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HeroSelectViewModel(
    private val draftRepository: DraftRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<HeroSelectUiState>(HeroSelectUiState.Loading)
    val uiState: StateFlow<HeroSelectUiState> = _uiState.asStateFlow()

    private var allHeroes: List<Hero> = emptyList()

    init { loadHeroes() }

    private fun loadHeroes() {
        viewModelScope.launch {
            draftRepository.getAllHeroes().collect { heroes ->
                allHeroes = heroes
                _uiState.value = HeroSelectUiState.Ready(
                    allHeroes = heroes,
                    filteredHeroes = heroes
                )
            }
        }
    }

    fun filterByRole(role: HeroRole?) {
        val current = _uiState.value
            as? HeroSelectUiState.Ready ?: return
        val filtered = if (role == null) allHeroes
        else allHeroes.filter { it.role == role }
        _uiState.value = current.copy(
            filteredHeroes = filtered,
            selectedRole = role,
            searchQuery = ""
        )
    }

    fun onSearchQueryChange(query: String) {
        val current = _uiState.value
            as? HeroSelectUiState.Ready ?: return
        val base = if (current.selectedRole == null) allHeroes
        else allHeroes.filter { it.role == current.selectedRole }
        val filtered = if (query.isEmpty()) base
        else base.filter {
            it.name.contains(query, ignoreCase = true)
        }
        _uiState.value = current.copy(
            filteredHeroes = filtered,
            searchQuery = query
        )
    }

    fun pickHero(slotIndex: Int, isAlly: Boolean, hero: Hero) {
        viewModelScope.launch {
            draftRepository.pickHero(slotIndex, isAlly, hero)
        }
    }
}