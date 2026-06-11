package id.my.sinanonym.mybawanggacha.presentation.screens.gacha

import id.my.sinanonym.mybawanggacha.domain.gacha.model.GachaHistoryEntry
import id.my.sinanonym.mybawanggacha.domain.gacha.model.GachaPreference
import id.my.sinanonym.mybawanggacha.domain.gacha.model.GachaResultItem
import id.my.sinanonym.mybawanggacha.domain.search.model.SearchFilterOption

data class GachaUiState(
    val preference: GachaPreference = GachaPreference(),
    val availableGenres: List<SearchFilterOption> = emptyList(),
    val isGenreLoading: Boolean = false,
    val genreErrorMessage: String? = null,
    val result: GachaResultItem? = null,
    val history: List<GachaHistoryEntry> = emptyList(),
    val isLoading: Boolean = false,
    val isRolling: Boolean = false,
    val canSkipRoll: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null
)
