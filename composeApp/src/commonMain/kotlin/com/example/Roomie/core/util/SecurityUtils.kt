package com.example.Roomie.core.util

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Utilitas keamanan sederhana untuk mengaburkan data sensitif di lokal.
 * Untuk produksi asli, disarankan menggunakan AES encryption.
 */
@OptIn(ExperimentalEncodingApi::class)
object SecurityUtils {
    
    fun obfuscate(data: String): String {
        return Base64.encode(data.encodeToByteArray())
    }

    fun deobfuscate(obfuscated: String): String {
        return try {
            Base64.decode(obfuscated).decodeToString()
        } catch (e: Exception) {
            ""
        }
    }
}
