package com.example.nutriscan.presentation.screens.consultation

import com.example.nutriscan.data.repository.FakeConsultationRepository
import com.example.nutriscan.data.repository.FakeSessionRepository
import com.example.nutriscan.data.repository.FakeUserProfileRepository
import com.example.nutriscan.domain.model.Conversation
import com.example.nutriscan.domain.model.Nutritionist
import com.example.nutriscan.domain.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
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
class ConsultationViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private lateinit var consultationRepository: FakeConsultationRepository
    private lateinit var sessionRepository: FakeSessionRepository
    private lateinit var profileRepository: FakeUserProfileRepository
    private lateinit var viewModel: ConsultationViewModel

    private fun profile(name: String = "Budi") = UserProfile(
        name = name,
        age = 25,
        weight = 70f,
        height = 170f
    )

    private fun nutritionist(
        id: String = "nutri-test",
        price: Int = 30
    ) = Nutritionist(
        id = id,
        name = "Dr. Test",
        specialty = "Gizi Klinik",
        bio = "Bio ahli gizi",
        experienceYears = 5,
        rating = 4.8,
        reviewCount = 100,
        pricePerChat = price
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(dispatcher)

        consultationRepository = FakeConsultationRepository()
        sessionRepository = FakeSessionRepository()
        profileRepository = FakeUserProfileRepository()

        viewModel = ConsultationViewModel(
            consultationRepository = consultationRepository,
            sessionRepository = sessionRepository,
            userProfileRepository = profileRepository
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
    fun `repository fake menyediakan nutritionist`() {
        val list = consultationRepository.getNutritionists()

        assertTrue(list.isNotEmpty())
    }

    @Test
    fun `startConsultation membuat conversation baru`() = runTest {
        profileRepository.saveProfile(profile("Cahya"))

        var opened = false

        viewModel.startConsultation(
            nutritionist = nutritionist(id = "nutri-new", price = 30),
            onOpen = { opened = true }
        )

        advanceUntilIdle()

        assertTrue(opened)
        assertTrue(consultationRepository.currentConversations().isNotEmpty())
    }

    @Test
    fun `startConsultation dengan coin tidak cukup tidak membuka chat`() = runTest {
        var opened = false

        viewModel.startConsultation(
            nutritionist = nutritionist(id = "nutri-expensive", price = 9999),
            onOpen = { opened = true }
        )

        advanceUntilIdle()

        assertTrue(!opened)
    }

    @Test
    fun `startConsultation conversation yang sudah ada langsung membuka chat`() = runTest {
        consultationRepository.addConversation(
            Conversation(
                id = 10L,
                nutritionistId = "nutri-existing",
                nutritionistName = "Dr. Existing",
                nutritionistSpecialty = "Gizi Klinik",
                userName = "Pengguna",
                createdAt = 1L,
                lastMessage = "Halo",
                lastMessageAt = 1L
            )
        )

        var opened = false

        viewModel.startConsultation(
            nutritionist = nutritionist(id = "nutri-existing", price = 30),
            onOpen = { opened = true }
        )

        advanceUntilIdle()

        assertTrue(opened)
    }

    @Test
    fun `consumeError aman dipanggil`() = runTest {
        viewModel.consumeError()

        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value)
    }
}