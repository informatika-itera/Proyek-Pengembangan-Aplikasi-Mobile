package com.kosthub.app.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.contentType
import com.kosthub.app.data.remote.NetworkResult
import com.kosthub.app.data.remote.dto.KostDetailResponse
import com.kosthub.app.data.remote.dto.KostDto
import com.kosthub.app.data.remote.dto.KostListResponse

class ApiServiceImpl(
    private val client: HttpClient,
    private val baseUrl: String = "http://localhost:8787"
) : ApiService {

    override suspend fun getAllKosts(daerah: String?, tipeKos: String?): NetworkResult<KostListResponse> {
        return try {
            val response = client.get("$baseUrl/api/kosts") {
                parameter("daerah", daerah)
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

    override suspend fun addKost(kostDto: KostDto): NetworkResult<KostDetailResponse> {
        return try {
            val response = client.post("$baseUrl/api/kosts") {
                contentType(ContentType.Application.Json)
                setBody(kostDto)
            }
            NetworkResult.Success(response.body())
        } catch (e: Exception) {
            NetworkResult.Error(e, e.message)
        }
    }

    override suspend fun updateKost(id: Long, kostDto: KostDto): NetworkResult<KostDetailResponse> {
        return try {
            val response = client.put("$baseUrl/api/kosts/$id") {
                contentType(ContentType.Application.Json)
                setBody(kostDto)
            }
            NetworkResult.Success(response.body())
        } catch (e: Exception) {
            NetworkResult.Error(e, e.message)
        }
    }

    override suspend fun deleteKost(id: Long): NetworkResult<Unit> {
        return try {
            client.delete("$baseUrl/api/kosts/$id")
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(e, e.message)
        }
    }
}
