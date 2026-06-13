package com.example.Roomie.data.repository

import com.example.Roomie.data.local.datastore.UserPreferences
import io.github.jan.supabase.SupabaseClient
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class SupabaseAuthRepositoryImplTest {

    private val mockSupabase = mockk<SupabaseClient>(relaxed = true)
    private val mockPrefs = mockk<UserPreferences>(relaxed = true)
    private lateinit var repository: SupabaseAuthRepositoryImpl

    @BeforeTest
    fun setup() {
        repository = SupabaseAuthRepositoryImpl(mockSupabase, mockPrefs)
    }

    @Test
    fun `getCurrentUser - coverage check`() {
        every { mockPrefs.userData } returns flowOf(null)
        repository.getCurrentUser()
        verify { mockPrefs.userData }
    }

    @Test
    fun `logout - coverage check`() = runTest {
        repository.logout()
        coVerify { mockPrefs.clearUser() }
    }

    @Test
    fun `login - trigger catch block for coverage`() = runTest {
        val result = repository.login("wrong")
        assertTrue(result.isFailure)
    }
}
