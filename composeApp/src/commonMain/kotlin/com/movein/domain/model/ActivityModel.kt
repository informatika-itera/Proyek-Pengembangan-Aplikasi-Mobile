package com.movein.domain.model

data class ActivityModel(
    val id: Long = 0,
    val title: String,
    val description: String,
    val mood: String,
    val createdAt: Long
)