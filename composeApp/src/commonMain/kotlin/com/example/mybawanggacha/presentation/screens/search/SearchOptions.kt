package com.example.mybawanggacha.presentation.screens.search

internal data class SearchOption(
    val value: String,
    val label: String
)

internal val ANIME_TYPES = listOf(
    SearchOption("tv", "TV"),
    SearchOption("movie", "Movie"),
    SearchOption("ova", "OVA"),
    SearchOption("special", "Special"),
    SearchOption("ona", "ONA"),
    SearchOption("music", "Music"),
    SearchOption("cm", "CM"),
    SearchOption("pv", "PV"),
    SearchOption("tv_special", "TV Special")
)

internal val MANGA_TYPES = listOf(
    SearchOption("manga", "Manga"),
    SearchOption("novel", "Novel"),
    SearchOption("lightnovel", "Light Novel"),
    SearchOption("oneshot", "One-shot"),
    SearchOption("doujin", "Doujin"),
    SearchOption("manhwa", "Manhwa"),
    SearchOption("manhua", "Manhua")
)

internal val ANIME_STATUSES = listOf(
    SearchOption("airing", "Airing"),
    SearchOption("complete", "Complete"),
    SearchOption("upcoming", "Upcoming")
)

internal val MANGA_STATUSES = listOf(
    SearchOption("publishing", "Publishing"),
    SearchOption("complete", "Complete"),
    SearchOption("hiatus", "Hiatus"),
    SearchOption("discontinued", "Discontinued"),
    SearchOption("upcoming", "Upcoming")
)

internal val ANIME_RATINGS = listOf(
    SearchOption("g", "G - All Ages"),
    SearchOption("pg", "PG - Children"),
    SearchOption("pg13", "PG-13"),
    SearchOption("r17", "R - 17+"),
    SearchOption("r", "R+ - Mild Nudity"),
    SearchOption("rx", "Rx - Hentai")
)

internal val ANIME_ORDER_BY = listOf(
    SearchOption("mal_id", "MAL ID"),
    SearchOption("title", "Title"),
    SearchOption("start_date", "Start Date"),
    SearchOption("end_date", "End Date"),
    SearchOption("episodes", "Episodes"),
    SearchOption("score", "Score"),
    SearchOption("scored_by", "Scored By"),
    SearchOption("rank", "Rank"),
    SearchOption("popularity", "Popularity"),
    SearchOption("members", "Members")
)

internal val MANGA_ORDER_BY = listOf(
    SearchOption("mal_id", "MAL ID"),
    SearchOption("title", "Title"),
    SearchOption("start_date", "Start Date"),
    SearchOption("end_date", "End Date"),
    SearchOption("chapters", "Chapters"),
    SearchOption("volumes", "Volumes"),
    SearchOption("score", "Score"),
    SearchOption("scored_by", "Scored By"),
    SearchOption("rank", "Rank"),
    SearchOption("popularity", "Popularity")
)

internal val SORT_OPTIONS = listOf(
    SearchOption("desc", "Desc"),
    SearchOption("asc", "Asc")
)

internal fun defaultDropdownLabel(label: String): String {
    return when (label.trim().lowercase()) {
        SearchText.typeLabel.lowercase() -> SearchText.anyTypeLabel
        SearchText.statusLabel.lowercase() -> SearchText.anyStatusLabel
        SearchText.ratingLabel.lowercase() -> SearchText.anyRatingLabel
        "order by", "orderby", "order_by" -> SearchText.defaultOrderByLabel
        SearchText.sortLabel.lowercase() -> SearchText.defaultSortLabel
        else -> SearchText.defaultDropdownLabel
    }
}
