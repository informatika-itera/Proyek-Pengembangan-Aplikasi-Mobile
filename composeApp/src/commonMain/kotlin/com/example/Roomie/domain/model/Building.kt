package com.example.Roomie.domain.model

data class Building(
    val id: String,
    val name: String,
    val description: String,
    val isAvailable: Boolean = false
)
