package com.itera.news.core.util

/**
 * Mengembalikan waktu saat ini dalam milidetik (epoch).
 * Diimplementasikan per platform karena KMP tidak memiliki
 * System.currentTimeMillis() secara langsung di commonMain.
 */
expect fun currentTimeMillis(): Long
