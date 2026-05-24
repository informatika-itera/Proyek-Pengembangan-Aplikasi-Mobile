package com.example.rosea.presentation.screens.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rosea.domain.model.CartItem
import com.example.rosea.domain.repository.CartRepository
import com.example.rosea.domain.repository.OrderRepository // <-- Import baru
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class CartViewModel(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository // <-- Injeksi baru
) : ViewModel() {

    val uiState: StateFlow<CartUiState> = cartRepository.getCartItems()
        .map { items -> CartUiState.Success(items) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CartUiState.Loading
        )

    fun updateQuantity(productId: Long, currentQuantity: Long, isIncrease: Boolean) {
        val newQuantity = if (isIncrease) currentQuantity + 1 else currentQuantity - 1
        viewModelScope.launch {
            if (newQuantity <= 0) {
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

    fun removeItem(productId: Long) {
        viewModelScope.launch {
            cartRepository.deleteCartItem(productId)
        }
    }

    // Fungsi Checkout yang baru
    fun checkout(items: List<CartItem>, totalPrice: Double) {
        viewModelScope.launch {
            val summary = items.joinToString(", ") { "${it.productName} (x${it.quantity})" }
            orderRepository.saveOrderLocally(totalPrice, summary)
            cartRepository.clearCart()
        }
    }
}

sealed interface CartUiState {
    object Loading : CartUiState
    data class Success(val items: List<CartItem>) : CartUiState
}