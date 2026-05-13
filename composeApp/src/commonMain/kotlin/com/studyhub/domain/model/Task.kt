package com.studyhub.domain.model

data class Task(
    val id: String,
    val title: String,
    val deadline: Long,
    val isCompleted: Boolean
)
