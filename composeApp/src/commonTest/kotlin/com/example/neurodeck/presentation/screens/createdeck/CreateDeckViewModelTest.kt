package com.example.neurodeck.presentation.screens.createdeck

import com.example.neurodeck.fakes.FakeDeckRepository
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
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CreateDeckViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var deckRepo: FakeDeckRepository

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(dispatcher)
        deckRepo = FakeDeckRepository()
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `state awal kosong dan tidak bisa save`() = runTest {
        val vm = CreateDeckViewModel(deckRepo)
        val state = vm.uiState.value
        assertEquals("", state.title)
        assertEquals("", state.description)
        assertFalse(state.canSave, "Title kosong => tidak boleh save")
    }

    @Test
    fun `canSave false kalau title kurang dari 3 karakter`() = runTest {
        val vm = CreateDeckViewModel(deckRepo)
        vm.onTitleChange("ab")
        assertFalse(vm.uiState.value.canSave)
    }

    @Test
    fun `canSave true kalau title minimal 3 karakter`() = runTest {
        val vm = CreateDeckViewModel(deckRepo)
        vm.onTitleChange("Kalkulus")
        assertTrue(vm.uiState.value.canSave)
    }

    @Test
    fun `onTitleChange membatasi panjang ke MAX_TITLE_LENGTH`() = runTest {
        val vm = CreateDeckViewModel(deckRepo)
        val tooLong = "x".repeat(CreateDeckUiState.MAX_TITLE_LENGTH + 50)
        vm.onTitleChange(tooLong)
        assertEquals(CreateDeckUiState.MAX_TITLE_LENGTH, vm.uiState.value.title.length)
    }

    @Test
    fun `saveDeck sukses memanggil repository dan onSuccess dengan id baru`() = runTest {
        val vm = CreateDeckViewModel(deckRepo)
        vm.onTitleChange("  Pemrograman Mobile  ")
        vm.onDescriptionChange("  Materi KMP  ")

        var receivedId: Long? = null
        vm.saveDeck { receivedId = it }

        assertNotNull(receivedId, "onSuccess harus dipanggil dengan id deck baru")
        // ViewModel harus trim sebelum kirim ke repository
        assertEquals("Pemrograman Mobile", deckRepo.lastCreatedTitle)
        assertEquals("Materi KMP", deckRepo.lastCreatedDescription)
    }

    @Test
    fun `saveDeck tidak melakukan apa-apa kalau canSave false`() = runTest {
        val vm = CreateDeckViewModel(deckRepo)
        vm.onTitleChange("ab") // < 3 char

        var called = false
        vm.saveDeck { called = true }

        assertFalse(called)
        assertEquals(0, deckRepo.createCallCount, "Repository tidak boleh dipanggil")
    }

    @Test
    fun `saveDeck gagal menampilkan errorMessage dan reset isSaving`() = runTest {
        deckRepo.throwOnCreate = true
        val vm = CreateDeckViewModel(deckRepo)
        vm.onTitleChange("Deck Error")

        var successCalled = false
        vm.saveDeck { successCalled = true }

        val state = vm.uiState.value
        assertFalse(successCalled)
        assertNotNull(state.errorMessage, "Error harus tampil saat save gagal")
        assertFalse(state.isSaving, "isSaving harus kembali false setelah error")
    }

    @Test
    fun `onTitleChange menghapus error message sebelumnya`() = runTest {
        deckRepo.throwOnCreate = true
        val vm = CreateDeckViewModel(deckRepo)
        vm.onTitleChange("Deck")
        vm.saveDeck { }
        assertNotNull(vm.uiState.value.errorMessage)

        // Mengetik lagi harus clear error
        vm.onTitleChange("Deck Baru")
        assertNull(vm.uiState.value.errorMessage)
    }
}
