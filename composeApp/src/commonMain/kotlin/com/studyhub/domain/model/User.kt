package com.studyhub.domain.model

import androidx.compose.runtime.Stable

@Stable
data class User(
    val id: String,
    val displayName: String,
    val avatarInitials: String,
    val avatarColorHex: String
)
