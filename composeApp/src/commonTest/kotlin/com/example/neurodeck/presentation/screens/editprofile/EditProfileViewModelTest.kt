package com.example.neurodeck.presentation.screens.editprofile

import com.example.neurodeck.domain.model.UserProfile
import com.example.neurodeck.fakes.FakeUserPreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class EditProfileViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var prefsRepo: FakeUserPreferencesRepository

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(dispatcher)
        prefsRepo = FakeUserPreferencesRepository()
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init memuat profil yang ada ke form`() = runTest {
        prefsRepo.profileFlow.value = UserProfile(
            name = "Fajri",
            username = "@fajri",
            bio = "Mahasiswa Informatika",
        )

        val vm = EditProfileViewModel(prefsRepo)
        val state = vm.uiState.value

        assertFalse(state.isLoading)
        assertEquals("Fajri", state.name)
        assertEquals("@fajri", state.username)
        assertEquals("Mahasiswa Informatika", state.bio)
    }

    @Test
    fun `canSave false kalau name kosong`() = runTest {
        val vm = EditProfileViewModel(prefsRepo)
        vm.onNameChange("")
        assertFalse(vm.uiState.value.canSave)
    }

    @Test
    fun `canSave true kalau name terisi dan sudah selesai load`() = runTest {
        val vm = EditProfileViewModel(prefsRepo)
        vm.onNameChange("Nama Baru")
        assertTrue(vm.uiState.value.canSave)
    }

    @Test
    fun `onUsernameChange auto-prepend at sign`() = runTest {
        val vm = EditProfileViewModel(prefsRepo)
        vm.onUsernameChange("fajri")
        assertEquals("@fajri", vm.uiState.value.username)
    }

    @Test
    fun `onUsernameChange tidak menambah at sign kalau sudah ada`() = runTest {
        val vm = EditProfileViewModel(prefsRepo)
        vm.onUsernameChange("@sudahada")
        assertEquals("@sudahada", vm.uiState.value.username)
    }

    @Test
    fun `onNameChange membatasi panjang sesuai MAX_NAME_LENGTH`() = runTest {
        val vm = EditProfileViewModel(prefsRepo)
        vm.onNameChange("a".repeat(UserProfile.MAX_NAME_LENGTH + 20))
        assertEquals(UserProfile.MAX_NAME_LENGTH, vm.uiState.value.name.length)
    }

    @Test
    fun `save menyimpan profil ter-trim dan set isSaved`() = runTest {
        prefsRepo.profileFlow.value = UserProfile(name = "Lama", username = "@lama")
        val vm = EditProfileViewModel(prefsRepo)
        vm.onNameChange("  Fajri Firdaus  ")
        vm.onBioChange("  Backend dev  ")

        vm.save()

        val saved = prefsRepo.savedProfile
        assertNotNull(saved)
        assertEquals("Fajri Firdaus", saved.name)
        assertEquals("Backend dev", saved.bio)
        assertTrue(vm.uiState.value.isSaved, "isSaved harus true untuk trigger navigate back")
    }

    @Test
    fun `save mempertahankan memberSince original`() = runTest {
        val original = UserProfile(name = "Lama")
        prefsRepo.profileFlow.value = original
        val vm = EditProfileViewModel(prefsRepo)
        vm.onNameChange("Baru")

        vm.save()

        assertEquals(original.memberSince, prefsRepo.savedProfile?.memberSince)
    }

    @Test
    fun `save gagal menampilkan error dan reset isSaving`() = runTest {
        prefsRepo.throwOnSave = true
        val vm = EditProfileViewModel(prefsRepo)
        vm.onNameChange("Nama")

        vm.save()

        assertNotNull(vm.uiState.value.errorMessage)
        assertFalse(vm.uiState.value.isSaving)
        assertFalse(vm.uiState.value.isSaved)
    }
}
