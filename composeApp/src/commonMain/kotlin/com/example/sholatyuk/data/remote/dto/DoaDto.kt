package com.example.sholatyuk.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class DoaDto(
    val doa: String,
    val ayat: String,
    val latin: String,
    val artinya: String
)