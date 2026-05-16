package com.example.rosea.presentation.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rosea.domain.model.CartItem
import com.example.rosea.domain.model.Product
import com.example.rosea.domain.repository.CartRepository
import com.example.rosea.domain.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class DetailViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    // State untuk memunculkan notifikasi/snackbar saat barang berhasil masuk keranjang
    private val _addToCartStatus = MutableStateFlow<Boolean?>(null)
    val addToCartStatus: StateFlow<Boolean?> = _addToCartStatus.asStateFlow()

    // Fungsi membaca (Read) satu produk dari database
    fun loadProduct(id: Long) {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            try {
                val product = productRepository.getProductById(id)
                if (product != null) {
                    _uiState.value = DetailUiState.Success(product)
                } else {
                    _uiState.value = DetailUiState.Error("Produk tidak ditemukan")
                }
            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error(e.message ?: "Terjadi kesalahan")
            }
        }
    }

    // Fungsi menambah (Create) barang ke database keranjang (Cart)
    fun addToCart(product: Product) {
        viewModelScope.launch {
            try {
                val cartItem = CartItem(
                    id = 0, // Dibiarkan 0 karena di set AUTOINCREMENT di SQL
                    productId = product.id,
                    productName = product.name,
                    brand = product.brand,
                    price = product.price,
                    imageUrl = product.imageUrl,
                    quantity = 1,
                    addedAt = Clock.System.now().toEpochMilliseconds()
                )
                cartRepository.insertCartItem(cartItem)
                _addToCartStatus.value = true
            } catch (e: Exception) {
                _addToCartStatus.value = false
            }
        }
    }

    fun resetCartStatus() {
        _addToCartStatus.value = null
    }
}

// Mengatur status tampilan layar
sealed interface DetailUiState {
    object Loading : DetailUiState
    data class Success(val product: Product) : DetailUiState
    data class Error(val message: String) : DetailUiState
}