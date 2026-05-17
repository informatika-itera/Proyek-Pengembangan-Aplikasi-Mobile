package com.dailybliss.app.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class Moment(
    val id: Long = 0,
    val title: String,
    val content: String,
    val imageUrl: String? = null,
    val isPinned: Boolean = false,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now()
) {
    val preview: String
        get() {
            return if (content.startsWith("{\"blocks\":")) {
                // Simple extraction of first text block for preview
                try {
                    val textPart = content.split("\"text\":\"").getOrNull(1)?.split("\"")?.getOrNull(0) ?: ""
                    if (textPart.length > 120) "${textPart.take(120)}..." else textPart
                } catch (e: Exception) {
                    if (content.length > 120) "${content.take(120)}..." else content
                }
            } else {
                if (content.length > 120) "${content.take(120)}..." else content
            }
        }
}

