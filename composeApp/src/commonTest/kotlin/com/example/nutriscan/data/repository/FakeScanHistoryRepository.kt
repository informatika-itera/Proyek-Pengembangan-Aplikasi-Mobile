package com.example.nutriscan.data.repository

import com.example.nutriscan.domain.model.ScanResult
import com.example.nutriscan.domain.repository.ScanHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * In-memory fake repository untuk testing.
 * Tidak membutuhkan database nyata.
 */
class FakeScanHistoryRepository : ScanHistoryRepository {

    private val _scans = MutableStateFlow<List<ScanResult>>(emptyList())
    private var nextId = 1L

    override fun getAllHistory(): Flow<List<ScanResult>> = _scans

    override fun getRecentHistory(limit: Long): Flow<List<ScanResult>> =
        _scans.map { it.take(limit.toInt()) }

    override suspend fun getScanById(id: Long): ScanResult? =
        _scans.value.find { it.id == id }

    // ← PERBAIKAN: implement getScanByBarcode yang hilang dari interface
    override suspend fun getScanByBarcode(barcode: String): ScanResult? =
        _scans.value.find { it.product.barcode == barcode }

    override suspend fun saveScan(scanResult: ScanResult) {
        val withId = scanResult.copy(id = nextId++)
        _scans.update { it + withId }
    }

    override suspend fun deleteScan(id: Long) {
        _scans.update { list -> list.filter { it.id != id } }
    }

    override suspend fun clearHistory() {
        _scans.value = emptyList()
    }

    // Helper untuk tests
    fun currentScans(): List<ScanResult> = _scans.value
}