package com.example.inventra.data.repository

import com.example.inventra.core.network.SupabaseClientProvider
import com.example.inventra.data.remote.dto.InsertItemDto
import io.github.jan.supabase.postgrest.postgrest

/**
 * MNAUFALFAKMAL
 * Seed data inventaris HMIF 2026 ke Supabase.
 * Dipanggil sekali dari DashboardViewModel saat pertama kali login.
 */
object DataSeeder {

    private val db = SupabaseClientProvider.client.postgrest

    suspend fun seedIfEmpty() {
        try {
            val existing = db["items"].select().decodeList<Map<String, Any>>()
            if (existing.isNotEmpty()) return // sudah ada data, skip
            seedItems()
            println("SEED: Data inventaris berhasil di-seed")
        } catch (e: Exception) {
            println("SEED: Gagal seed data — ${e.message}")
        }
    }

    private suspend fun seedItems() {
        val items = listOf(
            // Perlengkapan Medis
            InsertItemDto(
                name = "Obat-obatan (Set Lengkap)",
                description = "Dexaharsen, Mefenamic Acid, Amoxicillin, Paracetamol, Omeprazole, Rhemafar, Dramamine, Ambroxol, Alleron, Promag, Insto",
                category = "MEDICAL",
                location = "Kost Regina — https://maps.app.goo.gl/MvZC5ac5WoQNUQNw9",
                totalStock = 1,
                availableStock = 1,
                condition = "GOOD",
                picName = "Revania"
            ),
            InsertItemDto(
                name = "Perlengkapan P3K",
                description = "Emergency blanket, kasa gulungan, alkohol swab, plaster berbagai jenis, betadine, rivanol, oralite, oxycan, selang infus, cairan infus, dll",
                category = "MEDICAL",
                location = "Kost Regina — https://maps.app.goo.gl/MvZC5ac5WoQNUQNw9",
                totalStock = 1,
                availableStock = 1,
                condition = "GOOD",
                picName = "Revania"
            ),
            InsertItemDto(
                name = "Konsumsi (Kopi, Teh, Gula, Cup)",
                description = "Stok konsumsi: kopi, teh, gula, cup",
                category = "FOOD",
                location = "Kost Regina — https://maps.app.goo.gl/MvZC5ac5WoQNUQNw9",
                totalStock = 1,
                availableStock = 1,
                condition = "GOOD",
                picName = "Revania"
            ),
            InsertItemDto(
                name = "Speaker",
                description = "Speaker portable untuk kegiatan HMIF",
                category = "ELECTRONICS",
                location = "Kost Gabriel",
                totalStock = 1,
                availableStock = 1,
                condition = "GOOD",
                picName = "Gabriel"
            ),
            InsertItemDto(
                name = "Mic",
                description = "Microphone untuk kegiatan HMIF",
                category = "ELECTRONICS",
                location = "Kost Gabriel",
                totalStock = 2,
                availableStock = 2,
                condition = "GOOD",
                picName = "Gabriel"
            ),
            InsertItemDto(
                name = "TOA",
                description = "Pengeras suara TOA untuk kegiatan outdoor",
                category = "ELECTRONICS",
                location = "Kost Vania — https://maps.app.goo.gl/54zzWdoRQ9bsrFEGA",
                totalStock = 1,
                availableStock = 1,
                condition = "GOOD",
                picName = "Vania"
            ),
            InsertItemDto(
                name = "HT (Handy Talky)",
                description = "Handy Talky untuk koordinasi kegiatan",
                category = "ELECTRONICS",
                location = "Kost Vania — https://maps.app.goo.gl/54zzWdoRQ9bsrFEGA",
                totalStock = 11,
                availableStock = 11,
                condition = "GOOD",
                picName = "Vania"
            ),
            InsertItemDto(
                name = "Map Kertas Biru",
                description = "Map kertas warna biru untuk dokumen",
                category = "OTHER",
                location = "Sekre HMIF",
                totalStock = 33,
                availableStock = 33,
                condition = "GOOD",
                picName = "Revania"
            ),
            InsertItemDto(
                name = "Konfetti",
                description = "Konfetti untuk dekorasi acara",
                category = "OTHER",
                location = "Sekre HMIF",
                totalStock = 2,
                availableStock = 2,
                condition = "GOOD",
                picName = "Revania"
            ),
            InsertItemDto(
                name = "Paper Bag Kecil",
                description = "Paper bag kecil untuk keperluan acara",
                category = "OTHER",
                location = "Sekre HMIF",
                totalStock = 5,
                availableStock = 5,
                condition = "GOOD",
                picName = "Revania"
            ),
            InsertItemDto(
                name = "Kertas Sertifikat",
                description = "Kertas sertifikat 1 rim",
                category = "OTHER",
                location = "Sekre HMIF",
                totalStock = 1,
                availableStock = 1,
                condition = "GOOD",
                picName = "Revania"
            ),
            InsertItemDto(
                name = "Map Folder",
                description = "Map folder dokumen",
                category = "OTHER",
                location = "Sekre HMIF",
                totalStock = 1,
                availableStock = 1,
                condition = "GOOD",
                picName = "Revania"
            ),
            InsertItemDto(
                name = "Bendera HMIF",
                description = "Bendera resmi HMIF ITERA",
                category = "FLAG",
                location = "Kost Vania — https://maps.app.goo.gl/54zzWdoRQ9bsrFEGA",
                totalStock = 1,
                availableStock = 1,
                condition = "GOOD",
                picName = "Vania"
            ),
            InsertItemDto(
                name = "Bendera Merah Putih",
                description = "Bendera Merah Putih ukuran besar",
                category = "FLAG",
                location = "Kost Vania — https://maps.app.goo.gl/54zzWdoRQ9bsrFEGA",
                totalStock = 1,
                availableStock = 1,
                condition = "GOOD",
                picName = "Vania"
            ),
            InsertItemDto(
                name = "Palu Sidang",
                description = "Palu sidang untuk rapat resmi HMIF",
                category = "OTHER",
                location = "Sekre HMIF",
                totalStock = 1,
                availableStock = 1,
                condition = "GOOD",
                picName = "Revania"
            ),
            InsertItemDto(
                name = "Bambu",
                description = "Bambu untuk keperluan dekorasi/kegiatan",
                category = "OTHER",
                location = "Kost Gabriel",
                totalStock = 7,
                availableStock = 7,
                condition = "GOOD",
                picName = "Gabriel"
            ),
            InsertItemDto(
                name = "Giant Flag HMIF",
                description = "Bendera HMIF ukuran besar (giant flag)",
                category = "FLAG",
                location = "Sekre HMIF",
                totalStock = 1,
                availableStock = 1,
                condition = "GOOD",
                picName = "Revania"
            ),
            InsertItemDto(
                name = "Stand Bendera",
                description = "Stand/tiang bendera set (2 set)",
                category = "OTHER",
                location = "Kost Vania — https://maps.app.goo.gl/54zzWdoRQ9bsrFEGA",
                totalStock = 2,
                availableStock = 2,
                condition = "GOOD",
                picName = "Vania"
            ),
            InsertItemDto(
                name = "Baterai",
                description = "Baterai 1 pack untuk keperluan elektronik",
                category = "ELECTRONICS",
                location = "Kost Willy",
                totalStock = 1,
                availableStock = 1,
                condition = "GOOD",
                picName = "Willy"
            )
        )

        items.forEach { dto ->
            try {
                db["items"].insert(dto)
            } catch (e: Exception) {
                println("SEED: Gagal insert ${dto.name} — ${e.message}")
            }
        }
    }
}