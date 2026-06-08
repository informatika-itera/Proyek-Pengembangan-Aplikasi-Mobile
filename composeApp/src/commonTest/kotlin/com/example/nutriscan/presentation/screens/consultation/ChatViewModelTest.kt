package com.example.nutriscan.presentation.screens.consultation

import com.example.nutriscan.data.repository.FakeConsultationRepository
import com.example.nutriscan.data.repository.FakeSessionRepository
import com.example.nutriscan.domain.model.Conversation
import com.example.nutriscan.domain.model.UserRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private lateinit var consultationRepository: FakeConsultationRepository
    private lateinit var sessionRepository: FakeSessionRepository
    private lateinit var viewModel: ChatViewModel

    private val conversation = Conversation(
        id = 1L,
        nutritionistId = "nutri-1",
        nutritionistName = "Dr. Sinta",
        nutritionistSpecialty = "Gizi Klinik",
        userName = "Budi",
        createdAt = 1L,
        lastMessage = "Halo",
        lastMessageAt = 1L
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(dispatcher)

        consultationRepository = FakeConsultationRepository()
        consultationRepository.addConversation(conversation)

        sessionRepository = FakeSessionRepository()

        viewModel = ChatViewModel(
            conversationId = 1L,
            consultationRepository = consultationRepository,
            sessionRepository = sessionRepository
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `viewModel berhasil dibuat dan memiliki uiState`() = runTest {
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value)
    }

    @Test
    fun `send pesan kosong tidak membuat aplikasi crash`() = runTest {
        viewModel.send("   ")

        advanceUntilIdle()

        assertTrue(consultationRepository.currentMessages().isEmpty())
    }

    @Test
    fun `send pesan valid menambahkan minimal satu pesan`() = runTest {
        viewModel.send("Halo, saya ingin konsultasi nutrisi")

        advanceUntilIdle()
        advanceTimeBy(2_000)
        advanceUntilIdle()

        assertTrue(consultationRepository.currentMessages().isNotEmpty())
    }

    @Test
    fun `pesan pertama dari user memiliki role USER`() = runTest {
        viewModel.send("Saya ingin tanya soal gula")

        advanceUntilIdle()
        advanceTimeBy(2_000)
        advanceUntilIdle()

        val messages = consultationRepository.currentMessages()

        assertTrue(messages.isNotEmpty())
        assertTrue(messages.first().sender == UserRole.USER)
    }
}