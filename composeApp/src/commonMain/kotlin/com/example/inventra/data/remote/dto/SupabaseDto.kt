package com.example.inventra.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
    val id: String,
    val name: String,
    val role: String = "MEMBER",
    val division: String = "PUBDOK",
    @SerialName("student_id") val studentId: String? = null,
    val phone: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class ItemDto(
    val id: String = "",
    val name: String,
    val description: String = "",
    val category: String = "OTHER",
    val location: String = "",
    @SerialName("total_stock") val totalStock: Int = 1,
    @SerialName("available_stock") val availableStock: Int = 1,
    val condition: String = "GOOD",
    @SerialName("pic_name") val picName: String = "",
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class BorrowRecordDto(
    val id: String = "",
    @SerialName("item_id") val itemId: String,
    @SerialName("borrower_id") val borrowerId: String,
    @SerialName("approver_id") val approverId: String? = null,
    @SerialName("item_name") val itemName: String,
    @SerialName("borrower_name") val borrowerName: String,
    val division: String,
    val quantity: Int = 1,
    val notes: String? = null,
    @SerialName("borrow_date") val borrowDate: String,
    @SerialName("due_date") val dueDate: String,
    @SerialName("return_date") val returnDate: String? = null,
    val status: String = "PENDING",
    @SerialName("fine_amount") val fineAmount: Long = 0,
    @SerialName("admin_note") val adminNote: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class NotificationDto(
    val id: String = "",
    @SerialName("user_id") val userId: String,
    val type: String,
    val title: String,
    val body: String,
    @SerialName("is_read") val isRead: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class InsertItemDto(
    val name: String,
    val description: String = "",
    val category: String = "OTHER",
    val location: String = "",
    @SerialName("total_stock") val totalStock: Int = 1,
    @SerialName("available_stock") val availableStock: Int = 1,
    val condition: String = "GOOD",
    @SerialName("pic_name") val picName: String = "",
    @SerialName("image_url") val imageUrl: String? = null
)

@Serializable
data class InsertBorrowDto(
    @SerialName("item_id") val itemId: String,
    @SerialName("borrower_id") val borrowerId: String,
    @SerialName("item_name") val itemName: String,
    @SerialName("borrower_name") val borrowerName: String,
    val division: String,
    val quantity: Int = 1,
    val notes: String? = null,
    @SerialName("due_date") val dueDate: String,
    val status: String = "PENDING"
)