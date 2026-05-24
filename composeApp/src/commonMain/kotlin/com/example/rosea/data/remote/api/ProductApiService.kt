package com.example.rosea.data.remote.api

import com.example.rosea.data.remote.dto.ProductResponseDto
import com.example.rosea.data.remote.dto.toDomain
import com.example.rosea.domain.model.Product
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class ProductApiService(private val client: HttpClient) {

    /**
     * Fungsi ini bertugas mengambil data katalog produk kecantikan (beauty)
     * dari server publik DummyJSON.
     */
    suspend fun getBeautyProducts(): List<Product> {
        return try {
            // 1. Ktor melakukan HTTP GET Request ke URL internet
            val response: ProductResponseDto = client.get("https://dummyjson.com/products/category/beauty").body()

            // 2. Data JSON yang didapat (DTO) langsung diubah (mapping) menjadi
            // Model Domain "Product" yang dipahami oleh database aplikasi kita
            response.products.map { it.toDomain() }
        } catch (e: Exception) {
            // Jika tidak ada koneksi internet atau server error, kita lemparkan peringatan
            throw Exception("Gagal mengambil data dari internet: ${e.message}")
        }
    }
}