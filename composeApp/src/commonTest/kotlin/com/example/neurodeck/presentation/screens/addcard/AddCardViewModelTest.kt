package com.example.neurodeck.presentation.screens.addcard

import com.example.neurodeck.fakes.FakeCardRepository
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

class AddCardViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var cardRepo: FakeCardRepository
    private val deckId = 7L

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(dispatcher)
        cardRepo = FakeCardRepository()
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    private fun newVm() = AddCardViewModel(deckId, cardRepo)

    @Test
    fun `state awal tidak bisa save`() = runTest {
        assertFalse(newVm().uiState.value.canSave)
    }

    @Test
    fun `canSave false kalau hanya front yang terisi`() = runTest {
        val vm = newVm()
        vm.onFrontChange("Apa itu KMP?")
        assertFalse(vm.uiState.value.canSave, "Back masih kosong")
    }

    @Test
    fun `canSave true kalau front dan back terisi`() = runTest {
        val vm = newVm()
        vm.onFrontChange("Apa itu KMP?")
        vm.onBackChange("Kotlin Multiplatform")
        assertTrue(vm.uiState.value.canSave)
    }

    @Test
    fun `canSave false kalau front hanya whitespace`() = runTest {
        val vm = newVm()
        vm.onFrontChange("   ")
        vm.onBackChange("jawaban")
        assertFalse(vm.uiState.value.canSave, "isBlank front => tidak boleh save")
    }

    @Test
    fun `saveCard memanggil repository dengan deckId dan konten ter-trim`() = runTest {
        val vm = newVm()
        vm.onFrontChange("  Apa itu Koin?  ")
        vm.onBackChange("  Dependency Injection framework  ")

        var success = false
        vm.saveCard { success = true }

        assertTrue(success)
        assertEquals(deckId, cardRepo.lastCreatedDeckId)
        assertEquals("Apa itu Koin?", cardRepo.lastCreatedFront)
        assertEquals("Dependency Injection framework", cardRepo.lastCreatedBack)
    }

    @Test
    fun `saveCard tidak jalan kalau canSave false`() = runTest {
        val vm = newVm()
        vm.onFrontChange("hanya front")

        var success = false
        vm.saveCard { success = true }

        assertFalse(success)
        assertEquals(null, cardRepo.lastCreatedFront)
    }

    @Test
    fun `saveCard gagal menampilkan error dan reset isSaving`() = runTest {
        cardRepo.throwOnCreate = true
        val vm = newVm()
        vm.onFrontChange("front")
        vm.onBackChange("back")

        var success = false
        vm.saveCard { success = true }

        val state = vm.uiState.value
        assertFalse(success)
        assertNotNull(state.errorMessage)
        assertFalse(state.isSaving)
    }
}
