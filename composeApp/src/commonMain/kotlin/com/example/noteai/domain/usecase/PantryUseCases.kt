package com.example.noteai.domain.usecase

import com.example.noteai.domain.model.PantryItem
import com.example.noteai.domain.repository.PantryRepository
import kotlinx.coroutines.flow.Flow

class GetPantryItems(private val repository: PantryRepository) {
    operator fun invoke(): Flow<List<PantryItem>> = repository.getAllPantryItems()
}

class AddPantryItem(private val repository: PantryRepository) {
    suspend operator fun invoke(item: PantryItem) = repository.insertPantryItem(item)
}

class UpdatePantryItem(private val repository: PantryRepository) {
    suspend operator fun invoke(item: PantryItem) = repository.updatePantryItem(item)
}

class DeletePantryItem(private val repository: PantryRepository) {
    suspend operator fun invoke(id: Long) = repository.deletePantryItem(id)
}

class UpdateStockAmount(private val repository: PantryRepository) {
    suspend operator fun invoke(id: Long, amount: Double) = repository.updateStockAmount(id, amount)
}
