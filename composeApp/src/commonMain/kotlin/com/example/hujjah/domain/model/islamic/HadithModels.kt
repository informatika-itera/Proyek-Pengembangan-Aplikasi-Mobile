package com.example.hujjah.domain.model.islamic

import kotlinx.serialization.Serializable

@Serializable
data class HadithBookItem(
    val id: String,
    val name: String,
    val totalHadith: Int
)

@Serializable
data class HadithItem(
    val number: Int,
    val arab: String,
    val translation: String
)
