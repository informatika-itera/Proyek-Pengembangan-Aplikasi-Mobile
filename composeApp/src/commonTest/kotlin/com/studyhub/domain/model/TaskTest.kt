package com.studyhub.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class TaskTest {

    @Test
    fun `given valid status string when fromString then returns correct TaskStatus`() {
        assertEquals(TaskStatus.TODO, TaskStatus.fromString("todo"))
        assertEquals(TaskStatus.IN_PROGRESS, TaskStatus.fromString("in_progress"))
        assertEquals(TaskStatus.DONE, TaskStatus.fromString("done"))
    }

    @Test
    fun `given invalid status string when fromString then returns TODO as fallback`() {
        assertEquals(TaskStatus.TODO, TaskStatus.fromString("invalid"))
        assertEquals(TaskStatus.TODO, TaskStatus.fromString(""))
    }
}
