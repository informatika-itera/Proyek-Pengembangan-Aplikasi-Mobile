package com.studyhub.domain.usecase.preferences

import com.studyhub.domain.fake.FakePreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GetDarkModeUseCaseTest {
    private lateinit var fakeRepo: FakePreferencesRepository
    private lateinit var useCase: GetDarkModeUseCase

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        fakeRepo = FakePreferencesRepository()
        useCase = GetDarkModeUseCase(fakeRepo)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given dark mode false when invoke then emits false`() = runTest {
        fakeRepo.isDarkModeValue = false
        val result = useCase().first()
        assertFalse(result)
    }

    @Test
    fun `given dark mode true when invoke then emits true`() = runTest {
        fakeRepo.isDarkModeValue = true
        val result = useCase().first()
        assertTrue(result)
    }
}
