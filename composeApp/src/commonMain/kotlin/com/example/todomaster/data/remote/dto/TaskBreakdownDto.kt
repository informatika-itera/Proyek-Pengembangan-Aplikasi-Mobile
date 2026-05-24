package com.example.todomaster.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubTaskResponse(
    @SerialName("title")
    val title: String,

    @SerialName("estimated_minutes")
    val estimatedMinutes: Int
)