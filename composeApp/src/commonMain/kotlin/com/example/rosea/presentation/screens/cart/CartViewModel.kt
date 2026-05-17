package com.example.rosea.presentation.screens.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rosea.domain.model.CartItem
import com.example.rosea.domain.repository.CartRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class CartViewModel(
    private val cartRepository: CartRepository
) : ViewModel() {

    // READ: Mengambil seluruh item keranjang secara Real-Time (Flow)
    val uiState: StateFlow<CartUiState> = cartRepository.getCartItems()
        .map { items -> CartUiState.Success(items) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CartUiState.Loading
        )

    // UPDATE: Menambah atau mengurangi kuantitas produk
    fun updateQuantity(productId: Long, currentQuantity: Long, isIncrease: Boolean) {
        val newQuantity = if (isIncrease) currentQuantity + 1 else currentQuantity - 1

        viewModelScope.launch {
            if (newQuantity <= 0) {
                // Jika kuantitas menjadi 0, otomatis hapus dari keranjang
                cartRepository.deleteCartItem(productId)
            } else {
                cartRepository.updateCartItemQuantity(
                    productId = productId,
                    quantity = newQuantity,
                    addedAt = Clock.System.now().toEpochMilliseconds()
                )
            }
        }
    }

    // DELETE: Menghapus item dari keranjang
    fun removeItem(productId: Long) {
        viewModelScope.launch {
            cartRepository.deleteCartItem(productId)
        }
    }

    // DELETE ALL: Mengosongkan keranjang setelah Checkout
    fun checkout() {
        viewModelScope.launch {
            cartRepository.clearCart()
        }
    }
}

sealed interface CartUiState {
    object Loading : CartUiState
    data class Success(val items: List<CartItem>) : CartUiState
}