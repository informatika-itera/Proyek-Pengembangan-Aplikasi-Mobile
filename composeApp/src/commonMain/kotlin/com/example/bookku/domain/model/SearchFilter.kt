package com.example.bookku.domain.model

enum class NoteSortBy(val displayName: String) {
    TITLE_ASC("Judul (A-Z)"),
    TITLE_DESC("Judul (Z-A)"),
    CREATED_ASC("Dibuat (Lama)"),
    CREATED_DESC("Dibuat (Baru)"),
    UPDATED_ASC("Diupdate (Lama)"),
    UPDATED_DESC("Diupdate (Baru)")
}

data class SearchFilter(
    val query: String = "",
    val category: BookGenre? = null,
    val minRating: Float? = null,
    val maxRating: Float? = null,
    val readingStatus: ReadingStatus? = null,
    val sortBy: NoteSortBy = NoteSortBy.UPDATED_DESC,
    val isPinnedOnly: Boolean = false
) {
    fun isActive(): Boolean = 
        query.isNotBlank() || 
        category != null || 
        minRating != null || 
        readingStatus != null ||
        isPinnedOnly
}
