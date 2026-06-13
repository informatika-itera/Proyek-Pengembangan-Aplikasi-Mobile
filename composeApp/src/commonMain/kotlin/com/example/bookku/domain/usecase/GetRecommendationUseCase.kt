package com.example.bookku.domain.usecase

import com.example.bookku.domain.repository.AIRepository
import com.example.bookku.domain.repository.NoteRepository
import com.example.bookku.domain.model.Book
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class GetRecommendationUseCase(
    private val aiRepository: AIRepository,
    private val noteRepository: NoteRepository
) {
    operator fun invoke(book: Book): Flow<Result<String>> = flow {
        val bookId = book.id
        
        // 1. Ambil dari cache jika tersedia dan TIDAK KOSONG
        val cached = noteRepository.getCachedRecommendation(bookId).firstOrNull()
        if (cached != null && cached.isNotBlank()) {
            emit(Result.success(cached))
            return@flow
        }

        // 2. Ambil konteks buku lain
        val allBooks = noteRepository.getAllNotes().first()
        
        val otherBooks = allBooks
            .filter { it.id != bookId }
            .take(5)
            .joinToString("\n") { "- ${it.title} oleh ${it.author} (${it.category.displayName})" }

        val prompt = if (otherBooks.isNotBlank()) {
            """
                Anda adalah asisten pustakawan cerdas "Buku-San" dari aplikasi Bookku.
                
                PENGGUNA MEMILIKI KOLEKSI BERIKUT:
                $otherBooks
                
                BUKU YANG SEDANG DILIHAT:
                Judul: "${book.title}"
                Penulis: ${book.author}
                Kategori: ${book.category.displayName}
                Deskripsi: ${book.content.ifBlank { "Tidak ada deskripsi tersedia." }}
                
                TUGAS:
                1. Berikan ulasan singkat (2 kalimat) mengapa buku "${book.title}" ini menarik bagi pengguna berdasarkan koleksi buku lainnya yang mereka miliki.
                2. Berikan 1 rekomendasi buku lain yang belum ada di koleksi mereka tapi relevan.
                
                Gunakan Bahasa Indonesia yang ramah dan puitis. Maksimal 4 kalimat.
            """.trimIndent()
        } else {
            """
                Anda adalah asisten pustakawan cerdas "Buku-San" dari aplikasi Bookku.
                
                BUKU YANG SEDANG DILIHAT:
                Judul: "${book.title}"
                Penulis: ${book.author}
                Kategori: ${book.category.displayName}
                Deskripsi: ${book.content.ifBlank { "Tidak ada deskripsi tersedia." }}
                
                TUGAS:
                1. Berikan ulasan singkat (2 kalimat) mengapa buku "${book.title}" ini menarik secara umum.
                2. Berikan 1 rekomendasi buku lain yang serupa dengan genre ${book.category.displayName}.
                
                Gunakan Bahasa Indonesia yang ramah dan puitis. Maksimal 4 kalimat.
            """.trimIndent()
        }
        
        val remoteResult = aiRepository.chat(prompt)
        
        remoteResult.fold(
            onSuccess = { newRecommendation ->
                if (newRecommendation.isNotBlank()) {
                    noteRepository.saveRecommendation(bookId, newRecommendation)
                    emit(Result.success(newRecommendation))
                } else {
                    emit(Result.failure(Exception("AI memberikan respons kosong")))
                }
            },
            onFailure = { error ->
                emit(Result.failure(error))
            }
        )
    }
}
