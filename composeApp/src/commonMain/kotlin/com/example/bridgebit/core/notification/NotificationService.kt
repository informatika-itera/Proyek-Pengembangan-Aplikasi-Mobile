package com.example.bridgebit.core.notification

/**
 * KMP Notification Service
 *
 * Menyediakan abstraksi untuk menampilkan local notification lintas platform.
 */
expect class NotificationService() {
    /**
     * Meminta izin notifikasi dari pengguna.
     * @param onResult Callback yang mengembalikan true jika diizinkan, false jika ditolak.
     */
    fun requestPermission(onResult: (Boolean) -> Unit)

    /**
     * Menampilkan local notification.
     * @param title Judul notifikasi
     * @param body Isi/pesan notifikasi
     */
    fun showNotification(title: String, body: String)
}
