package com.studymate.core.network

expect object ApiConfig {
    val groqApiKey: String
    val googleWebClientId: String
}

object ApiConstants {
    const val GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/v1beta"
    const val GEMINI_MODEL = "gemini-1.5-flash"

    object Prompts {
        fun refineNote(subject: String, title: String, content: String): String {
            return """
                Bertindaklah sebagai asisten akademik cerdas. Tugas Anda adalah melakukan "Refine" pada catatan mahasiswa.
                
                KONTEKS:
                Mata Kuliah: $subject
                Judul Materi: $title
                Isi Catatan Mentah: ${if (content.isBlank()) "(Kosong)" else content}
                
                INSTRUKSI REFINE:
                1. Hubungkan materi dengan kurikulum standar Mata Kuliah $subject dan topik $title.
                2. Perbaiki isi catatan agar sangat relevan dengan judul dan mata kuliah tersebut.
                3. Jika isi catatan mentah kosong atau sedikit, berikan penjelasan komprehensif mengenai apa yang biasanya dipelajari pada topik $title di mata kuliah $subject.
                4. Berikan informasi "beken" atau fakta menarik terbaru yang sedang tren terkait materi ini (hal yang mungkin belum sempat dicatat mahasiswa).
                5. Gunakan Bahasa Indonesia yang akademis namun tetap asik dibaca.
                
                FORMAT OUTPUT (Markdown):
                ## 📘 Ringkasan Materi
                [Penjelasan materi yang terstruktur]
                
                ## 📝 Poin-Poin Utama
                - [Poin penting 1]
                - [Poin penting 2]
                
                ## 💡 Informasi Beken & Wawasan Tambahan
                [Hal menarik/tren/fakta tambahan yang belum banyak diketahui]
                
                ## 📚 Glosarium Istilah
                - [Istilah]: [Definisi singkat]
            """.trimIndent()
        }

        fun generateQuiz(subject: String, title: String, noteContent: String): String {
            return """
                Buatlah 5 soal pilihan ganda (soal HOTS - Higher Order Thinking Skills) berdasarkan materi berikut.
                
                PRIORITAS SUMBER (Urutan Kepentingan):
                1. Mata Kuliah: $subject
                2. Judul Materi: $title
                3. Isi Catatan: $noteContent
                
                Instruksi Penting:
                - Soal harus menantang dan menguji pemahaman konsep, bukan sekadar hafalan.
                - Output harus dalam format JSON murni.
                - Struktur JSON: { "questions": [{ "question": "", "options": ["", "", "", ""], "correct": 0, "explanation": "" }] }
                - Field "correct" adalah index (0-3) dari jawaban yang benar.
                - Field "explanation" menjelaskan kenapa jawaban tersebut benar.
                - Kembalikan HANYA JSON tanpa teks penjelasan lain di awal atau akhir.
            """.trimIndent()
        }
    }
}
