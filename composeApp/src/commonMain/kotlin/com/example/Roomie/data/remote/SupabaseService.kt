package com.example.Roomie.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import kotlinx.datetime.Clock

class SupabaseService(
    private val client: SupabaseClient
) {
    suspend fun uploadReportImage(imageBytes: ByteArray): String? {
        return try {
            val fileName = "report_${Clock.System.now().toEpochMilliseconds()}.jpg"
            val bucket = client.storage.from("roomie-images")
            
            bucket.upload(path = fileName, data = imageBytes, upsert = true)

            bucket.publicUrl(fileName)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
