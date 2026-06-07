package com.studyhub.data.sync

import com.studyhub.core.util.NetworkMonitor
import com.studyhub.data.local.SyncOperation
import com.studyhub.data.local.SyncQueueDataSource
import com.studyhub.domain.repository.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SyncManager(
    private val syncQueueDataSource: SyncQueueDataSource,
    private val networkMonitor: NetworkMonitor,
    private val taskRepository: TaskRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var isSyncing = false

    fun startAutoSync() {
        scope.launch {
            networkMonitor.isOnline.collectLatest { isOnline ->
                if (isOnline) {
                    processQueue()
                }
            }
        }
    }

    private suspend fun processQueue() {
        if (isSyncing) return
        isSyncing = true
        
        try {
            val pendingItems = syncQueueDataSource.getAllPending()
            if (pendingItems.isEmpty()) {
                isSyncing = false
                return
            }

            for (item in pendingItems) {
                val success = try {
                    // Di sini seharusnya ada panggilan ke Remote API
                    // Karena simulasi offline-first, kita anggap sync berhasil
                    // dan hapus dari queue. Jika ada API beneran, panggil di sini.
                    
                    println("Syncing item: ${item.operation} for task ${item.taskId}")
                    delay(500) // Simulasi network delay
                    true
                } catch (e: Exception) {
                    syncQueueDataSource.incrementRetry(item.id)
                    false
                }

                if (success) {
                    syncQueueDataSource.remove(item.id)
                }
            }
        } finally {
            isSyncing = false
            syncQueueDataSource.cleanupFailed()
        }
    }
}
