package com.example.todomaster.domain.usecase

import com.example.todomaster.data.repository.FakeTaskRepository
import com.example.todomaster.domain.model.Quadrant
import com.example.todomaster.domain.model.Task
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class AddTaskUseCaseTest {
    private val repository = FakeTaskRepository()
    private val useCase = AddTaskUseCase(repository)

    @Test
    fun `do not allow more than 5 Do First tasks`() = runTest {
        repeat(5) {
            useCase(createTestTask("Task $it", Quadrant.DO_FIRST))
        }

        val result = useCase(createTestTask("Task 6", Quadrant.DO_FIRST))

        assertFalse(result.isSuccess)
    }

    private fun createTestTask(title: String, priority: Quadrant) = Task(
        title = title, priority = priority, createdAt = Clock.System.now().toEpochMilliseconds()
    )
}