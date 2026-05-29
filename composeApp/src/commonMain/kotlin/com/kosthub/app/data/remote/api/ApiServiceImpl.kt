package com.kosthub.app.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import com.kosthub.app.data.remote.NetworkResult
import com.kosthub.app.data.remote.dto.KostDetailResponse
import com.kosthub.app.data.remote.dto.KostListResponse

class ApiServiceImpl(
    private val client: HttpClient,
    private val baseUrl: String = "https://kosthup-api.klikolio-creative.workers.dev"
) : ApiService {

    override suspend fun getAllKosts(tipeKos: String?): NetworkResult<KostListResponse> {
        return try {
            val response = client.get("$baseUrl/api/kosts") {
                parameter("tipeKos", tipeKos)
            }
            NetworkResult.Success(response.body())
        } catch (e: Exception) {
            NetworkResult.Error(e, e.message)
        }
    }

    override suspend fun getKostById(id: Long): NetworkResult<KostDetailResponse> {
        return try {
            val response = client.get("$baseUrl/api/kosts/$id")
            NetworkResult.Success(response.body())
        } catch (e: Exception) {
            NetworkResult.Error(e, e.message)
        }
    }
}
