package com.example.rosea.domain.repository

import com.example.rosea.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    // Read: Menampilkan katalog produk secara realtime (reaktif)
    fun getAllProducts(): Flow<List<Product>>

    // Read: Mengambil detail 1 produk spesifik
    suspend fun getProductById(id: Long): Product?

    // Fitur Lanjutan: Pencarian Pintar (Search)
    fun searchProducts(query: String): Flow<List<Product>>

    // Fitur Lanjutan: Filter Kategori
    fun getProductsByCategory(category: String): Flow<List<Product>>

    // Offline-First: Menyimpan cache data katalog ke HP
    suspend fun insertProduct(product: Product)
    suspend fun deleteAllProducts()
}