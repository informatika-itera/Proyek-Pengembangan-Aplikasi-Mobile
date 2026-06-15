package com.itera.news.data.remote.api

/**
 * Analisis sentimen berita MBG secara LOKAL — tanpa API call, tanpa biaya.
 *
 * Strategi: pencocokan keyword bahasa Indonesia yang umum digunakan
 * dalam pemberitaan program Makan Bergizi Gratis.
 *
 * Kategori:
 *  - "Pro"     → berita mendukung / positif terhadap program MBG
 *  - "Kontra"  → berita mengkritik / negatif terhadap program MBG
 *  - "Netral"  → berita informatif / tidak berpihak
 */
object SentimentAnalyzer {

    private val proKeywords = setOf(
        "sukses", "berhasil", "manfaat", "positif", "mendukung", "dukungan",
        "apresiasi", "baik", "bagus", "efektif", "tepat sasaran", "meningkat",
        "tumbuh", "semangat", "antusias", "bangga", "senang", "puas",
        "tercapai", "optimal", "bermanfaat", "lancar", "progres", "kemajuan",
        "sehat", "gizi terpenuhi", "distribusi berjalan", "disambut baik",
        "disambut positif", "mendapat sambutan", "diterima baik"
    )

    private val kontraKeywords = setOf(
        "gagal", "masalah", "kritik", "menolak", "penolakan", "protes",
        "buruk", "tidak efektif", "tidak tepat", "korupsi", "penyelewengan",
        "penyalahgunaan", "kekurangan", "kurang", "mubazir", "basi",
        "tidak layak", "tidak higienis", "keterlambatan", "terlambat",
        "keluhan", "kecewa", "tidak merata", "diskriminasi", "ribet",
        "birokrasi", "hambatan", "kendala serius", "merugi", "sia-sia",
        "anggaran membengkak", "tidak transparan", "bocor"
    )

    /**
     * Tentukan sentimen dari judul + deskripsi berita.
     * Mengembalikan "Pro", "Kontra", atau "Netral".
     * Tidak ada network call — murni komputasi lokal.
     */
    fun analyze(title: String, description: String): String {
        val text = "$title $description".lowercase()

        var proScore = 0
        var kontraScore = 0

        for (keyword in proKeywords) {
            if (text.contains(keyword)) proScore++
        }
        for (keyword in kontraKeywords) {
            if (text.contains(keyword)) kontraScore++
        }

        return when {
            proScore > kontraScore -> "Pro"
            kontraScore > proScore -> "Kontra"
            else -> "Netral"
        }
    }
}