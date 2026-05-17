package com.example.mybawanggacha.presentation.screens.anime.detail.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Theaters
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.mybawanggacha.domain.anime.model.AnimeDetail
import com.example.mybawanggacha.domain.anime.model.AnimeEpisode
import com.example.mybawanggacha.domain.anime.model.AnimeRelationEntry
import com.example.mybawanggacha.presentation.components.MBGSideRailItem

internal enum class AnimeDetailSection(
    val key: String,
    val label: String,
    val icon: ImageVector
) {
    Overview("overview", "Overview", Icons.Default.Theaters),
    Synopsis("synopsis", "Sinopsis", Icons.Default.Article),
    Episodes("episodes", "Episode", Icons.Default.PlaylistPlay),
    Info("info", "Info", Icons.Default.Info),
    Media("media", "Media", Icons.Default.Movie),
    Relations("relations", "Relations", Icons.Default.Link),
    ThemeSongs("theme_songs", "Theme Songs", Icons.Default.MusicNote)
}

internal fun animeDetailRailItems(): List<MBGSideRailItem> = AnimeDetailSection.entries.map { section ->
    MBGSideRailItem(
        key = section.key,
        label = section.label,
        icon = section.icon
    )
}

@Composable
internal fun AnimeDetailContent(
    anime: AnimeDetail,
    episodes: List<AnimeEpisode>,
    selectedSection: AnimeDetailSection,
    onEpisodeWatchedChange: (Int, Boolean) -> Unit,
    onRelationEntryClick: (AnimeRelationEntry) -> Unit
) {
    when (selectedSection) {
        AnimeDetailSection.Overview -> AnimeOverviewSection(anime)
        AnimeDetailSection.Synopsis -> AnimeSynopsisSection(anime)
        AnimeDetailSection.Episodes -> AnimeEpisodeListSection(
            anime = anime,
            episodes = episodes,
            onEpisodeWatchedChange = onEpisodeWatchedChange
        )
        AnimeDetailSection.Info -> AnimeInfoSection(anime)
        AnimeDetailSection.Media -> AnimeMediaSection(anime)
        AnimeDetailSection.Relations -> AnimeRelationsSection(
            anime = anime,
            onEntryClick = onRelationEntryClick
        )
        AnimeDetailSection.ThemeSongs -> AnimeThemeSongsSection(anime)
    }
}