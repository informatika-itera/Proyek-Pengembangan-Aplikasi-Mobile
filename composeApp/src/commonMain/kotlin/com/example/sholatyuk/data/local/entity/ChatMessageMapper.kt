package com.example.sholatyuk.data.local.entity

import com.example.sholatyuk.data.local.ChatHistoryEntity
import com.example.sholatyuk.domain.model.ChatMessage
import com.example.sholatyuk.domain.model.MessageRole
import kotlinx.datetime.Instant

// ── ChatHistoryEntity → Domain ────────────────────────────────────

fun ChatHistoryEntity.toDomain(): ChatMessage = ChatMessage(
    id        = id,
    content   = content,
    role      = when (role) {
        "USER"      -> MessageRole.USER
        else        -> MessageRole.ASSISTANT
    },
    timestamp = Instant.fromEpochMilliseconds(timestamp),
    isError   = is_error != 0L
)

fun List<ChatHistoryEntity>.toDomainList(): List<ChatMessage> = map { it.toDomain() }

// ── Domain → Insert params ────────────────────────────────────────

fun ChatMessage.timestampMillis(): Long = timestamp.toEpochMilliseconds()

fun ChatMessage.roleKey(): String = role.name

fun ChatMessage.isErrorAsLong(): Long = if (isError) 1L else 0L