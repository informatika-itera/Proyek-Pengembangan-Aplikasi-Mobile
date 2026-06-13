package com.example.noteai.presentation.screens.chat

import app.cash.turbine.test
import com.example.noteai.domain.repository.AIRepository
import com.example.noteai.domain.repository.WritingStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

//@OptIn(ExperimentalCoroutinesApi::class)
//class ChatViewModelTest {
//    private lateinit var viewModel: ChatViewModel
//    private lateinit var repository: FakeAIRepository
//    private val testDispatcher = StandardTestDispatcher()
//
//    @BeforeTest
//    fun setup() {
//        Dispatchers.setMain(testDispatcher)
//        repository = FakeAIRepository()
//        viewModel = ChatViewModel(repository)
//    }
//
//    @AfterTest
//    fun tearDown() { Dispatchers.resetMain() }
//
//    @Test
//    fun `initial state should have welcome message`() = runTest {
//        viewModel.uiState.test {
//            assertEquals(1, awaitItem().messages.size)
//        }
//    }
//
//    @Test
//    fun `onInputTextChanged should update input text`() = runTest {
//        viewModel.onInputTextChanged("Hello")
//        viewModel.uiState.test {
//            assertEquals("Hello", awaitItem().inputText)
//        }
//    }
//
//    @Test
//    fun `sendMessage should clear input text`() = runTest {
//        viewModel.onInputTextChanged("Hello")
//        viewModel.sendMessage()
//        viewModel.uiState.test {
//            assertEquals("", awaitItem().inputText)
//        }
//    }
//}
//
//class FakeAIRepository : AIRepository {
//    override suspend fun chat(m: String) = Result.success("Hi")
//    override suspend fun summarize(t: String) = Result.success("")
//    override suspend fun generateIdeas(t: String) = Result.success(emptyList<String>())
//    override suspend fun improveWriting(t: String, s: WritingStyle) = Result.success("")
//    override suspend fun translate(t: String, l: String) = Result.success("")
//    override suspend fun suggestTitle(c: String) = Result.success("")
//}
