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

        fun generateQuiz(subject: String, title: String, noteContent: String, questionCount: Int): String {
            return """
                Create $questionCount multiple choice questions in Indonesian about $subject.
                Topic: $title
                Content: $noteContent
                
                Mandatory Rules:
                1. Focus strictly on the subject: $subject.
                2. Every question must have exactly 4 options.
                3. ALL 4 options for each question MUST be unique and different from each other. Do NOT repeat the same text in options.
                4. Output JSON only. No extra text.
                
                JSON Structure:
                {
                  "questions": [
                    {
                      "question": "text",
                      "options": ["opt1", "opt2", "opt3", "opt4"],
                      "correct": 0,
                      "explanation": "why"
                    }
                  ]
                }
            """.trimIndent()
        }
    }
}
