package com.example.mybawanggacha.presentation.screens.anime.list

import com.example.mybawanggacha.domain.anime.model.AnimeSeasonPeriod

enum class AnimeListTab(val label: String) {
    CurrentSeason("Musim Ini"),
    SeasonArchive("Arsip Musim"),
    Upcoming("Akan Tayang"),
    TopAnime("Top Anime"),
    Recommendations("Rekomendasi")
}

internal fun AnimeListTab.contentTitle(
    currentSeasonPeriod: AnimeSeasonPeriod,
    selectedSeasonPeriod: AnimeSeasonPeriod
): String {
    return when (this) {
        AnimeListTab.CurrentSeason -> currentSeasonPeriod.displayLabel
        AnimeListTab.SeasonArchive -> selectedSeasonPeriod.displayLabel
        AnimeListTab.Upcoming -> "Akan Tayang"
        AnimeListTab.TopAnime -> "Top Anime"
        AnimeListTab.Recommendations -> "Rekomendasi Anime"
    }
}

internal fun AnimeListTab.contentSubtitle(
    currentSeasonPeriod: AnimeSeasonPeriod,
    selectedSeasonPeriod: AnimeSeasonPeriod
): String {
    return when (this) {
        AnimeListTab.CurrentSeason -> "Anime yang sedang tayang pada ${currentSeasonPeriod.displayLabel}."
        AnimeListTab.SeasonArchive -> "Arsip anime dari ${selectedSeasonPeriod.displayLabel}."
        AnimeListTab.Upcoming -> "Anime musim mendatang yang cocok masuk Rencana Tonton."
        AnimeListTab.TopAnime -> "Anime dengan ranking tinggi dari katalog MyAnimeList."
        AnimeListTab.Recommendations -> "Rekomendasi komunitas dari Jikan/MyAnimeList."
    }
}