package com.studymate.core.network

expect object ApiConfig {
    val geminiApiKey: String
    val googleWebClientId: String
}

object ApiConstants {
    const val GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/v1beta"
    const val GEMINI_MODEL = "gemini-1.5-flash"

    object Prompts {
        fun refineNote(rawNote: String): String {
            val isShort = rawNote.split(" ").size < 50
            
            return if (isShort) {
                """
                    Tolong berikan penjelasan mendalam mengenai teks berikut:
                    
                    ${rawNote}
                    
                    Instruksi:
                    - Berikan penjelasan yang mudah dipahami.
                    - Karena teks ini pendek, buatkan penjelasan naratif saja jangan dibuat ringkasan poin-poin.
                    - Gunakan Bahasa Indonesia.
                """.trimIndent()
            } else {
                """
                    Tolong ubah teks mentah berikut menggunakan format AI Smart Refine:
                    
                    ${rawNote}
                    
                    Instruksi:
                    - Berikan Ringkasan terstruktur (poin per poin yang jelas).
                    - Berikan Poin-poin detail (bullet points) untuk setiap inti pembahasan.
                    - Berikan Glosarium istilah teknis atau penting beserta definisinya.
                    - Gunakan Bahasa Indonesia.
                    - Gunakan format output sebagai berikut:
                    ## Ringkasan Terstruktur
                    [Isi ringkasan]
                    
                    ## Poin-Poin Detail
                    - [Poin 1]
                    - [Poin 2]
                    
                    ## Glosarium
                    - [Istilah]: [Definisi]
                """.trimIndent()
            }
        }

        fun generateQuiz(noteContent: String): String {
            return """
                Buatlah 5 soal pilihan ganda berdasarkan materi berikut:
                
                ${noteContent}
                
                Instruksi Penting:
                - Prioritaskan membuat soal dari ISI CATATAN secara mendalam.
                - Pastikan soal menanyakan konsep kunci yang ada dalam materi.
                - Output harus dalam format JSON murni.
                - Struktur JSON: { "questions": [{ "question": "", "options": ["", "", "", ""], "correct": 0, "explanation": "" }] }
                - Field "correct" adalah index (0-3) dari jawaban yang benar.
                - Kembalikan HANYA JSON tanpa teks penjelasan lain di awal atau akhir.
            """.trimIndent()
        }
    }
}
