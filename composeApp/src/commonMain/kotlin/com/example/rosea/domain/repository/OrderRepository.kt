package com.example.rosea.domain.repository

import com.example.rosea.domain.model.PendingOrder
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    fun getPendingOrdersFlow(): Flow<List<PendingOrder>>
    suspend fun saveOrderLocally(totalPrice: Double, itemsSummary: String)
    suspend fun syncOrderToApi(order: PendingOrder): Boolean
}