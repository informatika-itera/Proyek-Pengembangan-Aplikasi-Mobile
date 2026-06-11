package com.example.inventra.presentation.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventra.domain.model.BorrowRecord
import com.example.inventra.domain.model.BorrowStatus
import com.example.inventra.domain.model.Item
import com.example.inventra.domain.model.User
import com.example.inventra.domain.repository.AuthRepository
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
    private val borrowRepository: BorrowRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val currentUser: StateFlow<User?> = flow {
        emit(authRepository.getCurrentUser())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val uiState: StateFlow<ItemDetailUiState> = itemRepository.getItemById(itemId)
        .map { item ->
            if (item != null) ItemDetailUiState.Success(item)
            else ItemDetailUiState.NotFound
        }
        .catch { e -> emit(ItemDetailUiState.Error(e.message ?: "Terjadi kesalahan")) }
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

    fun requestBorrow(
        borrowerName: String,
        borrowerDivision: String, // parameter ini sekarang diabaikan, ambil dari repo
        onSuccess: () -> Unit,
        onError: (String) -> Unit = {}
    ) {
        val currentState = uiState.value

        if (currentState !is ItemDetailUiState.Success) {
            onError("Data barang belum dimuat, coba lagi")
            return
        }

        val item = currentState.item

        if (item.availableStock <= 0) {
            onError("Stok barang habis")
            return
        }

        viewModelScope.launch {
            try {
                val currentUser = authRepository.getCurrentUser()
                val division = currentUser?.division?.name ?: "PUBDOK"

                val now = Clock.System.now()
                val dueDate = now.plus(2, DateTimeUnit.DAY, TimeZone.currentSystemDefault())

                val record = BorrowRecord(
                    itemId = item.id,
                    borrowerId = currentUser?.id ?: "anonymous",
                    itemName = item.name,
                    borrowerName = borrowerName,
                    borrowerDivision = division,
                    borrowDate = now,
                    dueDate = dueDate,
                    status = BorrowStatus.PENDING
                )

                borrowRepository.borrowItem(record)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Gagal meminjam, coba lagi")
            }
        }
    }
}