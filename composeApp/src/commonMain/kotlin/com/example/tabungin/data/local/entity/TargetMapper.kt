package com.example.tabungin.data.local.entity

import com.example.tabungin.data.local.GetAllTargets
import com.example.tabungin.data.local.GetAllSetoran
import com.example.tabungin.data.local.GetTargetById
import com.example.tabungin.data.local.SetoranEntity
import com.example.tabungin.domain.model.Setoran
import com.example.tabungin.domain.model.Target



fun GetAllTargets.toDomain() = Target(
    id           = id,
    nama         = nama,
    targetAmount = targetAmount,
    terkumpul    = terkumpul,
    deadline     = deadline,
    icon         = icon,
    warna        = warna,
    createdAt    = createdAt,
    updatedAt    = updatedAt
)

fun GetTargetById.toDomain() = Target(
    id           = id,
    nama         = nama,
    targetAmount = targetAmount,
    terkumpul    = terkumpul,
    deadline     = deadline,
    icon         = icon,
    warna        = warna,
    createdAt    = createdAt,
    updatedAt    = updatedAt
)


fun GetAllSetoran.toDomain() = Setoran(
    id          = id,
    targetId    = targetId,
    amount      = amount,
    catatan     = catatan,
    tanggal     = tanggal,
    createdAt   = createdAt,
    targetNama  = targetNama
)

fun SetoranEntity.toDomain() = Setoran(
    id        = id,
    targetId  = targetId,
    amount    = amount,
    catatan   = catatan,
    tanggal   = tanggal,
    createdAt = createdAt
)
