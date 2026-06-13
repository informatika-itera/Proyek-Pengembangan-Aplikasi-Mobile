package com.example.neurodeck.presentation.screens.importgenerate

import com.example.neurodeck.fakes.FakeAIRepository
import com.example.neurodeck.fakes.FakeCardRepository
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
import kotlin.test.assertTrue

class ImportGenerateViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var deckRepo: FakeDeckRepository
    private lateinit var cardRepo: FakeCardRepository
    private lateinit var aiRepo: FakeAIRepository
    private val deckId = 5L

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(dispatcher)
        deckRepo = FakeDeckRepository()
        cardRepo = FakeCardRepository()
        aiRepo = FakeAIRepository()
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    private fun newVm(id: Long = deckId) = ImportGenerateViewModel(id, deckRepo, cardRepo, aiRepo)

    private val validMaterial = "Kotlin Multiplatform memungkinkan berbagi kode antar platform."

    @Test
    fun `state awal di phase Input dan tidak bisa generate`() = runTest {
        val vm = newVm()
        assertEquals(GeneratePhase.Input, vm.uiState.value.phase)
        assertFalse(vm.uiState.value.canGenerate, "Material kosong")
    }

    @Test
    fun `canGenerate false kalau material terlalu pendek`() = runTest {
        val vm = newVm()
        vm.onMaterialChange("pendek")
        assertFalse(vm.uiState.value.canGenerate)
    }

    @Test
    fun `canGenerate true kalau material memenuhi panjang minimal`() = runTest {
        val vm = newVm()
        vm.onMaterialChange(validMaterial)
        assertTrue(vm.uiState.value.canGenerate)
    }

    @Test
    fun `onCardCountChange di-clamp ke rentang valid`() = runTest {
        val vm = newVm()
        vm.onCardCountChange(100)
        assertEquals(ImportGenerateUiState.MAX_CARD_COUNT, vm.uiState.value.cardCount)
        vm.onCardCountChange(1)
        assertEquals(ImportGenerateUiState.MIN_CARD_COUNT, vm.uiState.value.cardCount)
    }

    @Test
    fun `generate sukses pindah ke Preview dengan drafts`() = runTest {
        aiRepo.flashcardsResult = listOf(
            "Apa itu KMP?" to "Kotlin Multiplatform",
            "Apa itu Compose?" to "UI toolkit deklaratif",
        )
        val vm = newVm()
        vm.onMaterialChange(validMaterial)

        vm.generate()

        val state = vm.uiState.value
        assertEquals(GeneratePhase.Preview, state.phase)
        assertEquals(2, state.drafts.size)
        assertEquals("Apa itu KMP?", state.drafts[0].front)
        assertTrue(state.canSave, "Preview dengan drafts => bisa save")
        // Draft punya id unik
        assertTrue(state.drafts.map { it.id }.toSet().size == 2)
    }

    @Test
    fun `generate dengan hasil AI kosong pindah ke Error`() = runTest {
        aiRepo.flashcardsResult = emptyList()
        val vm = newVm()
        vm.onMaterialChange(validMaterial)

        vm.generate()

        assertEquals(GeneratePhase.Error, vm.uiState.value.phase)
        assertNotNull(vm.uiState.value.errorMessage)
    }

    @Test
    fun `generate yang melempar exception pindah ke Error`() = runTest {
        aiRepo.throwOnGenerate = true
        val vm = newVm()
        vm.onMaterialChange(validMaterial)

        vm.generate()

        assertEquals(GeneratePhase.Error, vm.uiState.value.phase)
        assertNotNull(vm.uiState.value.errorMessage)
    }

    @Test
    fun `deleteDraft menghapus draft sesuai id`() = runTest {
        aiRepo.flashcardsResult = listOf("A" to "1", "B" to "2", "C" to "3")
        val vm = newVm()
        vm.onMaterialChange(validMaterial)
        vm.generate()

        val idToDelete = vm.uiState.value.drafts[1].id
        vm.deleteDraft(idToDelete)

        val drafts = vm.uiState.value.drafts
        assertEquals(2, drafts.size)
        assertFalse(drafts.any { it.id == idToDelete })
    }

    @Test
    fun `onDraftFrontChange memperbarui front draft tertentu`() = runTest {
        aiRepo.flashcardsResult = listOf("A" to "1")
        val vm = newVm()
        vm.onMaterialChange(validMaterial)
        vm.generate()

        val id = vm.uiState.value.drafts[0].id
        vm.onDraftFrontChange(id, "Front diedit")

        assertEquals("Front diedit", vm.uiState.value.drafts[0].front)
    }

    @Test
    fun `saveAll menyimpan drafts valid lalu pindah ke Done`() = runTest {
        aiRepo.flashcardsResult = listOf("A" to "1", "B" to "2")
        val vm = newVm()
        vm.onMaterialChange(validMaterial)
        vm.generate()

        vm.saveAll()

        assertEquals(GeneratePhase.Done, vm.uiState.value.phase)
        assertEquals(2, vm.uiState.value.savedCardsCount)
        assertEquals(deckId, cardRepo.lastCreatedDeckId)
        assertEquals(2, cardRepo.lastBulkCreate?.size)
    }

    @Test
    fun `saveAll dengan deckId tidak valid pindah ke Error`() = runTest {
        aiRepo.flashcardsResult = listOf("A" to "1")
        val vm = newVm(id = 0L) // invalid
        vm.onMaterialChange(validMaterial)
        vm.generate()

        vm.saveAll()

        assertEquals(GeneratePhase.Error, vm.uiState.value.phase)
        assertNotNull(vm.uiState.value.errorMessage)
        assertEquals(null, cardRepo.lastBulkCreate)
    }

    @Test
    fun `retryFromInput mengembalikan ke phase Input dan clear error`() = runTest {
        aiRepo.throwOnGenerate = true
        val vm = newVm()
        vm.onMaterialChange(validMaterial)
        vm.generate()
        assertEquals(GeneratePhase.Error, vm.uiState.value.phase)

        vm.retryFromInput()

        assertEquals(GeneratePhase.Input, vm.uiState.value.phase)
        assertEquals(null, vm.uiState.value.errorMessage)
    }
}
