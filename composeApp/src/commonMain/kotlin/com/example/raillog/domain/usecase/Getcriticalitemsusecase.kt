package com.example.raillog.domain.usecase

import com.example.raillog.domain.model.Priority
import com.example.raillog.domain.model.SupplyItem
import com.example.raillog.domain.repository.SupplyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Use case untuk mendapatkan daftar komponen dengan prioritas CRITICAL atau HIGH.
 *
 * Digunakan oleh:
 * - CriticalItemsNotifier (Android) untuk memicu notifikasi
 * - Dashboard untuk menampilkan ringkasan komponen kritis
 *
 * Lokasi: domain/usecase/GetCriticalItemsUseCase.kt
 */
class GetCriticalItemsUseCase(
    private val repository: SupplyRepository
) {
    /**
     * Mengembalikan Flow list komponen yang membutuhkan perhatian segera.
     * Filter: Priority.CRITICAL atau Priority.HIGH
     */
    operator fun invoke(activeUsername: String): Flow<List<SupplyItem>> {
        return repository.getAllItems(activeUsername).map { items ->
            items.filter { item ->
                item.priority == Priority.CRITICAL || item.priority == Priority.HIGH
            }
        }
    }
}