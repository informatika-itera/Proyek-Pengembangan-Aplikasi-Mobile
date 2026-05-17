package com.studymate.core.network

object ApiConfig {
    const val GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/v1beta"
    const val GEMINI_MODEL = "gemini-1.5-flash"

    object Prompts {
        fun refineNote(rawNote: String): String {
            return """
                Tolong perbaiki struktur catatan kuliah yang berantakan berikut ini:
                
                $rawNote
                
                Instruksi:
                - Ubah menjadi bullet points yang rapi dalam Bahasa Indonesia.
                - Tambahkan glosarium untuk istilah teknis yang muncul.
                - Gunakan format output sebagai berikut:
                ## Ringkasan
                [Isi ringkasan]
                
                ## Poin Utama
                - [Poin 1]
                - [Poin 2]
                
                ## Glosarium
                - [Istilah]: [Definisi]
            """.trimIndent()
        }

        fun generateQuiz(refinedNote: String): String {
            return """
                Buatlah 5 soal pilihan ganda berdasarkan catatan berikut:
                
                $refinedNote
                
                Instruksi:
                - Output harus dalam format JSON murni.
                - Struktur JSON: { "questions": [{ "question": "", "options": [], "correct": 0, "explanation": "" }] }
                - Kembalikan HANYA JSON tanpa teks penjelasan lain di awal atau akhir.
            """.trimIndent()
        }
    }
}
