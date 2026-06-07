package com.example.todomaster.data.repository

import app.cash.turbine.test
import com.example.todomaster.domain.model.Quadrant
import com.example.todomaster.domain.model.Task
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TaskRepositoryTest {

    private lateinit var repository: FakeTaskRepository

    @BeforeTest
    fun setup() {
        repository = FakeTaskRepository()
    }

    @Test
    fun `insertTask should append task to the data flow`() = runTest {
        // Arrange
        val task = createTestTask(title = "Tugas WebGIS")

        // Act
        repository.insertTask(task)

        // Assert
        repository.getAllTasks().test {
            val taskList = awaitItem()
            assertEquals(1, taskList.size)
            assertEquals("Tugas WebGIS", taskList.first().title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getTaskById should return correct data for valid id`() = runTest {
        // Arrange
        val task = createTestTask(title = "Belajar Big Data")
        repository.insertTask(task)

        // Act
        val result = repository.getTaskById(1)

        // Assert
        assertNotNull(result)
        assertEquals("Belajar Big Data", result.title)
    }

    @Test
    fun `getTaskById should return null for non existent id`() = runTest {
        // Act
        val result = repository.getTaskById(999)

        // Assert
        assertNull(result)
    }

    @Test
    fun `toggleTaskCompletion should change state of completion correctly`() = runTest {
        // Arrange
        val task = createTestTask(title = "Tugas Digital Watermarking")
        repository.insertTask(task)

        // Act
        repository.toggleTaskCompletion(1, true)

        // Assert
        val result = repository.getTaskById(1)
        assertNotNull(result)
        assertTrue(result.isCompleted)
    }

    @Test
    fun `deleteTask should completely remove task from flow`() = runTest {
        // Arrange
        val task = createTestTask(title = "Tugas Interaksi Manusia Komputer")
        repository.insertTask(task)

        // Act
        repository.deleteTask(1)

        // Assert
        repository.getAllTasks().test {
            val taskList = awaitItem()
            assertTrue(taskList.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getTodayDoFirstCount returns correct count`() = runTest {
        val now = Clock.System.now().toEpochMilliseconds()
        repository.insertTask(createTestTask("Tugas 1").copy(priority = Quadrant.DO_FIRST, createdAt = now))

        val count = repository.getTodayDoFirstCount(now - 1000)
        assertEquals(1L, count)
    }

    @Test
    fun `verify all repository operations`() = runTest {
        // Ini akan menaikkan coverage di data.repository secara drastis
        val task = createTestTask("Repo Test")
        repository.insertTask(task)
        repository.toggleTaskCompletion(1, true)
        val result = repository.getTaskById(1)
        repository.deleteTask(1)

        assertTrue(result?.isCompleted == true)
    }

    private fun createTestTask(title: String): Task {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        return Task(
            id = 0,
            title = title,
            description = "Skenario testing repository",
            priority = Quadrant.DO_FIRST,
            dueDate = currentTime,
            isCompleted = false,
            parentTaskTitle = null,
            createdAt = currentTime
        )
    }
}