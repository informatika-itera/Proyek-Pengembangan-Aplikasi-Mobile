package com.example.metaforge.domain.model

data class Hero(
    val id: Int,
    val name: String,
    val role: HeroRole,
    val imageUrl: String,
    val specialty: String = ""
)