package com.example.Roomie.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import io.github.aakira.napier.Napier
import kotlinx.datetime.Clock

interface SupabaseService {
    suspend fun uploadReportImage(imageBytes: ByteArray): String?
    suspend fun uploadAvatar(imageBytes: ByteArray): String?
}

class SupabaseServiceImpl(
    private val client: SupabaseClient
) : SupabaseService {
    /**
     * Upload image to specified bucket
     */
    private suspend fun uploadImage(bucketName: String, folderName: String, imageBytes: ByteArray): String? {
        return try {
            val fileName = "$folderName/${Clock.System.now().toEpochMilliseconds()}.jpg"
            val bucket = client.storage.from(bucketName)
            
            Napier.d("Supabase: Uploading to $bucketName/$fileName")
            
            bucket.upload(path = fileName, data = imageBytes, upsert = true)

            val url = bucket.publicUrl(fileName)
            Napier.d("Supabase: Upload success! URL: $url")
            url
        } catch (e: Exception) {
            Napier.e("Supabase Upload Error: ${e.message}", e)
            null
        }
    }

    override suspend fun uploadReportImage(imageBytes: ByteArray): String? = 
        uploadImage("roomie-images", "reports", imageBytes)

    override suspend fun uploadAvatar(imageBytes: ByteArray): String? =
        uploadImage("roomie-images", "avatars", imageBytes)
}
