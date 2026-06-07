package com.studyhub.domain.usecase.task

import com.studyhub.domain.fake.TaskBuilder
import com.studyhub.domain.model.Priority
import com.studyhub.domain.model.SortBy
import com.studyhub.domain.model.TaskStatus
import kotlin.test.*

class FilterAndSortTasksUseCaseTest {
    private val useCase = FilterAndSortTasksUseCase()
    private val tasks = listOf(
        TaskBuilder.build(id = "1", title = "B", subject = "Math", priority = Priority.HIGH, status = TaskStatus.TODO),
        TaskBuilder.build(id = "2", title = "A", subject = "Physics", priority = Priority.LOW, status = TaskStatus.DONE),
        TaskBuilder.build(id = "3", title = "C", subject = "Biology", priority = Priority.MEDIUM, status = TaskStatus.TODO)
    )

    @Test
    fun `given showCompleted false when filter then excludes DONE tasks`() {
        val result = useCase(tasks, showCompleted = false)
        assertTrue(result.none { it.status == TaskStatus.DONE })
        assertEquals(2, result.size)
    }

    @Test
    fun `given showCompleted true when filter then includes DONE tasks`() {
        val result = useCase(tasks, showCompleted = true)
        assertTrue(result.any { it.status == TaskStatus.DONE })
        assertEquals(3, result.size)
    }

    @Test
    fun `given searchQuery when filter then matches title`() {
        // "A" matches Title "A" (id 2) and Subject "Math" (id 1)
        val result = useCase(tasks, searchQuery = "A", showCompleted = true)
        assertEquals(2, result.size)
    }

    @Test
    fun `given subject filter when filter then matches subject`() {
        val result = useCase(tasks, filterSubject = "Physics", showCompleted = true)
        assertEquals(1, result.size)
        assertEquals("Physics", result[0].subject)
    }

    @Test
    fun `given priority filter when filter then matches priority`() {
        val result = useCase(tasks, filterPriority = Priority.HIGH)
        assertEquals(1, result.size)
        assertEquals(Priority.HIGH, result[0].priority)
    }

    @Test
    fun `given sort by title when filter then ordered correctly`() {
        val result = useCase(tasks, sortBy = SortBy.TITLE, showCompleted = true)
        assertEquals("A", result[0].title)
        assertEquals("B", result[1].title)
        assertEquals("C", result[2].title)
    }

    @Test
    fun `given sort by priority when filter then ordered correctly`() {
        val result = useCase(tasks, sortBy = SortBy.PRIORITY, showCompleted = true)
        assertEquals(Priority.HIGH, result[0].priority)
        assertEquals(Priority.MEDIUM, result[1].priority)
        assertEquals(Priority.LOW, result[2].priority)
    }
}
