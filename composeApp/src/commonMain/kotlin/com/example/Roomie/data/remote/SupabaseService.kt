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
            val fileName = "${Clock.System.now().toEpochMilliseconds()}.jpg"
            val fullPath = "$folderName/$fileName"
            val bucket = client.storage.from(bucketName)
            
            Napier.d("Supabase: Menyiapkan upload ke $bucketName/$fullPath")
            
            // Mencoba upload langsung
            try {
                bucket.upload(path = fullPath, data = imageBytes, upsert = true)
            } catch (uploadError: Exception) {
                Napier.e("Supabase: Upload bytes gagal: ${uploadError.message}")
                // Jika error 403 atau semacamnya, mungkin perlu cek policy RLS di Supabase
                throw uploadError
            }

            // Ambil URL publik
            val url = try {
                bucket.publicUrl(fullPath)
            } catch (urlError: Exception) {
                // Fallback manual URL construction
                "https://sgxhjpequhgiifmdbnkt.supabase.co/storage/v1/object/public/$bucketName/$fullPath"
            }
            
            // Tambahkan cache buster
            val finalUrl = if (url.contains("?")) "$url&t=${Clock.System.now().toEpochMilliseconds()}" 
                           else "$url?t=${Clock.System.now().toEpochMilliseconds()}"
            
            Napier.d("Supabase: Upload berhasil! URL: $finalUrl")
            finalUrl
        } catch (e: Exception) {
            Napier.e("Supabase Upload Error Utama: ${e.message}", e)
            // Cek apakah error karena session expired
            null
        }
    }

    override suspend fun uploadReportImage(imageBytes: ByteArray): String? = 
        uploadImage("roomie-images", "reports", imageBytes)

    override suspend fun uploadAvatar(imageBytes: ByteArray): String? =
        uploadImage("roomie-images", "avatars", imageBytes)
}
