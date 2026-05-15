package com.example.mybawanggacha.presentation.screens.anime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybawanggacha.data.remote.api.JikanService
import com.example.mybawanggacha.data.remote.dto.AnimeDetailData
import com.example.mybawanggacha.data.remote.dto.AnimeEpisodeDto
import com.example.mybawanggacha.data.remote.dto.AnimeRelationEntryDto
import com.example.mybawanggacha.data.remote.dto.RelationEntryPreviewDto
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AnimeDetailUiState {
    data object Loading : AnimeDetailUiState
    data class Success(
        val anime: AnimeDetailData,
        val episodes: List<AnimeEpisodeDto> = emptyList(),
        val relationPreviews: Map<String, RelationEntryPreviewDto> = emptyMap()
    ) : AnimeDetailUiState
    data class Error(val message: String) : AnimeDetailUiState
}

class AnimeDetailViewModel(
    private val jikanService: JikanService
) : ViewModel() {

    private val _uiState = MutableStateFlow<AnimeDetailUiState>(AnimeDetailUiState.Loading)
    val uiState: StateFlow<AnimeDetailUiState> = _uiState.asStateFlow()

    fun fetchAnimeDetail(malId: Int) {
        viewModelScope.launch {
            _uiState.value = AnimeDetailUiState.Loading
            try {
                val anime = jikanService.fetchAnimeFullDetail(malId).data
                val episodes = runCatching {
                    jikanService.fetchAnimeEpisodes(malId).data
                }.getOrDefault(emptyList())

                _uiState.value = AnimeDetailUiState.Success(
                    anime = anime,
                    episodes = episodes
                )
                val relationPreviews = fetchRelationPreviews(
                    entries = anime.relations.flatMap { it.entry }
                )

                val currentState = _uiState.value
                if (currentState is AnimeDetailUiState.Success && currentState.anime.mal_id == anime.mal_id) {
                    _uiState.value = currentState.copy(relationPreviews = relationPreviews)
                }
            } catch (e: Exception) {
                _uiState.value = AnimeDetailUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
    private suspend fun fetchRelationPreviews(
        entries: List<AnimeRelationEntryDto>
    ): Map<String, RelationEntryPreviewDto> {
        val previews = mutableMapOf<String, RelationEntryPreviewDto>()

        entries
            .distinctBy { it.previewKey() }
            .forEachIndexed { index, entry ->
                if (index > 0) delay(360)

                runCatching {
                    jikanService.fetchRelationEntryPreview(
                        id = entry.mal_id,
                        type = entry.type
                    ).data
                }.getOrNull()?.let { preview ->
                    previews[entry.previewKey()] = preview
                }
            }

        return previews
    }
}

private fun AnimeRelationEntryDto.previewKey(): String {
    return "${type.orEmpty().lowercase()}:$mal_id"
}

