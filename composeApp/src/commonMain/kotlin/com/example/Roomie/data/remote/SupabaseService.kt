package com.example.Roomie.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import io.github.aakira.napier.Napier
import kotlinx.datetime.Clock

open class SupabaseService(
    private val client: SupabaseClient? = null // Made nullable for easier testing
) {
    open suspend fun uploadReportImage(imageBytes: ByteArray): String? {
        val currentClient = client ?: return null
        return try {
            val fileName = "report_${Clock.System.now().toEpochMilliseconds()}.jpg"
            val bucket = currentClient.storage.from("roomie-images")
            
            Napier.d("Supabase: Memulai upload file $fileName ke bucket roomie-images")
            
            // Mencoba upload tanpa upsert untuk test, dan tambahkan eksplisit content type
            bucket.upload(
                path = fileName, 
                data = imageBytes
            )

            val url = bucket.publicUrl(fileName)
            Napier.d("Supabase: Upload berhasil! URL: $url")
            url
        } catch (e: Exception) {
            Napier.e("Supabase Upload Error: ${e.message}", e)
            e.printStackTrace()
            null
        }
    }
}
