package com.example.Feelia.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class Note(
    val id: Long = 0,
    val content: String,
    val emotion: Emotion = Emotion.NEUTRAL,
    val isPinned: Boolean = false,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now()
) {
    val preview: String
        get() = if (content.length > 120) content.take(120) + "..." else content

    val isEmpty: Boolean
        get() = content.isBlank()
}
// bagian yang udh disesuain Core architecture
enum class Emotion(
    val displayName: String,
    val emoji: String,
    val colorHex: Long
) {
    HAPPY("Senang", "😊", 0xFFFFF9C4),
    SAD("Sedih", "😢", 0xFFBBDEFB),
    ANXIOUS("Cemas", "😰", 0xFFE1BEE7),
    ANGRY("Marah", "😠", 0xFFFFCDD2),
    NEUTRAL("Netral", "😐", 0xFFE8F5E9);

    companion object {
        fun fromString(value: String): Emotion {
            return entries.find { it.name == value } ?: NEUTRAL
        }
    }
}