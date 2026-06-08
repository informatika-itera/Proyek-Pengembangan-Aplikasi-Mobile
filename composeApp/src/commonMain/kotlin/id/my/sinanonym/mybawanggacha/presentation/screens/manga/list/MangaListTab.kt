package id.my.sinanonym.mybawanggacha.presentation.screens.manga.list

enum class MangaListTab(val label: String) {
    TopManga("Top Manga"),
    Popular("Populer"),
    Recommendations("Rekomendasi")
}

internal fun MangaListTab.contentTitle(): String {
    return when (this) {
        MangaListTab.TopManga -> "Top Manga"
        MangaListTab.Popular -> "Manga Populer"
        MangaListTab.Recommendations -> "Rekomendasi Manga"
    }
}

internal fun MangaListTab.contentSubtitle(): String {
    return when (this) {
        MangaListTab.TopManga -> "Manga dengan ranking tinggi dari katalog MyAnimeList."
        MangaListTab.Popular -> "Manga yang paling banyak dilihat komunitas MyAnimeList."
        MangaListTab.Recommendations -> "Rekomendasi komunitas dari Jikan/MyAnimeList."
    }
}
