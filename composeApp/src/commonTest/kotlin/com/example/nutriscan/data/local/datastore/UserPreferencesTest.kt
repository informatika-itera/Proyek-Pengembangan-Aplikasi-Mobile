package com.example.nutriscan.data.local.datastore

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UserPreferencesTest {

    private lateinit var preferences: UserPreferences

    @BeforeTest
    fun setup() {
        preferences = UserPreferences(InMemoryDataStore())
    }

    @Test
    fun `dark mode default false`() = runTest {
        preferences.isDarkMode.test {
            assertFalse(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `setDarkMode true mengubah nilai`() = runTest {
        preferences.setDarkMode(true)

        preferences.isDarkMode.test {
            assertTrue(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `sortBy default UPDATED_DESC`() = runTest {
        preferences.sortBy.test {
            assertEquals("UPDATED_DESC", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `setSortBy mengubah sort preference`() = runTest {
        preferences.setSortBy("NAME_ASC")

        preferences.sortBy.test {
            assertEquals("NAME_ASC", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `defaultCategory default GENERAL`() = runTest {
        preferences.defaultCategory.test {
            assertEquals("GENERAL", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `showPreview default true`() = runTest {
        preferences.showPreview.test {
            assertTrue(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onboarding default false lalu bisa completed`() = runTest {
        preferences.isOnboardingCompleted.test {
            assertFalse(awaitItem())

            preferences.setOnboardingCompleted()

            assertTrue(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `login menyimpan session`() = runTest {
        preferences.login(role = "USER", name = "Budi")

        preferences.isLoggedIn.test {
            assertTrue(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        preferences.displayName.test {
            assertEquals("Budi", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        preferences.userRole.test {
            assertEquals("USER", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `logout hanya mengubah isLoggedIn menjadi false`() = runTest {
        preferences.login(role = "USER", name = "Budi")
        preferences.logout()

        preferences.isLoggedIn.test {
            assertFalse(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `addCoins menambah coin`() = runTest {
        preferences.addCoins(50)

        preferences.coins.test {
            assertEquals(150, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `trySpendCoins berhasil jika saldo cukup`() = runTest {
        val success = preferences.trySpendCoins(30)

        assertTrue(success)

        preferences.coins.test {
            assertEquals(70, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `trySpendCoins gagal jika saldo tidak cukup`() = runTest {
        val success = preferences.trySpendCoins(500)

        assertFalse(success)

        preferences.coins.test {
            assertEquals(100, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}