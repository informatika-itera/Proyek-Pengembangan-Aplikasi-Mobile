package com.kosthub.app.domain.model

data class Profile(
    val id: Long,
    val name: String,
    val email: String,
    val latitude: Double,
    val longitude: Double
)
