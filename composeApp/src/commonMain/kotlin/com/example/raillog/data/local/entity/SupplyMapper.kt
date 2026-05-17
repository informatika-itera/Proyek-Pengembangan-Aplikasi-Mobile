package com.example.raillog.data.local.entity

import com.example.raillog.data.local.SupplyItemEntity
import com.example.raillog.domain.model.PartCategory
import com.example.raillog.domain.model.Priority
import com.example.raillog.domain.model.SupplyItem
import com.example.raillog.domain.model.SupplyStatus
import kotlinx.datetime.Instant

fun SupplyItemEntity.toDomain(): SupplyItem {
    return SupplyItem(
        id = id,
        partCode = part_code,
        name = name,
        category = PartCategory.fromString(category),
        quantity = quantity.toInt(),
        unit = unit,
        supplier = supplier,
        status = SupplyStatus.fromString(status),
        priority = Priority.fromString(priority),
        documentRef = document_ref,
        notes = notes,
        createdAt = Instant.fromEpochMilliseconds(created_at),
        updatedAt = Instant.fromEpochMilliseconds(updated_at)
    )
}

fun List<SupplyItemEntity>.toDomainList(): List<SupplyItem> {
    return map { it.toDomain() }
}