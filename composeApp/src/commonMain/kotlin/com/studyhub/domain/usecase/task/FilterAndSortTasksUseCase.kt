package com.studyhub.domain.usecase.task

import com.studyhub.domain.model.Priority
import com.studyhub.domain.model.SortBy
import com.studyhub.domain.model.Task
import com.studyhub.domain.model.TaskStatus

class FilterAndSortTasksUseCase {
    operator fun invoke(
        tasks: List<Task>,
        filterStatus: TaskStatus? = null,
        filterPriority: Priority? = null,
        filterSubject: String? = null,
        sortBy: SortBy = SortBy.DUE_DATE,
        searchQuery: String = "",
        showCompleted: Boolean = false
    ): List<Task> = tasks
        .filter { task ->
            !task.isDeleted &&
            (showCompleted || task.status != TaskStatus.DONE) &&
            (filterStatus == null || task.status == filterStatus) &&
            (filterPriority == null || task.priority == filterPriority) &&
            (filterSubject == null || task.subject == filterSubject) &&
            (searchQuery.isBlank() ||
                task.title.contains(searchQuery, ignoreCase = true) ||
                task.subject.contains(searchQuery, ignoreCase = true) ||
                task.description.contains(searchQuery, ignoreCase = true))
        }
        .sortedWith(
            compareBy {
                when (sortBy) {
                    SortBy.DUE_DATE -> it.dueDate.toString()
                    SortBy.PRIORITY -> it.priority.ordinal.toString()
                    SortBy.SUBJECT -> it.subject
                    SortBy.TITLE -> it.title
                }
            }
        )
}
