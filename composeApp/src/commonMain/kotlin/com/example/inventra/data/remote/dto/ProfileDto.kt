package com.example.inventra.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
    @SerialName("id") val id: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("role") val role: String = "MEMBER",
    @SerialName("division") val division: String = "PUBDOK",
    @SerialName("student_id") val studentId: String? = null,
    @SerialName("phone") val phone: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("division_head") val divisionHead: String? = null,
    @SerialName("staff_list") val staffList: String? = null
)