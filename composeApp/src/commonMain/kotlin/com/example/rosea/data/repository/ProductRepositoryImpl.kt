package com.example.rosea.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.rosea.data.local.NoteDatabase
import com.example.rosea.data.remote.api.ProductApiService
import com.example.rosea.domain.model.Product
import com.example.rosea.domain.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

class ProductRepositoryImpl(
    db: NoteDatabase,
    private val apiService: ProductApiService // <--- Menyuntikkan kurir API Ktor
) : ProductRepository {

    private val queries = db.productQueries

    override fun getAllProducts(): Flow<List<Product>> {
        return queries.getAllProducts()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { entity ->
                    Product(
                        id = entity.id,
                        name = entity.name,
                        brand = entity.brand,
                        description = entity.description,
                        price = entity.price,
                        category = entity.category,
                        imageUrl = entity.image_url,
                        createdAt = entity.created_at,
                        updatedAt = entity.updated_at
                    )
                }
            }
            .onStart {
                // Mekanisme Utama Cache-First / Offline-First saat aliran data dimulai
                try {
                    // 1. Ambil data produk kecantikan segar langsung dari internet
                    val remoteProducts = apiService.getBeautyProducts()

                    if (remoteProducts.isNotEmpty()) {
                        // 2. Bersihkan cache produk kosmetik lama di SQLite lokal
                        queries.deleteAllProducts()

                        // 3. Simpan data baru hasil download ke dalam penyimpanan internal HP
                        remoteProducts.forEach { product ->
                            queries.insertProduct(
                                id = product.id,
                                name = product.name,
                                brand = product.brand,
                                description = product.description,
                                price = product.price,
                                category = product.category,
                                image_url = product.imageUrl,
                                created_at = product.createdAt,
                                updated_at = product.updatedAt
                            )
                        }
                    }
                } catch (e: Exception) {
                    // JIKA OFFLINE: Tangkap error jaringan agar aplikasi tidak crash,
                    // sistem akan otomatis langsung mengandalkan data lokal yang ada di SQLite.
                    println("ROSÉA Offline Mode aktif: ${e.message}")
                }
            }
    }

    override suspend fun getProductById(id: Long): Product? {
        return withContext(Dispatchers.IO) {
            queries.getProductById(id).executeAsOneOrNull()?.let { entity ->
                Product(
                    id = entity.id,
                    name = entity.name,
                    brand = entity.brand,
                    description = entity.description,
                    price = entity.price,
                    category = entity.category,
                    imageUrl = entity.image_url,
                    createdAt = entity.created_at,
                    updatedAt = entity.updated_at
                )
            }
        }
    }

    override fun searchProducts(query: String): Flow<List<Product>> {
        val formattedQuery = "%$query%"
        return queries.searchProducts(query = formattedQuery)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { entity ->
                    Product(
                        id = entity.id, name = entity.name, brand = entity.brand,
                        description = entity.description, price = entity.price,
                        category = entity.category, imageUrl = entity.image_url,
                        createdAt = entity.created_at, updatedAt = entity.updated_at
                    )
                }
            }
    }

    override fun getProductsByCategory(category: String): Flow<List<Product>> {
        return queries.getProductsByCategory(category)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { entity ->
                    Product(
                        id = entity.id, name = entity.name, brand = entity.brand,
                        description = entity.description, price = entity.price,
                        category = entity.category, imageUrl = entity.image_url,
                        createdAt = entity.created_at, updatedAt = entity.updated_at
                    )
                }
            }
    }

    override suspend fun insertProduct(product: Product) {
        withContext(Dispatchers.IO) {
            queries.insertProduct(
                id = product.id,
                name = product.name,
                brand = product.brand,
                description = product.description,
                price = product.price,
                category = product.category,
                image_url = product.imageUrl,
                created_at = product.createdAt,
                updated_at = product.updatedAt
            )
        }
    }

    override suspend fun deleteAllProducts() {
        withContext(Dispatchers.IO) {
            queries.deleteAllProducts()
        }
    }
}