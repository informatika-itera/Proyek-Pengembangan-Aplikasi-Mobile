package com.example.inventra.domain.usecase

import com.example.inventra.domain.model.Item
import com.example.inventra.domain.model.ItemCategory
import com.example.inventra.domain.repository.ItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAllItemsUseCase(
    private val repository: ItemRepository
) {
    operator fun invoke(sortBy: ItemSortBy = ItemSortBy.UPDATED_DESC): Flow<List<Item>> {
        return repository.getAllItems().map { items ->
            sortItems(items, sortBy)
        }
    }

    private fun sortItems(items: List<Item>, sortBy: ItemSortBy): List<Item> {
        return when (sortBy) {
            ItemSortBy.NAME_ASC -> items.sortedBy { it.name.lowercase() }
            ItemSortBy.NAME_DESC -> items.sortedByDescending { it.name.lowercase() }
            ItemSortBy.UPDATED_DESC -> items.sortedByDescending { it.updatedAt }
            ItemSortBy.CATEGORY -> items.sortedBy { it.category.name }
        }
    }
}

enum class ItemSortBy {
    NAME_ASC, NAME_DESC, UPDATED_DESC, CATEGORY
}

class SearchItemsUseCase(
    private val repository: ItemRepository
) {
    operator fun invoke(query: String, category: ItemCategory? = null): Flow<List<Item>> {
        return if (query.isBlank() && category == null) {
            repository.getAllItems()
        } else if (query.isBlank()) {
            repository.getItemsByCategory(category!!)
        } else {
            repository.searchItems(query).map { items ->
                if (category != null && category != ItemCategory.ALL) {
                    items.filter { it.category == category }
                } else {
                    items
                }
            }
        }
    }
}

class SaveItemUseCase(
    private val repository: ItemRepository
) {
    suspend operator fun invoke(item: Item): Result<Long> {
        return try {
            if (item.name.isBlank()) {
                return Result.failure(IllegalArgumentException("Nama item tidak boleh kosong"))
            }
            if (item.totalStock < 1) {
                return Result.failure(IllegalArgumentException("Stok minimal 1"))
            }

            val id = if (item.id == 0L) {
                repository.insertItem(item)
            } else {
                repository.updateItem(item)
                item.id
            }

            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class DeleteItemUseCase(
    private val repository: ItemRepository
) {
    suspend operator fun invoke(id: Long): Result<Unit> {
        return try {
            repository.deleteItem(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
