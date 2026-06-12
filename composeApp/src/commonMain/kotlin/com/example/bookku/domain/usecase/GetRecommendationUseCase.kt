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

        // 2. Ambil konteks buku lain untuk perbandingan
        val similarBooks = noteRepository.getBooksByCategory(book.category).first()
            .filter { it.id != bookId }
            .take(3)
            .joinToString("\n") { "- ${it.title} oleh ${it.author}" }

        val prompt = """
            Anda adalah asisten perpustakaan digital "Bookku" yang cerdas.
            
            Buku saat ini: "${book.title}" (${book.category.displayName})
            Deskripsi: ${book.content}
            
            ${if (similarBooks.isNotBlank()) "Buku serupa lainnya:\n$similarBooks" else ""}
            
            Tugas:
            Berikan wawasan singkat mengapa buku ini menarik dan saran apa yang sebaiknya dibaca selanjutnya dalam 3-4 kalimat.
            Gunakan Bahasa Indonesia yang hangat.
        """.trimIndent()
        
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
