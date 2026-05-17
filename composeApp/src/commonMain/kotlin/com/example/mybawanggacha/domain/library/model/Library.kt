package com.example.mybawanggacha.domain.library.model

import kotlin.time.Clock
import kotlin.time.Instant

data class LibraryEntry(
    val id: Long = 0,
    val mediaId: Int,
    val mediaType: MediaType,
    val title: String,
    val imageUrl: String? = null,
    val status: LibraryStatus = LibraryStatus.PlanToWatch,
    val progress: UserProgress = UserProgress(),
    val userScore: UserScore? = null,
    val notes: String? = null,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now()
)

enum class MediaType(val storageKey: String, val displayName: String) {
    Anime("ANIME", "Anime"),
    Manga("MANGA", "Manga");

    companion object {
        fun fromStorageKey(value: String?): MediaType {
            return entries.firstOrNull { it.storageKey.equals(value, ignoreCase = true) }
                ?: Anime
        }
    }
}

enum class LibraryStatus(val storageKey: String, val animeLabel: String, val mangaLabel: String) {
    PlanToWatch("PLAN_TO_WATCH", "Rencana Tonton", "Rencana Baca"),
    Watching("WATCHING", "Sedang Ditonton", "Sedang Dibaca"),
    Completed("COMPLETED", "Sudah Ditonton", "Sudah Dibaca"),
    OnHold("ON_HOLD", "Pending", "Pending"),
    Dropped("DROPPED", "Dropped", "Dropped");

    val defaultLabel: String
        get() = animeLabel

    fun labelFor(mediaType: MediaType): String {
        return when (mediaType) {
            MediaType.Anime -> animeLabel
            MediaType.Manga -> mangaLabel
        }
    }

    companion object {
        fun fromStorageKey(value: String?): LibraryStatus {
            return entries.firstOrNull { it.storageKey.equals(value, ignoreCase = true) }
                ?: PlanToWatch
        }
    }
}

data class UserProgress(
    val current: Int = 0,
    val total: Int? = null
) {
    init {
        require(current >= 0) { "Progress tidak boleh negatif" }
        require(total == null || total >= 0) { "Total tidak boleh negatif" }
        require(total == null || current <= total) { "Progress tidak boleh lebih besar dari total" }
    }

    fun format(): String {
        return total?.let { "$current/$it" } ?: current.toString()
    }
}

data class UserScore(val value: Int) {
    init {
        require(value in 1..10) { "Score harus berada di rentang 1 sampai 10" }
    }
}
