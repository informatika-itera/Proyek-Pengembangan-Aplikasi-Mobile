package com.example.rosea.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: Long,
    val name: String,
    val brand: String,
    val description: String,
    val price: Double,
    val category: String,
    val imageUrl: String,
    val createdAt: Long,
    val updatedAt: Long
)