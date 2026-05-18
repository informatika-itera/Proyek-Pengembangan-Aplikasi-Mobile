package com.kosthub.app.domain.model

data class Profile(
    val id: Long = 1,
    val name: String,
    val role: String,
    val contact: String,
    val preference: String,
    val location: String
)
