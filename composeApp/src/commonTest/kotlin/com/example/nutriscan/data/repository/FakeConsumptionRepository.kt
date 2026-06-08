package com.example.nutriscan.data.repository

import com.example.nutriscan.domain.model.ConsumptionEntry
import com.example.nutriscan.domain.repository.ConsumptionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/**
 * In-memory fake ConsumptionRepository untuk testing.
 */
class FakeConsumptionRepository : ConsumptionRepository {

    private val _entries = MutableStateFlow<List<ConsumptionEntry>>(emptyList())
    private var nextId = 1L

    override fun observeAll(): Flow<List<ConsumptionEntry>> = _entries

    override suspend fun add(entry: ConsumptionEntry) {
        _entries.update { it + entry.copy(id = nextId++) }
    }

    override suspend fun delete(id: Long) {
        _entries.update { list -> list.filter { it.id != id } }
    }

    override suspend fun clear() {
        _entries.value = emptyList()
    }

    // Helper untuk tests
    fun currentEntries(): List<ConsumptionEntry> = _entries.value
}