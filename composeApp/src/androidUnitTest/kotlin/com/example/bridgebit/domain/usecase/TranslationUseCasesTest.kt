package com.example.bridgebit.domain.usecase

import app.cash.turbine.test
import com.example.bridgebit.domain.model.Translation
import com.example.bridgebit.domain.repository.TranslationRepository
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TranslationUseCasesTest {

    private lateinit var repository: TranslationRepository

    @Before
    fun setup() {
        repository = mockk()
    }

    private fun createTranslation(
        id: Long = 0L,
        sourceText: String = "Hello",
        translatedText: String = "Halo"
    ) = Translation(
        id = id,
        sourceText = sourceText,
        translatedText = translatedText,
        createdAt = 1L,
        updatedAt = 1L
    )

    @Test
    fun getAllHistory_returnsData() = runTest {
        val data = listOf(createTranslation())

        every { repository.getAllHistory() } returns flowOf(data)

        val useCase = GetAllHistoryUseCase(repository)

        useCase().test {
            assertEquals(data, awaitItem())
            awaitComplete()
        }

        verify(exactly = 1) {
            repository.getAllHistory()
        }
    }

    @Test
    fun getVaultPhrases_returnsData() = runTest {
        val data = listOf(createTranslation())

        every { repository.getVaultPhrases() } returns flowOf(data)

        val useCase = GetVaultPhrasesUseCase(repository)

        useCase().test {
            assertEquals(data, awaitItem())
            awaitComplete()
        }

        verify(exactly = 1) {
            repository.getVaultPhrases()
        }
    }

    @Test
    fun searchHistory_blankQuery_callsGetAllHistory() = runTest {
        val data = listOf(createTranslation())

        every { repository.getAllHistory() } returns flowOf(data)

        val useCase = SearchHistoryUseCase(repository)

        useCase("").test {
            assertEquals(data, awaitItem())
            awaitComplete()
        }

        verify(exactly = 1) {
            repository.getAllHistory()
        }

        verify(exactly = 0) {
            repository.searchHistory(any())
        }
    }

    @Test
    fun searchHistory_nonBlank_callsSearchHistory() = runTest {
        val data = listOf(createTranslation())

        every {
            repository.searchHistory("hello")
        } returns flowOf(data)

        val useCase = SearchHistoryUseCase(repository)

        useCase("hello").test {
            assertEquals(data, awaitItem())
            awaitComplete()
        }

        verify(exactly = 1) {
            repository.searchHistory("hello")
        }
    }

    @Test
    fun saveTranslation_insertNewTranslation_success() = runTest {
        val translation = createTranslation(id = 0L)

        coEvery {
            repository.insertTranslation(translation)
        } returns 100L

        val useCase = SaveTranslationUseCase(repository)

        val result = useCase(translation)

        assertTrue(result.isSuccess)
        assertEquals(100L, result.getOrNull())

        coVerify(exactly = 1) {
            repository.insertTranslation(translation)
        }
    }

    @Test
    fun saveTranslation_updateExistingTranslation_success() = runTest {
        val translation = createTranslation(id = 10L)

        coEvery {
            repository.updateTranslation(translation)
        } just Runs

        val useCase = SaveTranslationUseCase(repository)

        val result = useCase(translation)

        assertTrue(result.isSuccess)
        assertEquals(10L, result.getOrNull())

        coVerify(exactly = 1) {
            repository.updateTranslation(translation)
        }
    }

    @Test
    fun saveTranslation_blankSourceText_returnsFailure() = runTest {
        val translation = createTranslation(
            sourceText = ""
        )

        val useCase = SaveTranslationUseCase(repository)

        val result = useCase(translation)

        assertTrue(result.isFailure)
    }

    @Test
    fun deleteTranslation_success() = runTest {
        coEvery {
            repository.deleteTranslationById(1L)
        } just Runs

        val useCase = DeleteTranslationUseCase(repository)

        val result = useCase(1L)

        assertTrue(result.isSuccess)

        coVerify(exactly = 1) {
            repository.deleteTranslationById(1L)
        }
    }

    @Test
    fun deleteTranslation_repositoryThrows_returnsFailure() = runTest {
        coEvery {
            repository.deleteTranslationById(1L)
        } throws RuntimeException("Database Error")

        val useCase = DeleteTranslationUseCase(repository)

        val result = useCase(1L)

        assertTrue(result.isFailure)
    }

    @Test
    fun toggleVaultStatus_success() = runTest {
        coEvery {
            repository.toggleVaultStatus(1L)
        } just Runs

        val useCase = ToggleVaultStatusUseCase(repository)

        val result = useCase(1L)

        assertTrue(result.isSuccess)

        coVerify(exactly = 1) {
            repository.toggleVaultStatus(1L)
        }
    }

    @Test
    fun toggleVaultStatus_repositoryThrows_returnsFailure() = runTest {
        coEvery {
            repository.toggleVaultStatus(1L)
        } throws RuntimeException("Database Error")

        val useCase = ToggleVaultStatusUseCase(repository)

        val result = useCase(1L)

        assertTrue(result.isFailure)
    }
}