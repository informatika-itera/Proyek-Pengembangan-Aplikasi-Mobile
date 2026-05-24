package com.example.mapenumkm.presentation.screens.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapenumkm.domain.model.Note
import com.example.mapenumkm.domain.model.Transaction
import com.example.mapenumkm.domain.model.TransactionItem
import com.example.mapenumkm.domain.repository.NoteRepository
import com.example.mapenumkm.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TransactionUiState(
    val products: List<Note> = emptyList(),
    val searchQuery: String = "",
    val cartItems: Map<Long, Int> = emptyMap(), // ProductId -> Quantity
    val paymentAmount: String = "",
    val discount: Double = 0.0,
    val isSuccess: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val selectedProducts: List<TransactionItem>
        get() = products.filter { cartItems.containsKey(it.id) && cartItems[it.id]!! > 0 }
            .map { product ->
                TransactionItem(
                    productId = product.id,
                    productName = product.title,
                    productPrice = product.price,
                    quantity = cartItems[product.id] ?: 0,
                    imageUrl = product.imageUri
                )
            }

    val subtotal: Double
        get() = selectedProducts.sumOf { it.totalPrice }

    val total: Double
        get() = subtotal - discount

    val changeAmount: Double
        get() {
            val pay = paymentAmount.toDoubleOrNull() ?: 0.0
            return (pay - total).coerceAtLeast(0.0)
        }
}

class TransactionViewModel(
    private val noteRepository: NoteRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            noteRepository.getAllNotes().collect { notes ->
                _uiState.update { it.copy(products = notes) }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        viewModelScope.launch {
            noteRepository.searchNotes(query).collect { notes ->
                _uiState.update { it.copy(products = notes) }
            }
        }
    }

    fun updateQuantity(productId: Long, delta: Int) {
        _uiState.update { state ->
            val product = state.products.find { it.id == productId }
            val availableStock = product?.stock ?: 0
            val currentQty = state.cartItems[productId] ?: 0
            
            val newQty = (currentQty + delta).coerceAtLeast(0)
            
            if (newQty > availableStock) {
                return@update state.copy(error = "Stok tidak mencukupi (Tersedia: $availableStock)")
            }

            val newCart = state.cartItems.toMutableMap()
            if (newQty == 0) {
                newCart.remove(productId)
            } else {
                newCart[productId] = newQty
            }
            state.copy(cartItems = newCart, error = null)
        }
    }

    fun onPaymentAmountChange(amount: String) {
        if (amount.isEmpty() || amount.all { it.isDigit() || it == '.' }) {
            _uiState.update { it.copy(paymentAmount = amount) }
        }
    }

    fun clearCart() {
        _uiState.update { it.copy(cartItems = emptyMap()) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun saveTransaction() {
        val state = _uiState.value
        if (state.selectedProducts.isEmpty()) {
            _uiState.update { it.copy(error = "Pilih minimal satu produk") }
            return
        }

        val payAmount = state.paymentAmount.toDoubleOrNull() ?: 0.0
        if (payAmount < state.total) {
            _uiState.update { it.copy(error = "Pembayaran kurang") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // Save Transaction
                val transaction = Transaction(
                    items = state.selectedProducts,
                    subtotal = state.subtotal,
                    discount = state.discount,
                    total = state.total,
                    paymentAmount = payAmount,
                    changeAmount = state.changeAmount
                )
                transactionRepository.insertTransaction(transaction)

                // Update Stock
                state.selectedProducts.forEach { item ->
                    val product = state.products.find { it.id == item.productId }
                    if (product != null) {
                        val updatedProduct = product.copy(
                            stock = (product.stock - item.quantity).coerceAtLeast(0)
                        )
                        noteRepository.updateNote(updatedProduct)
                    }
                }

                _uiState.update { it.copy(isSuccess = true, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }
}