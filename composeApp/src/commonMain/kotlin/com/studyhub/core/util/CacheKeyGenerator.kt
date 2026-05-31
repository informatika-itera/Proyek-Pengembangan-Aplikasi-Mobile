package com.studyhub.core.util

import com.studyhub.domain.model.Task
import okio.ByteString.Companion.toByteString

object CacheKeyGenerator {

    fun forPriority(tasks: List<Task>): String {
        val sortedIds = tasks
            .map { it.id.take(8) }
            .sorted()
            .joinToString(",")
        return hash("priority:$sortedIds")
    }

    fun forReminder(task: Task, historySize: Int): String =
        hash("reminder:${task.id.take(8)}:$historySize")

    private fun hash(input: String): String =
        input.encodeToByteArray().toByteString().sha256().hex().take(16)
}
