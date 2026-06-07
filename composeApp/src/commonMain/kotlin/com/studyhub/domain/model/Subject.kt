package com.studyhub.domain.model

import androidx.compose.runtime.Stable

@Stable
data class Subject(
    val id: String,
    val name: String,
    val colorHex: String,
    val icon: String,
    val createdAt: Long
)
