package com.example.inventra.data.repository

import com.example.inventra.core.network.SupabaseClientProvider
import com.example.inventra.data.remote.dto.InsertItemDto
import io.github.jan.supabase.postgrest.postgrest

/**
 * Seed data inventaris HMIF 2026 ke Supabase (Updated).
 */
object DataSeeder {

    private val db = SupabaseClientProvider.client.postgrest

    suspend fun seedIfEmpty() {
        try {
            val existing = db["items"].select().decodeList<Map<String, Any>>()
            if (existing.isNotEmpty()) return 
            seedItems()
        } catch (e: Exception) {
            // SEED: Gagal seed data
        }
    }

    private suspend fun seedItems() {
        val items = listOf(
            InsertItemDto(
                name = "Obat-obatan (Set)",
                description = "Isi: Dexaharsen, Mefenamic Acid, Amoxicillin, Paracetamol, Omeprazole, Rhemafar, Dramamine, Ambroxol, Alleron, Promag, Insto",
                category = "MEDICAL",
                location = "Kost Regina (https://maps.app.goo.gl/MvZC5ac5WoQNUQNw9)",
                totalStock = 1, availableStock = 1, condition = "GOOD", picName = "Revania"
            ),
            InsertItemDto(
                name = "Perlengkapan P3K (Set)",
                description = "Isi: Emergency blanket, Kasa (gulungan/steril/mini), Alkohol swab, Ekaplast, Plaster (luka/roll/perekat/antiseptik), Betadine, Penjepit, Gunting, Rivanol, Hot in cream, Oralite, Aseptic wipe, Tolakangin, Fresh care, Minyak angin, Saleb radang, Perban elastis, Serbet, Oxycan, Selang & Cairan Infus",
                category = "MEDICAL",
                location = "Kost Regina (https://maps.app.goo.gl/MvZC5ac5WoQNUQNw9)",
                totalStock = 1, availableStock = 1, condition = "GOOD", picName = "Revania"
            ),
            InsertItemDto(
                name = "Konsumsi (Set)",
                description = "Isi: Kopi, Teh, Gula, Cup",
                category = "FOOD",
                location = "Kost Regina (https://maps.app.goo.gl/MvZC5ac5WoQNUQNw9)",
                totalStock = 1, availableStock = 1, condition = "GOOD", picName = "Revania"
            ),
            InsertItemDto(
                name = "Speaker",
                description = "Speaker portable",
                category = "ELECTRONICS",
                location = "Kost Gabriel",
                totalStock = 1, availableStock = 1, condition = "GOOD", picName = "Revania"
            ),
            InsertItemDto(
                name = "Mic",
                description = "Microphone",
                category = "ELECTRONICS",
                location = "-",
                totalStock = 2, availableStock = 2, condition = "GOOD", picName = "Revania"
            ),
            InsertItemDto(
                name = "TOA",
                description = "Pengeras suara",
                category = "ELECTRONICS",
                location = "Kost Vania (https://maps.app.goo.gl/54zzWdoRQ9bsrFEGA)",
                totalStock = 1, availableStock = 1, condition = "GOOD", picName = "Revania"
            ),
            InsertItemDto(
                name = "HT (Handy Talky)",
                description = "HT untuk koordinasi",
                category = "ELECTRONICS",
                location = "-",
                totalStock = 11, availableStock = 11, condition = "GOOD", picName = "Revania"
            ),
            InsertItemDto(
                name = "Map Kertas Biru",
                description = "Map biru HMIF",
                category = "OTHER",
                location = "-",
                totalStock = 33, availableStock = 33, condition = "GOOD", picName = "Revania"
            ),
            InsertItemDto(
                name = "Konfetti",
                description = "Konfetti dekorasi",
                category = "OTHER",
                location = "-",
                totalStock = 2, availableStock = 2, condition = "GOOD", picName = "Revania"
            ),
            InsertItemDto(
                name = "Paper Bag Kecil",
                description = "Paper bag merchandise",
                category = "OTHER",
                location = "-",
                totalStock = 5, availableStock = 5, condition = "GOOD", picName = "Revania"
            ),
            InsertItemDto(
                name = "Kertas Sertifikat",
                description = "Kertas 1 rim",
                category = "OTHER",
                location = "-",
                totalStock = 1, availableStock = 1, condition = "GOOD", picName = "Revania"
            ),
            InsertItemDto(
                name = "Map Folder",
                description = "Map folder dokumen",
                category = "OTHER",
                location = "-",
                totalStock = 1, availableStock = 1, condition = "GOOD", picName = "Revania"
            ),
            InsertItemDto(
                name = "Bendera HMIF",
                description = "Bendera ormawa",
                category = "FLAG",
                location = "-",
                totalStock = 1, availableStock = 1, condition = "GOOD", picName = "Revania"
            ),
            InsertItemDto(
                name = "Bendera Merah Putih",
                description = "Bendera Nasional",
                category = "FLAG",
                location = "-",
                totalStock = 1, availableStock = 1, condition = "GOOD", picName = "Revania"
            ),
            InsertItemDto(
                name = "Palu Sidang",
                description = "Palu sidang rapat",
                category = "OTHER",
                location = "-",
                totalStock = 1, availableStock = 1, condition = "GOOD", picName = "Revania"
            ),
            InsertItemDto(
                name = "Sertifikat Ramahtamah (HIMAFA)",
                description = "Sertifikat ASCLEGIEIA",
                category = "OTHER",
                location = "-",
                totalStock = 1, availableStock = 1, condition = "GOOD", picName = "Revania"
            ),
            InsertItemDto(
                name = "Surat Komitmen LKMO",
                description = "6 kelompok surat",
                category = "OTHER",
                location = "-",
                totalStock = 6, availableStock = 6, condition = "GOOD", picName = "Revania"
            ),
            InsertItemDto(
                name = "Bambu",
                description = "Bambu tiang",
                category = "OTHER",
                location = "Kost Gabriel",
                totalStock = 7, availableStock = 7, condition = "GOOD", picName = "Revania"
            ),
            InsertItemDto(
                name = "Giant Flag HMIF",
                description = "Bendera ukuran raksasa",
                category = "FLAG",
                location = "-",
                totalStock = 1, availableStock = 1, condition = "GOOD", picName = "Revania"
            ),
            InsertItemDto(
                name = "Stand Bendera",
                description = "Dudukan bendera (set)",
                category = "FLAG",
                location = "Kost Vania (https://maps.app.goo.gl/54zzWdoRQ9bsrFEGA)",
                totalStock = 2, availableStock = 2, condition = "GOOD", picName = "Revania"
            ),
            InsertItemDto(
                name = "Baterai",
                description = "1 pack baterai",
                category = "ELECTRONICS",
                location = "Kost Willy",
                totalStock = 1, availableStock = 1, condition = "GOOD", picName = "Revania"
            )
        )

        items.forEach { dto ->
            try {
                db["items"].insert(dto)
            } catch (e: Exception) {
                // SEED: Gagal insert
            }
        }
    }
}
