package com.example.rosea.domain.model

data class Address(
    val id: Long = 0,
    val label: String,
    val receiverName: String,
    val phoneNumber: String,
    val fullAddress: String,
    val isDefault: Boolean = false
)
