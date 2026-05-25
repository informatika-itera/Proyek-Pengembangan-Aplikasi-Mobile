package com.example.inventra.presentation.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventra.domain.model.BorrowRecord
import com.example.inventra.domain.model.Item
import com.example.inventra.domain.repository.BorrowRepository
import com.example.inventra.domain.repository.ItemRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus

sealed interface ItemDetailUiState {
    data object Loading : ItemDetailUiState
    data class Success(val item: Item) : ItemDetailUiState
    data object NotFound : ItemDetailUiState
    data class Error(val message: String) : ItemDetailUiState
}

class ItemDetailViewModel(
    private val itemId: Long,
    private val itemRepository: ItemRepository,
    private val borrowRepository: BorrowRepository
) : ViewModel() {

    val uiState: StateFlow<ItemDetailUiState> = itemRepository.getItemById(itemId)
        .map { item ->
            if (item != null) ItemDetailUiState.Success(item)
            else ItemDetailUiState.NotFound
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ItemDetailUiState.Loading
        )

    fun deleteItem(onSuccess: () -> Unit) {
        viewModelScope.launch {
            itemRepository.deleteItem(itemId)
            onSuccess()
        }
    }

    fun borrowItem(borrowerName: String, onSuccess: () -> Unit) {
        val currentState = uiState.value
        if (currentState is ItemDetailUiState.Success) {
            viewModelScope.launch {
                val item = currentState.item
                if (item.availableStock > 0) {
                    val now = Clock.System.now()
                    val dueDate = now.plus(2, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
                    
                    val record = BorrowRecord(
                        itemId = item.id,
                        itemName = item.name,
                        borrowerName = borrowerName,
                        borrowDate = now,
                        dueDate = dueDate
                    )
                    
                    borrowRepository.borrowItem(record)
                    
                    // Update item stock
                    itemRepository.updateItem(
                        item.copy(availableStock = item.availableStock - 1)
                    )
                    onSuccess()
                }
            }
        }
    }
}
