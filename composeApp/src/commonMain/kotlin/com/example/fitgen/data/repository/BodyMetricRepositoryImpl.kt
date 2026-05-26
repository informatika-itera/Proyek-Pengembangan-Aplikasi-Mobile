package com.example.fitgen.data.repository

import com.example.fitgen.domain.model.BodyMetric
import com.example.fitgen.domain.repository.BodyMetricRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Implementasi sementara BodyMetricRepository menggunakan in-memory storage.
 * Akan diganti dengan SQLDelight setelah schema di-generate.
 */
class BodyMetricRepositoryImpl : BodyMetricRepository {

    private val _metrics = MutableStateFlow<List<BodyMetric>>(emptyList())
    private var nextId = 1L

    override fun getAllBodyMetrics(): Flow<List<BodyMetric>> {
        return _metrics.asStateFlow()
    }

    override suspend fun getLatestBodyMetric(): BodyMetric? {
        return _metrics.value.maxByOrNull { it.tanggal }
    }

    override suspend fun insertBodyMetric(metric: BodyMetric): Long {
        val newMetric = metric.copy(id = nextId++)
        _metrics.value = _metrics.value + newMetric
        return newMetric.id
    }

    override suspend fun deleteBodyMetric(id: Long) {
        _metrics.value = _metrics.value.filter { it.id != id }
    }
}