package com.example.hujjah.presentation.screens.quran

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hujjah.data.local.datastore.UserPreferences
import com.example.hujjah.domain.model.islamic.SurahItem
import com.example.hujjah.domain.model.islamic.VerseItem
import com.example.hujjah.domain.repository.hujjah.HujjahRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class QuranUiState(
    val surahs: List<SurahItem> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

data class SurahDetailUiState(
    val number: Int = 0,
    val name: String = "",
    val verses: List<VerseItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@OptIn(FlowPreview::class)
class QuranViewModel(
    private val hujjahRepository: HujjahRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _allSurahs = MutableStateFlow<List<SurahItem>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)
    private val _filteredSurahs = MutableStateFlow<List<SurahItem>>(emptyList())

    val uiState: StateFlow<QuranUiState> = combine(
        _searchQuery,
        _filteredSurahs,
        _isLoading,
        _error
    ) { query, filtered, isLoading, error ->
        QuranUiState(
            surahs = filtered,
            searchQuery = query,
            isLoading = isLoading,
            error = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = QuranUiState()
    )

    private val _detailUiState = MutableStateFlow(SurahDetailUiState())
    val detailUiState: StateFlow<SurahDetailUiState> = _detailUiState.asStateFlow()

    val lastReadLocation: StateFlow<String> = userPreferences.lastReadQuranLocation
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    init {
        fetchSurahs(forceRefresh = false)

        viewModelScope.launch {
            combine(
                _allSurahs,
                _searchQuery.debounce(300).distinctUntilChanged()
            ) { surahs, query ->
                if (query.isBlank()) {
                    surahs
                } else {
                    surahs.filter {
                        it.name.contains(query, ignoreCase = true) ||
                        it.translation.contains(query, ignoreCase = true) ||
                        it.asma.contains(query, ignoreCase = true)
                    }
                }
            }.collect { filtered ->
                _filteredSurahs.value = filtered
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun fetchSurahs(forceRefresh: Boolean = false) {
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                hujjahRepository.getSurahs(forceRefresh).collect { surahsList ->
                    _allSurahs.value = surahsList
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = "Koneksi lambat. Menampilkan data luring."
                _isLoading.value = false
            }
        }
    }

    fun fetchSurahDetail(surahNumber: Int, surahName: String) {
        _detailUiState.value = SurahDetailUiState(number = surahNumber, name = surahName, isLoading = true)
        viewModelScope.launch {
            try {
                hujjahRepository.getSurahDetail(surahNumber, surahName, forceRefresh = false).collect { verses ->
                    _detailUiState.value = SurahDetailUiState(
                        number = surahNumber,
                        name = surahName,
                        verses = verses,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _detailUiState.value = SurahDetailUiState(
                    number = surahNumber,
                    name = surahName,
                    verses = emptyList(),
                    isLoading = false,
                    error = "Koneksi lambat. Menampilkan data luring."
                )
            }
        }
    }

    fun saveLastRead(location: String) {
        viewModelScope.launch {
            userPreferences.setLastReadQuranLocation(location)
        }
    }

    fun addReadingTime(seconds: Int) {
        viewModelScope.launch {
            userPreferences.addReadingDuration(seconds)
            userPreferences.updateStreak()
        }
    }
}
