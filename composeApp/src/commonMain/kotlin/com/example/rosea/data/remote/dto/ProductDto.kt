package com.example.rosea.data.remote.dto

import com.example.rosea.domain.model.Product
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

// Wadah utama untuk menangkap array "products" dari DummyJSON
@Serializable
data class ProductResponseDto(
    val products: List<ProductDto>
)

// Wadah detail untuk masing-class masing produk dari internet
@Serializable
data class ProductDto(
    val id: Long,
    val title: String,
    val description: String,
    val price: Double,
    val brand: String? = null,
    val category: String,
    val thumbnail: String
)

/**
 * Extension function (Fungsi Bantuan) untuk memetakan (mapping)
 * data DTO dari internet menjadi bentuk Model Domain aplikasi ROSÉA.
 */
fun ProductDto.toDomain(): Product {
    return Product(
        id = this.id,
        // Di internet namanya "title", di database kita namanya "name"
        name = this.title,
        // Jika dari API brand-nya kosong, kita beri nama default
        brand = this.brand ?: "ROSÉA Collection",
        description = this.description,
        // Harga di DummyJSON menggunakan Dollar (USD), kita kalikan 15.500 agar menjadi Rupiah
        price = this.price * 15500.0,
        category = this.category.replaceFirstChar { it.uppercase() },
        imageUrl = this.thumbnail,
        createdAt = Clock.System.now().toEpochMilliseconds(),
        updatedAt = Clock.System.now().toEpochMilliseconds()
    )
}