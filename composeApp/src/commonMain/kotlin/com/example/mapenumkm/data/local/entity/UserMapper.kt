package com.example.mapenumkm.data.local.entity

import com.example.mapenumkm.data.local.UserEntity
import com.example.mapenumkm.domain.model.User

fun UserEntity.toDomain(): User {
    return User(
        id = id,
        name = name,
        email = email,
        phone = phone,
        password = password,
        createdAt = created_at
    )
}
