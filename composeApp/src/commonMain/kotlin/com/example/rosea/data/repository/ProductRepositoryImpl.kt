package com.example.rosea.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.rosea.data.local.NoteDatabase
import com.example.rosea.data.local.ProductEntity
import com.example.rosea.data.remote.api.ProductApiService
import com.example.rosea.domain.model.Product
import com.example.rosea.domain.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

class ProductRepositoryImpl(
    private val db: NoteDatabase,
    private val apiService: ProductApiService
) : ProductRepository {

    private val queries = db.productQueries

    override fun getAllProducts(): Flow<List<Product>> {
        return queries.getAllProducts()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities: List<ProductEntity> ->
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
                try {
                    val remoteProducts = apiService.getBeautyProducts()
                    if (remoteProducts.isNotEmpty()) {
                        queries.deleteAllProducts()
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
                    } else if (queries.getAllProducts().executeAsList().isEmpty()) {
                        // Tambahkan data dummy jika API kosong dan lokal kosong (untuk demo)
                        addDummyData()
                    }
                } catch (e: Exception) {
                    println("ROSÉA Offline/Error: ${e.message}")
                    if (queries.getAllProducts().executeAsList().isEmpty()) {
                        addDummyData()
                    }
                }
            }
    }

    private suspend fun addDummyData() = withContext(Dispatchers.Default) {
        val categories = listOf("Blush", "Bronzer", "Eyebrow", "Eyeliner", "Eyeshadow", "Lipstick")
        categories.forEachIndexed { index, cat ->
            queries.insertProduct(
                id = (index + 100).toLong(),
                name = "Rosea Luxe $cat",
                brand = "ROSÉA",
                description = "Koleksi premium $cat untuk tampilan natural dan elegan.",
                price = if (index % 2 == 0) 145000.0 else 225000.0, // Beberapa di bawah 200rb untuk tab "Promo"
                category = cat,
                image_url = "https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?q=80&w=500", // Placeholder cantik
                created_at = 1718140800000L, // Mock date
                updated_at = 1718140800000L
            )
        }
    }

    override suspend fun getProductById(id: Long): Product? {
        return withContext(Dispatchers.Default) {
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
            .mapToList(Dispatchers.Default)
            .map { entities: List<ProductEntity> ->
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
            .mapToList(Dispatchers.Default)
            .map { entities: List<ProductEntity> ->
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
        withContext(Dispatchers.Default) {
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
        withContext(Dispatchers.Default) {
            queries.deleteAllProducts()
        }
    }
}
