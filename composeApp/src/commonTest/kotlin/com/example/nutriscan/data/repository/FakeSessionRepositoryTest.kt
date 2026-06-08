package com.example.nutriscan.data.repository

import app.cash.turbine.test
import com.example.nutriscan.domain.model.UserRole
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FakeSessionRepositoryTest {

    private lateinit var repository: FakeSessionRepository

    @BeforeTest
    fun setup() {
        repository = FakeSessionRepository()
    }

    @Test
    fun `state awal belum login`() = runTest {
        repository.state.test {
            val state = awaitItem()
            assertFalse(state.isLoggedIn)
            assertEquals(UserRole.USER, state.role)
            assertEquals(100, state.coins)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `login mengubah state menjadi logged in`() = runTest {
        repository.login(UserRole.NUTRITIONIST, "Dr. Sinta")

        repository.state.test {
            val state = awaitItem()
            assertTrue(state.isLoggedIn)
            assertEquals(UserRole.NUTRITIONIST, state.role)
            assertEquals("Dr. Sinta", state.userName)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `logout mengubah isLoggedIn menjadi false`() = runTest {
        repository.login(UserRole.USER, "Budi")
        repository.logout()

        assertFalse(repository.currentState().isLoggedIn)
    }

    @Test
    fun `topUp menambah coin`() = runTest {
        repository.topUp(50)

        assertEquals(150, repository.currentCoins())
        assertEquals(150, repository.currentState().coins)
    }

    @Test
    fun `trySpend berhasil jika coin cukup`() = runTest {
        val result = repository.trySpend(30)

        assertTrue(result)
        assertEquals(70, repository.currentCoins())
    }

    @Test
    fun `trySpend gagal jika coin tidak cukup`() = runTest {
        val result = repository.trySpend(999)

        assertFalse(result)
        assertEquals(100, repository.currentCoins())
    }
}