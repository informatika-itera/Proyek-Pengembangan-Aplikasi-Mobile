package com.example.neurodeck.domain.model

/**
 * Pengaturan pengingat belajar harian.
 *
 * @property enabled  True kalau reminder aktif.
 * @property hour     Jam (0-23) kapan notifikasi muncul.
 * @property minute   Menit (0-59).
 */
data class ReminderSettings(
    val enabled: Boolean = false,
    val hour: Int = DEFAULT_HOUR,
    val minute: Int = DEFAULT_MINUTE,
) {
    /** Format "HH:mm", contoh "19:00". */
    val formatted: String
        get() = hour.toString().padStart(2, '0') + ":" + minute.toString().padStart(2, '0')

    companion object {
        const val DEFAULT_HOUR = 19
        const val DEFAULT_MINUTE = 0
    }
}