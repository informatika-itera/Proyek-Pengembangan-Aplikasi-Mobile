package com.example.rosea.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.rosea.data.local.NoteDatabase
import com.example.rosea.domain.model.CartItem
import com.example.rosea.domain.repository.CartRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class CartRepositoryImpl(db: NoteDatabase) : CartRepository {
    private val queries = db.cartQueries

    override fun getCartItems(): Flow<List<CartItem>> {
        return queries.getCartItems().asFlow().mapToList(Dispatchers.IO).map { entities ->
            entities.map { entity ->
                CartItem(
                    id = entity.id, productId = entity.product_id, productName = entity.product_name,
                    brand = entity.brand, price = entity.price, imageUrl = entity.image_url,
                    quantity = entity.quantity, addedAt = entity.added_at
                )
            }
        }
    }

    override suspend fun insertCartItem(item: CartItem) {
        withContext(Dispatchers.IO) {
            queries.insertCartItem(
                product_id = item.productId, product_name = item.productName, brand = item.brand,
                price = item.price, image_url = item.imageUrl, quantity = item.quantity, added_at = item.addedAt
            )
        }
    }

    override suspend fun updateCartItemQuantity(productId: Long, quantity: Long, addedAt: Long) {
        withContext(Dispatchers.IO) { queries.updateCartItemQuantity(quantity, addedAt, productId) }
    }

    override suspend fun deleteCartItem(productId: Long) {
        withContext(Dispatchers.IO) { queries.deleteCartItem(productId) }
    }

    override suspend fun clearCart() {
        withContext(Dispatchers.IO) { queries.clearCart() }
    }
}