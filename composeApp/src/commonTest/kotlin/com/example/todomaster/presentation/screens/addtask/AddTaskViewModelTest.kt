package com.example.todomaster.presentation.screens.addtask

import com.example.todomaster.data.repository.FakeTaskRepository
import com.example.todomaster.data.remote.api.GeminiService
import com.example.todomaster.domain.usecase.AddTaskUseCase
import io.ktor.client.HttpClient
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AddTaskViewModelTest {

    @Test
    fun `test add task viewmodel initialization`() = runTest {
        val repo = FakeTaskRepository()
        val useCase = AddTaskUseCase(repo)
        val gemini = GeminiService(HttpClient()) // Klien dummy

        val viewModel = AddTaskViewModel(useCase, repo, gemini)

        viewModel.title = "Tes Judul"
        assertEquals("Tes Judul", viewModel.title)
    }
}