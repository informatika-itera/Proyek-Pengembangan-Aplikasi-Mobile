package com.example.nutriscan.domain.usecase

import app.cash.turbine.test
import com.example.nutriscan.data.repository.FakeUserProfileRepository
import com.example.nutriscan.domain.model.Disease
import com.example.nutriscan.domain.model.UserProfile
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UserProfileUseCasesTest {

    private lateinit var repository: FakeUserProfileRepository
    private lateinit var getProfile: GetUserProfileUseCase
    private lateinit var saveProfile: SaveUserProfileUseCase
    private lateinit var updateProfile: UpdateUserProfileUseCase
    private lateinit var deleteProfile: DeleteUserProfileUseCase
    private lateinit var hasProfile: HasUserProfileUseCase

    private fun validProfile(
        name: String = "Budi",
        age: Int = 25,
        weight: Float = 70f,
        height: Float = 170f
    ) = UserProfile(
        name = name,
        age = age,
        weight = weight,
        height = height,
        healthConditions = listOf(Disease.DIABETES)
    )

    @BeforeTest
    fun setup() {
        repository = FakeUserProfileRepository()
        getProfile = GetUserProfileUseCase(repository)
        saveProfile = SaveUserProfileUseCase(repository)
        updateProfile = UpdateUserProfileUseCase(repository)
        deleteProfile = DeleteUserProfileUseCase(repository)
        hasProfile = HasUserProfileUseCase(repository)
    }

    @Test
    fun `saveProfile berhasil untuk data valid`() = runTest {
        val result = saveProfile(validProfile())

        assertTrue(result.isSuccess)
        assertTrue(hasProfile())
    }

    @Test
    fun `saveProfile gagal jika nama kosong`() = runTest {
        val result = saveProfile(validProfile(name = ""))

        assertTrue(result.isFailure)
        assertFalse(hasProfile())
    }

    @Test
    fun `saveProfile gagal jika usia tidak valid`() = runTest {
        val result = saveProfile(validProfile(age = 150))

        assertTrue(result.isFailure)
    }

    @Test
    fun `saveProfile gagal jika berat tidak valid`() = runTest {
        val result = saveProfile(validProfile(weight = 0f))

        assertTrue(result.isFailure)
    }

    @Test
    fun `saveProfile gagal jika tinggi tidak valid`() = runTest {
        val result = saveProfile(validProfile(height = 400f))

        assertTrue(result.isFailure)
    }

    @Test
    fun `getProfile emit profile yang sudah disimpan`() = runTest {
        saveProfile(validProfile(name = "Andi"))

        getProfile().test {
            val profile = awaitItem()
            assertTrue(profile?.name == "Andi")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateProfile mengganti data profile`() = runTest {
        saveProfile(validProfile(name = "Budi"))

        val result = updateProfile(validProfile(name = "Cahya", age = 30))

        assertTrue(result.isSuccess)

        getProfile().test {
            val profile = awaitItem()
            assertTrue(profile?.name == "Cahya")
            assertTrue(profile?.age == 30)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteProfile menghapus profile`() = runTest {
        saveProfile(validProfile())
        assertTrue(hasProfile())

        val result = deleteProfile()

        assertTrue(result.isSuccess)
        assertFalse(hasProfile())
    }
}