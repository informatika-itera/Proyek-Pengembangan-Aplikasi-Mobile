package com.example.bookku.domain.usecase

import com.example.bookku.domain.model.Book
import com.example.bookku.domain.model.BookGenre
import com.example.bookku.domain.repository.AIRepository
import com.example.bookku.domain.repository.NoteRepository
import com.example.bookku.domain.repository.WritingStyle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAllNotesUseCase(
    private val repository: NoteRepository
) {
    operator fun invoke(sortBy: NoteSortBy = NoteSortBy.UPDATED_DESC): Flow<List<Book>> {
        return repository.getAllNotes().map { books ->
            val (pinned, unpinned) = books.partition { it.isPinned }
            val sortedPinned = sortNotes(pinned, sortBy)
            val sortedUnpinned = sortNotes(unpinned, sortBy)
            sortedPinned + sortedUnpinned
        }
    }

    private fun sortNotes(books: List<Book>, sortBy: NoteSortBy): List<Book> {
        return when (sortBy) {
            NoteSortBy.TITLE_ASC -> books.sortedBy { it.title.lowercase() }
            NoteSortBy.TITLE_DESC -> books.sortedByDescending { it.title.lowercase() }
            NoteSortBy.CREATED_ASC -> books.sortedBy { it.createdAt }
            NoteSortBy.CREATED_DESC -> books.sortedByDescending { it.createdAt }
            NoteSortBy.UPDATED_ASC -> books.sortedBy { it.updatedAt }
            NoteSortBy.UPDATED_DESC -> books.sortedByDescending { it.updatedAt }
        }
    }
}

enum class NoteSortBy(val displayName: String) {
    TITLE_ASC("Judul (A-Z)"),
    TITLE_DESC("Judul (Z-A)"),
    CREATED_ASC("Dibuat (Lama)"),
    CREATED_DESC("Dibuat (Baru)"),
    UPDATED_ASC("Diupdate (Lama)"),
    UPDATED_DESC("Diupdate (Baru)")
}

class SearchNotesUseCase(
    private val repository: NoteRepository
) {
    operator fun invoke(query: String, category: BookGenre? = null): Flow<List<Book>> {
        return if (query.isBlank() && category == null) {
            repository.getAllNotes()
        } else if (query.isBlank()) {
            repository.getBooksByCategory(category!!)
        } else {
            repository.searchNotes(query).map { books ->
                if (category != null) {
                    books.filter { it.category == category }
                } else {
                    books
                }
            }
        }
    }
}

class SaveNoteUseCase(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(book: Book): Result<Long> {
        return try {
            if (book.title.isBlank() && book.content.isBlank()) {
                return Result.failure(IllegalArgumentException("book tidak boleh kosong"))
            }

            val id = if (book.id == 0L) {
                repository.addBook(book)
            } else {
                repository.updateBook(book)
                book.id
            }

            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class deleteBookUseCase(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(id: Long): Result<Unit> {
        return try {
            repository.deleteBook(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class SummarizeNoteUseCase(
    private val aiRepository: AIRepository
) {
    suspend operator fun invoke(content: String): Result<String> {
        if (content.length < 50) {
            return Result.failure(IllegalArgumentException("Konten terlalu pendek untuk diringkas"))
        }
        return aiRepository.summarize(content)
    }
}

class ImproveWritingUseCase(
    private val aiRepository: AIRepository
) {
    suspend operator fun invoke(content: String, style: WritingStyle = WritingStyle.NEUTRAL): Result<String> {
        if (content.isBlank()) {
            return Result.failure(IllegalArgumentException("Konten tidak boleh kosong"))
        }
        return aiRepository.improveWriting(content, style)
    }
}

class GenerateIdeasUseCase(
    private val aiRepository: AIRepository
) {
    suspend operator fun invoke(topic: String): Result<List<String>> {
        if (topic.isBlank()) {
            return Result.failure(IllegalArgumentException("Topik tidak boleh kosong"))
        }
        return aiRepository.generateIdeas(topic)
    }
}