package com.example.arcane.presentation

import com.example.arcane.domain.model.Book
import com.example.arcane.domain.model.ReadingStatus
import com.example.arcane.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import com.example.arcane.domain.repository.FolderRepository
import com.example.arcane.domain.model.Folder

class FakeFolderRepository : FolderRepository {
    override suspend fun createFolder(name: String) {}
    override fun getAllFolders(): Flow<List<Folder>> = flowOf(emptyList())
    override suspend fun getFolderById(id: Long): Folder? = null
    override suspend fun updateFolderName(id: Long, newName: String) {}
    override suspend fun deleteFolder(id: Long) {}
    override suspend fun addBookToFolder(bookId: Long, folderId: Long) {}
    override suspend fun removeBookFromFolder(bookId: Long, folderId: Long) {}
    override fun getFoldersForBook(bookId: Long): Flow<List<Folder>> = flowOf(emptyList())
    override fun getBooksInFolder(folderId: Long): Flow<List<Book>> = flowOf(emptyList())
}

class FakeBookRepository : BookRepository {
    private val _books = MutableStateFlow<List<Book>>(emptyList())

    fun setBooks(books: List<Book>) { _books.value = books }

    override fun getAllBooks(): Flow<List<Book>> = _books
    override fun getBooksByStatus(status: ReadingStatus): Flow<List<Book>> =
        flowOf(_books.value.filter { it.readingStatus == status })
    override fun getBookById(id: Long): Flow<Book?> =
        flowOf(_books.value.find { it.id == id })
    override suspend fun getBookByGoogleId(googleBookId: String): Book? =
        _books.value.find { it.googleBookId == googleBookId }
    override suspend fun saveBook(book: Book): Long {
        _books.value = _books.value + book
        return book.id
    }
    override suspend fun deleteBook(id: Long) {
        _books.value = _books.value.filter { it.id != id }
    }
    override suspend fun updateBookStatus(id: Long, status: ReadingStatus) {
        _books.value = _books.value.map {
            if (it.id == id) it.copy(readingStatus = status) else it
        }
    }
    override suspend fun updateBookNotesAndRating(id: Long, notes: String, rating: Int?) {
        _books.value = _books.value.map {
            if (it.id == id) it.copy(notes = notes, rating = rating) else it
        }
    }
    override suspend fun searchBooks(query: String): List<Book> =
        _books.value.filter { it.title.contains(query, ignoreCase = true) }
    override suspend fun getBookDetail(googleBookId: String): Book? = null
}

fun createDummyBook(
    id: Long = 1L,
    googleBookId: String = "google_$id",
    title: String = "Buku Test $id",
    status: ReadingStatus = ReadingStatus.TO_READ
) = Book(
    id = id,
    googleBookId = googleBookId,
    title = title,
    authors = listOf("Penulis Test"),
    description = "Deskripsi test",
    coverUrl = "",
    categories = emptyList(),
    publishedDate = "",
    pageCount = null,
    readingStatus = status,
    notes = "",
    rating = null
)