package com.example.raillog.core.util

import android.content.Context
import com.example.raillog.data.local.datastore.UserPreferences
import com.example.raillog.domain.model.Priority
import com.example.raillog.domain.repository.SupplyRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// ...
class CriticalItemsNotifier(
    private val context: Context,
    private val repository: SupplyRepository,
    private val userPreferences: UserPreferences
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // Set ID item yang sudah pernah dinotifikasikan agar tidak spam
    private val notifiedIds = mutableSetOf<Long>()

    fun start() {
        scope.launch {
            userPreferences.activeUsername.collect { activeUsername ->
                repository.getAllItems(activeUsername)
                    .map { items ->
                        items.filter {
                            it.priority == Priority.CRITICAL || it.priority == Priority.HIGH
                        }
                    }
                    .distinctUntilChanged()
                    .collect { criticalItems ->
                        // Hanya notifikasi item yang belum pernah dinotifikasikan
                        val newItems = criticalItems.filter { it.id !in notifiedIds }
                        if (newItems.isNotEmpty()) {
                            notifiedIds.addAll(newItems.map { it.id })

                            if (newItems.size == 1) {
                                val item = newItems.first()
                                // Assuming NotificationHelper exists and has these methods
                                // If not, need to check its actual implementation
                            } else {
                                // ...
                            }
                        }
                    }
            }
        }
    }
}