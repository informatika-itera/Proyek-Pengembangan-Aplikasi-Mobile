package com.dailybliss.app.data.repository

import app.cash.turbine.test
import com.dailybliss.app.domain.model.Moment
import com.dailybliss.app.domain.repository.MomentRepository
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

/**
 * Unit Tests untuk MomentRepository
 * 
 * Testing Guidelines:
 * 1. Gunakan FakeRepository untuk isolasi
 * 2. Test satu behavior per test
 * 3. Gunakan Turbine untuk test Flow
 * 4. Follow AAA pattern (Arrange, Act, Assert)
 */
class MomentRepositoryTest {
    
    private lateinit var repository: FakeMomentRepository
    
    @BeforeTest
    fun setup() {
        repository = FakeMomentRepository()
    }
    
    // ==================== INSERT TESTS ====================
    
    @Test
    fun `insertMoment should return new moment id`() = runTest {
        // Arrange
        val moment = createTestMoment(title = "Test Moment")
        
        // Act
        val id = repository.insertMoment(moment)
        
        // Assert
        assertTrue(id > 0)
    }
    
    @Test
    fun `insertMoment should add moment to list`() = runTest {
        // Arrange
        val moment = createTestMoment(title = "New Moment")
        
        // Act
        repository.insertMoment(moment)
        
        // Assert
        repository.getAllMoments().test {
            val moments = awaitItem()
            assertEquals(1, moments.size)
            assertEquals("New Moment", moments.first().title)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    // ==================== GET TESTS ====================
    
    @Test
    fun `getAllMoments should return all moments`() = runTest {
        // Arrange
        repository.insertMoment(createTestMoment(title = "Moment 1"))
        repository.insertMoment(createTestMoment(title = "Moment 2"))
        
        // Act & Assert
        repository.getAllMoments().test {
            val moments = awaitItem()
            assertEquals(2, moments.size)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `getMomentById should return correct moment`() = runTest {
        // Arrange
        val id = repository.insertMoment(createTestMoment(title = "Find Me"))
        
        // Act & Assert
        repository.getMomentById(id).test {
            val moment = awaitItem()
            assertNotNull(moment)
            assertEquals("Find Me", moment.title)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `getMomentById should return null for non-existent id`() = runTest {
        // Act & Assert
        repository.getMomentById(999).test {
            val moment = awaitItem()
            assertEquals(null, moment)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    // ==================== SEARCH TESTS ====================
    
    @Test
    fun `searchMoments should find moments by title`() = runTest {
        // Arrange
        repository.insertMoment(createTestMoment(title = "Kotlin Tutorial"))
        repository.insertMoment(createTestMoment(title = "Java Guide"))
        
        // Act & Assert
        repository.searchMoments("Kotlin").test {
            val moments = awaitItem()
            assertEquals(1, moments.size)
            assertEquals("Kotlin Tutorial", moments.first().title)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `searchMoments should find moments by content`() = runTest {
        // Arrange
        repository.insertMoment(createTestMoment(title = "Recipe", content = "Add tomatoes"))
        repository.insertMoment(createTestMoment(title = "Shopping", content = "Buy milk"))
        
        // Act & Assert
        repository.searchMoments("tomatoes").test {
            val moments = awaitItem()
            assertEquals(1, moments.size)
            assertEquals("Recipe", moments.first().title)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    // ==================== DELETE TESTS ====================
    
    @Test
    fun `deleteMoment should remove moment from list`() = runTest {
        // Arrange
        val id = repository.insertMoment(createTestMoment(title = "To Delete"))
        
        // Act
        repository.deleteMoment(id)
        
        // Assert
        repository.getAllMoments().test {
            val moments = awaitItem()
            assertTrue(moments.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    // ==================== UPDATE TESTS ====================
    
    @Test
    fun `updateMoment should modify existing moment`() = runTest {
        // Arrange
        val id = repository.insertMoment(createTestMoment(title = "Original"))
        
        // Act
        val updatedMoment = createTestMoment(id = id, title = "Updated")
        repository.updateMoment(updatedMoment)
        
        // Assert
        repository.getMomentById(id).test {
            val moment = awaitItem()
            assertEquals("Updated", moment?.title)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    // ==================== HELPER FUNCTIONS ====================
    
    private fun createTestMoment(
        id: Long = 0,
        title: String = "Test",
        content: String = "Content"
    ): Moment {
        return Moment(
            id = id,
            title = title,
            content = content,
            imageUrl = null,
            isPinned = false,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
    }
}

/**
 * Fake Repository untuk Testing
 * 
 * In-memory implementation yang tidak bergantung pada database.
 * Digunakan untuk unit testing tanpa side effects.
 */
class FakeMomentRepository : MomentRepository {
    
    private val moments = MutableStateFlow<List<Moment>>(emptyList())
    private var nextId = 1L
    
    override fun getAllMoments(): Flow<List<Moment>> = moments
    
    override fun getPinnedMoments(): Flow<List<Moment>> {
        return moments.map { list -> list.filter { it.isPinned } }
    }
    
    override fun searchMoments(query: String): Flow<List<Moment>> {
        return moments.map { list ->
            list.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.content.contains(query, ignoreCase = true)
            }
        }
    }
    
    override fun getMomentById(id: Long): Flow<Moment?> {
        return moments.map { list -> list.find { it.id == id } }
    }
    
    override suspend fun insertMoment(moment: Moment): Long {
        val id = nextId++
        val newMoment = moment.copy(id = id)
        moments.update { it + newMoment }
        return id
    }
    
    override suspend fun updateMoment(moment: Moment) {
        moments.update { list ->
            list.map { if (it.id == moment.id) moment else it }
        }
    }
    
    override suspend fun deleteMoment(id: Long) {
        moments.update { list -> list.filter { it.id != id } }
    }
    
    override suspend fun deleteMoments(ids: List<Long>) {
        moments.update { list -> list.filter { it.id !in ids } }
    }
}
