package com.example.raillog.domain.repository

import com.example.raillog.domain.model.TechnicalDocument
import com.example.raillog.domain.model.VerificationStatus
import kotlinx.coroutines.flow.Flow

interface DocumentRepository {
    fun getAllDocuments(): Flow<List<TechnicalDocument>>
    fun getDocumentsByItem(itemId: Long): Flow<List<TechnicalDocument>>
    fun getDocumentById(id: Long): Flow<TechnicalDocument?>
    fun getUnverifiedDocuments(): Flow<List<TechnicalDocument>>
    suspend fun insertDocument(doc: TechnicalDocument): Long
    suspend fun updateVerification(id: Long, status: VerificationStatus, aiSummary: String?)
    suspend fun deleteDocument(id: Long)
}