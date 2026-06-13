package com.example.raillog.domain.repository

import com.example.raillog.domain.model.DraftItem
import com.example.raillog.domain.model.SupplyItem
import com.example.raillog.domain.model.SupplyStatus
import kotlinx.coroutines.flow.Flow

interface SupplyRepository {
    suspend fun migrateDataToUser(username: String)
    fun getAllItems(activeUsername: String): Flow<List<SupplyItem>>
    fun getItemById(id: Long): Flow<SupplyItem?>

    suspend fun insertItem(item: SupplyItem, activeUsername: String)
    suspend fun updateItem(item: SupplyItem)
    suspend fun deleteItem(id: Long)
    suspend fun updateStatus(id: Long, status: SupplyStatus)

    // --- FITUR AUTO-SAVE DRAFT ---
    suspend fun saveDraft(draftId: String, projectTitle: String, currentStep: Int, lastUpdated: Long, formStateJson: String, activeUsername: String)
    fun getAllDrafts(activeUsername: String): Flow<List<DraftItem>>
    suspend fun deleteDraft(draftId: String)
}
