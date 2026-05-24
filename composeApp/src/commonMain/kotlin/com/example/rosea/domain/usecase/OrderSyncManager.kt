package com.example.rosea.domain.usecase

import com.example.rosea.domain.repository.OrderRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class OrderSyncManager(
    private val orderRepository: OrderRepository
) {
    private var syncJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    // Panggil fungsi ini SAAT APLIKASI PERTAMA KALI DIBUKA
    fun startObserving() {
        if (syncJob?.isActive == true) return

        syncJob = scope.launch {
            // collectLatest akan memantau tabel SQLite secara realtime
            orderRepository.getPendingOrdersFlow().collectLatest { pendingOrders ->
                if (pendingOrders.isNotEmpty()) {
                    // Jika ada pesanan ngantri, coba kirim satu per satu
                    for (order in pendingOrders) {
                        val success = orderRepository.syncOrderToApi(order)
                        if (!success) {
                            // Jika gagal (misal internet mati), hentikan proses sync sementara
                            // dan coba lagi 10 detik kemudian untuk menghindari spam request
                            delay(10000)
                            break
                        }
                    }
                }
            }
        }
    }
}