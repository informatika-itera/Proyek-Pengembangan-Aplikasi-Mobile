package com.example.inventra.data.local.entity

import com.example.inventra.data.local.BorrowRecordEntity
import com.example.inventra.data.local.ItemEntity
import com.example.inventra.domain.model.*
import kotlinx.datetime.Instant

fun ItemEntity.toDomain(): Item {
    return Item(
        id = id,
        remoteId = remote_id,
        name = name,
        description = description,
        category = try { ItemCategory.valueOf(category) } catch (e: Exception) { ItemCategory.OTHER },
        location = location,
        totalStock = total_stock.toInt(),
        availableStock = available_stock.toInt(), // FIX: bukan quantity
        condition = try { ItemCondition.valueOf(condition) } catch (e: Exception) { ItemCondition.GOOD },
        picName = pic_name,
        picPhone = pic_phone,
        imageUrl = image_url,
        createdAt = Instant.fromEpochMilliseconds(created_at),
        updatedAt = Instant.fromEpochMilliseconds(updated_at)
    )
}

fun BorrowRecordEntity.toDomain(): BorrowRecord {
    return BorrowRecord(
        id = id,
        remoteId = remote_id,
        itemId = item_id,
        borrowerId = borrower_id,
        itemName = item_name,
        borrowerName = borrower_name,
        borrowerDivision = borrower_division,
        borrowDate = Instant.fromEpochMilliseconds(borrow_date),
        dueDate = Instant.fromEpochMilliseconds(due_date),
        returnDate = return_date?.let { Instant.fromEpochMilliseconds(it) },
        status = try { BorrowStatus.valueOf(status) } catch (e: Exception) { BorrowStatus.ACTIVE },
        fineAmount = fine_amount,
        returnProofUrl = return_proof_url
    )
}
