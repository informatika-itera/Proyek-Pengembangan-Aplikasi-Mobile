package com.studyhub.domain.usecase.preferences

import com.studyhub.domain.fake.FakePreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SetDarkModeUseCaseTest {
    private lateinit var fakeRepo: FakePreferencesRepository
    private lateinit var useCase: SetDarkModeUseCase

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        fakeRepo = FakePreferencesRepository()
        useCase = SetDarkModeUseCase(fakeRepo)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given enabled true when invoke then repo setDarkMode called with true`() = runTest {
        useCase(true)
        assertTrue(fakeRepo.setDarkModeCalledWith == true)
    }

    @Test
    fun `given enabled false when invoke then repo setDarkMode called with false`() = runTest {
        useCase(false)
        assertFalse(fakeRepo.setDarkModeCalledWith == true)
    }
}
