package com.example.inventra.presentation

import app.cash.turbine.test
import com.example.inventra.FakeAuthRepository
import com.example.inventra.FakeBorrowRepository
import com.example.inventra.FakeItemRepository
import com.example.inventra.domain.model.BorrowRecord
import com.example.inventra.domain.model.BorrowStatus
import com.example.inventra.domain.model.User
import com.example.inventra.domain.model.UserRole
import com.example.inventra.domain.model.UserDivision
import com.example.inventra.presentation.screens.history.HistoryUiState
import com.example.inventra.presentation.screens.history.HistoryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var borrowRepository: FakeBorrowRepository
    private lateinit var itemRepository: FakeItemRepository
    private lateinit var authRepository: FakeAuthRepository
    private lateinit var viewModel: HistoryViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        borrowRepository = FakeBorrowRepository()
        itemRepository = FakeItemRepository()
        authRepository = FakeAuthRepository()
        
        // Default to admin for full visibility unless specified
        authRepository.loggedInUser = User(
            id = "admin", name = "Admin", email = "admin@hmif.itera.ac.id",
            role = UserRole.ADMIN, division = UserDivision.BENDAHARA_UMUM
        )
        
        viewModel = HistoryViewModel(
            borrowRepository = borrowRepository, 
            authRepository = authRepository, 
            itemRepository = itemRepository
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be Loading`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is HistoryUiState.Loading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `state should be Empty when no records exist`() = runTest {
        viewModel.uiState.test {
            skipItems(1) // Loading
            advanceUntilIdle()
            val state = awaitItem()
            assertTrue(state is HistoryUiState.Empty)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Admin should see all records`() = runTest {
        borrowRepository.addRecord(createTestRecord("User A"))
        borrowRepository.addRecord(createTestRecord("User B"))

        viewModel.uiState.test {
            skipItems(1)
            advanceUntilIdle()
            val state = awaitItem()
            assertTrue(state is HistoryUiState.Success)
            assertEquals(2, state.records.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Member should only see their own records`() = runTest {
        // Change user to Member "User A"
        authRepository.loggedInUser = User(
            id = "userA", name = "User A", email = "usera@hmif.itera.ac.id",
            role = UserRole.MEMBER, division = UserDivision.PUBDOK
        )
        
        borrowRepository.addRecord(createTestRecord("User A", "userA"))
        borrowRepository.addRecord(createTestRecord("User B", "userB"))

        viewModel.uiState.test {
            skipItems(1)
            advanceUntilIdle()
            val state = awaitItem()
            assertTrue(state is HistoryUiState.Success)
            assertEquals(1, state.records.size)
            assertEquals("User A", state.records.first().borrowerName)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createTestRecord(borrower: String, borrowerId: String = "anonymous"): BorrowRecord {
        return BorrowRecord(
            id = (1..10000).random().toLong(),
            itemId = 1,
            itemName = "Item",
            borrowerName = borrower,
            borrowerId = borrowerId,
            borrowDate = Clock.System.now(),
            dueDate = Clock.System.now(),
            status = BorrowStatus.PENDING
        )
    }
}
