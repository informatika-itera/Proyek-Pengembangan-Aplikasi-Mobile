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

