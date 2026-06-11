package com.example.mapenumkm.presentation.screens.transaction

import com.example.mapenumkm.domain.model.Note
import com.example.mapenumkm.domain.repository.NoteRepository
import com.example.mapenumkm.domain.repository.TransactionRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import kotlinx.datetime.Clock
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionViewModelTest {

    private val noteRepository: NoteRepository = mockk()
    private val transactionRepository: TransactionRepository = mockk()
    private lateinit var viewModel: TransactionViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    private val testProducts = listOf(
        Note(id = 1, title = "P1", content = "C1", price = 10.0, stock = 10, createdAt = Clock.System.now(), updatedAt = Clock.System.now()),
        Note(id = 2, title = "P2", content = "C2", price = 20.0, stock = 5, createdAt = Clock.System.now(), updatedAt = Clock.System.now())
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { noteRepository.getAllNotes() } returns flowOf(testProducts)
        viewModel = TransactionViewModel(noteRepository, transactionRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `updateQuantity should update cartItems correctly`() {
        viewModel.updateQuantity(1L, 1)
        assertEquals(1, viewModel.uiState.value.cartItems[1L])
        
        viewModel.updateQuantity(1L, 2)
        assertEquals(3, viewModel.uiState.value.cartItems[1L])
        
        viewModel.updateQuantity(1L, -1)
        assertEquals(2, viewModel.uiState.value.cartItems[1L])
        
        viewModel.updateQuantity(1L, -2)
        assertNull(viewModel.uiState.value.cartItems[1L])
    }

    @Test
    fun `updateQuantity should set error if stock is insufficient`() {
        viewModel.updateQuantity(2L, 6)
        assertNotNull(viewModel.uiState.value.error)
        assertTrue(viewModel.uiState.value.error!!.contains("Stok tidak mencukupi"))
    }

    @Test
    fun `saveTransaction should set error if cart is empty`() {
        viewModel.saveTransaction()
        assertEquals("Pilih minimal satu produk", viewModel.uiState.value.error)
    }

    @Test
    fun `saveTransaction should call repository and clear cart on success`() = runTest {
        coEvery { transactionRepository.insertTransaction(any()) } returns 1L
        coEvery { noteRepository.updateNote(any()) } returns Unit
        
        viewModel.updateQuantity(1L, 1)
        viewModel.onPaymentAmountChange("10.0")
        viewModel.saveTransaction()
        
        coVerify { transactionRepository.insertTransaction(any()) }
        assertTrue(viewModel.uiState.value.isSuccess)
    }
}
