package com.example.neurodeck.presentation.screens.editcard

import com.example.neurodeck.domain.model.Card
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

class EditCardViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var cardRepo: FakeCardRepository
    private val cardId = 3L

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(dispatcher)
        cardRepo = FakeCardRepository()
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init memuat kartu yang ada dan mengisi form`() = runTest {
        cardRepo.cardByIdResult = Card(
            id = cardId,
            deckId = 1L,
            front = "Front lama",
            back = "Back lama",
        )

        val vm = EditCardViewModel(cardId, cardRepo)
        val state = vm.uiState.value

        assertFalse(state.isLoading, "Loading selesai setelah init")
        assertEquals("Front lama", state.front)
        assertEquals("Back lama", state.back)
        assertEquals(null, state.errorMessage)
    }

    @Test
    fun `init menampilkan error kalau kartu tidak ditemukan`() = runTest {
        cardRepo.cardByIdResult = null

        val vm = EditCardViewModel(cardId, cardRepo)
        val state = vm.uiState.value

        assertFalse(state.isLoading)
        assertNotNull(state.errorMessage, "Kartu null => error message")
    }

    @Test
    fun `init menampilkan error kalau load melempar exception`() = runTest {
        cardRepo.throwOnGetById = true

        val vm = EditCardViewModel(cardId, cardRepo)

        assertFalse(vm.uiState.value.isLoading)
        assertNotNull(vm.uiState.value.errorMessage)
    }

    @Test
    fun `canSave true setelah load sukses dan field terisi`() = runTest {
        cardRepo.cardByIdResult = Card(id = cardId, deckId = 1L, front = "f", back = "b")
        val vm = EditCardViewModel(cardId, cardRepo)
        assertTrue(vm.uiState.value.canSave)
    }

    @Test
    fun `canSave false kalau field dikosongkan`() = runTest {
        cardRepo.cardByIdResult = Card(id = cardId, deckId = 1L, front = "f", back = "b")
        val vm = EditCardViewModel(cardId, cardRepo)
        vm.onFrontChange("")
        assertFalse(vm.uiState.value.canSave)
    }

    @Test
    fun `saveCard memanggil updateCardContent dengan konten ter-trim`() = runTest {
        cardRepo.cardByIdResult = Card(id = cardId, deckId = 1L, front = "f", back = "b")
        val vm = EditCardViewModel(cardId, cardRepo)
        vm.onFrontChange("  Front baru  ")
        vm.onBackChange("  Back baru  ")

        var success = false
        vm.saveCard { success = true }

        assertTrue(success)
        assertEquals(Triple(cardId, "Front baru", "Back baru"), cardRepo.lastUpdatedContent)
    }

    @Test
    fun `saveCard gagal menampilkan error dan reset isSaving`() = runTest {
        cardRepo.cardByIdResult = Card(id = cardId, deckId = 1L, front = "f", back = "b")
        cardRepo.throwOnUpdate = true
        val vm = EditCardViewModel(cardId, cardRepo)
        vm.onFrontChange("Front")
        vm.onBackChange("Back")

        var success = false
        vm.saveCard { success = true }

        assertFalse(success)
        assertNotNull(vm.uiState.value.errorMessage)
        assertFalse(vm.uiState.value.isSaving)
    }
}
