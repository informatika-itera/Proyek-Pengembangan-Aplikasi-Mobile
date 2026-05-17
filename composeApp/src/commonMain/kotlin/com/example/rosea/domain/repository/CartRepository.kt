package com.example.rosea.domain.repository

import com.example.rosea.domain.model.CartItem
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    // Read: Melihat isi tas belanja (My Shopping Bag)
    fun getCartItems(): Flow<List<CartItem>>

    // Create: Menambah produk ke tas belanja
    suspend fun insertCartItem(item: CartItem)

    // Update: Menambah/mengurangi jumlah barang
    suspend fun updateCartItemQuantity(productId: Long, quantity: Long, addedAt: Long)

    // Delete: Menghapus produk dari tas belanja
    suspend fun deleteCartItem(productId: Long)

    // Delete: Mengosongkan tas belanja setelah selesai
    suspend fun clearCart()
}