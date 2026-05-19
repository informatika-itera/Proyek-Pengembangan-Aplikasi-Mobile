package com.example.bookku.data.repository

import app.cash.turbine.test
import com.example.bookku.domain.model.Book
import com.example.bookku.domain.model.BookGenre
import com.example.bookku.domain.model.BookRating
import com.example.bookku.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class NoteRepositoryTest {
    
    private lateinit var repository: FakeNoteRepository
    
    @BeforeTest
    fun setup() {
        repository = FakeNoteRepository()
    }
    
    @Test
    fun `addBook should return new book id`() = runTest {
        val book = createTestNote(title = "Test book")
        val id = repository.addBook(book)
        assertTrue(id > 0)
    }
    
    @Test
    fun `addBook should add book to list`() = runTest {
        val book = createTestNote(title = "New book")
        repository.addBook(book)
        
        repository.getAllNotes().test {
            val books = awaitItem()
            assertEquals(1, books.size)
            assertEquals("New book", books.first().title)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `getAllNotes should return all books`() = runTest {
        repository.addBook(createTestNote(title = "book 1"))
        repository.addBook(createTestNote(title = "book 2"))
        
        repository.getAllNotes().test {
            val books = awaitItem()
            assertEquals(2, books.size)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `getBookById should return correct book`() = runTest {
        val id = repository.addBook(createTestNote(title = "Find Me"))
        
        repository.getBookById(id).test {
            val book = awaitItem()
            assertNotNull(book)
            assertEquals("Find Me", book.title)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `getBookById should return null for non-existent id`() = runTest {
        repository.getBookById(999).test {
            val book = awaitItem()
            assertEquals(null, book)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `searchNotes should find books by title`() = runTest {
        repository.addBook(createTestNote(title = "Kotlin Tutorial"))
        repository.addBook(createTestNote(title = "Java Guide"))
        
        repository.searchNotes("Kotlin").test {
            val books = awaitItem()
            assertEquals(1, books.size)
            assertEquals("Kotlin Tutorial", books.first().title)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `searchNotes should find books by content`() = runTest {
        repository.addBook(createTestNote(title = "Recipe", content = "Add tomatoes"))
        repository.addBook(createTestNote(title = "Shopping", content = "Buy milk"))
        
        repository.searchNotes("tomatoes").test {
            val books = awaitItem()
            assertEquals(1, books.size)
            assertEquals("Recipe", books.first().title)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `deleteBook should remove book from list`() = runTest {
        val id = repository.addBook(createTestNote(title = "To Delete"))
        repository.deleteBook(id)
        
        repository.getAllNotes().test {
            val books = awaitItem()
            assertTrue(books.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `updateBook should modify existing book`() = runTest {
        val id = repository.addBook(createTestNote(title = "Original"))
        val updatedNote = createTestNote(id = id, title = "Updated")
        repository.updateBook(updatedNote)
        
        repository.getBookById(id).test {
            val book = awaitItem()
            assertEquals("Updated", book?.title)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    private fun createTestNote(
        id: Long = 0,
        title: String = "Test",
        content: String = "Content",
        category: BookGenre = BookGenre.GENERAL
    ): Book {
        return Book(
            id = id,
            title = title,
            content = content,
            category = category,
            color = BookRating.DEFAULT,
            isPinned = false,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
    }
}

class FakeNoteRepository : NoteRepository {
    
    private val books = MutableStateFlow<List<Book>>(emptyList())
    private var nextId = 1L
    
    override fun getAllNotes(): Flow<List<Book>> = books
    
    override fun getPinnedNotes(): Flow<List<Book>> {
        return books.map { list -> list.filter { it.isPinned } }
    }
    
    override fun getBooksByCategory(category: BookGenre): Flow<List<Book>> {
        return books.map { list -> list.filter { it.category == category } }
    }
    
    override fun searchNotes(query: String): Flow<List<Book>> {
        return books.map { list ->
            list.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.content.contains(query, ignoreCase = true)
            }
        }
    }
    
    override fun getBookById(id: Long): Flow<Book?> {
        return books.map { list -> list.find { it.id == id } }
    }
    
    override suspend fun addBook(book: Book): Long {
        val id = nextId++
        val newNote = book.copy(id = id)
        books.update { it + newNote }
        return id
    }
    
    override suspend fun updateBook(book: Book) {
        books.update { list ->
            list.map { if (it.id == book.id) book else it }
        }
    }
    
    override suspend fun deleteBook(id: Long) {
        books.update { list -> list.filter { it.id != id } }
    }
    
    override suspend fun togglePinNote(id: Long) {
        books.update { list ->
            list.map { 
                if (it.id == id) it.copy(isPinned = !it.isPinned) else it 
            }
        }
    }
    
    override suspend fun deleteBooks(ids: List<Long>) {
        books.update { list -> list.filter { it.id !in ids } }
    }
}
