package com.example.todomaster.data.repository

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.example.todomaster.data.local.TaskDatabase
import com.example.todomaster.domain.model.Quadrant
import com.example.todomaster.domain.model.Task
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals

class RepositoryIntegrationTest {

    @Test
    fun `test real repository integration`() = runTest {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        TaskDatabase.Schema.create(driver)
        val db = TaskDatabase(driver)
        val repository = TaskRepositoryImpl(db)

        repository.insertTask(Task(title = "Tes Integrasi", priority = Quadrant.DO_FIRST, createdAt = 0L))
        val tasks = repository.getAllTasks().first()

        assertEquals(1, tasks.size)
    }
}