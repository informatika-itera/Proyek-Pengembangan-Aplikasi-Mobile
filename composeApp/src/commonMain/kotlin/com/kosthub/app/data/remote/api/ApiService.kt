package com.kosthub.app.data.remote.api

import com.kosthub.app.data.remote.NetworkResult
import com.kosthub.app.data.remote.dto.KostDetailResponse
import com.kosthub.app.data.remote.dto.KostDto
import com.kosthub.app.data.remote.dto.KostListResponse

interface ApiService {
    suspend fun getAllKosts(daerah: String? = null, tipeKos: String? = null): NetworkResult<KostListResponse>
    suspend fun getKostById(id: Long): NetworkResult<KostDetailResponse>
    suspend fun addKost(kostDto: KostDto): NetworkResult<KostDetailResponse>
    suspend fun updateKost(id: Long, kostDto: KostDto): NetworkResult<KostDetailResponse>
    suspend fun deleteKost(id: Long): NetworkResult<Unit>
}
