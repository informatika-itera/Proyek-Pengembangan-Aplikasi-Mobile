package com.example.pocketguard.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.example.pocketguard.data.local.datastore.UserPreferences
import com.example.pocketguard.domain.model.Transaction
import com.example.pocketguard.domain.model.TransactionCategory
import com.example.pocketguard.domain.model.TransactionType
import com.example.pocketguard.domain.repository.TransactionRepository
import com.example.pocketguard.domain.usecase.DeleteTransactionUseCase
import com.example.pocketguard.domain.usecase.GetAllTransactionsUseCase
import com.example.pocketguard.presentation.screens.home.HomeScreen
import com.example.pocketguard.presentation.screens.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

// 👇 MENCIPTAKAN SIMULATOR OS ANDROID (Mengobati FINGERPRINT = null)
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34]) // Mencegah bentrok dengan target SDK 36
class HomeScreenTest {

    // 👇 MENGGUNAKAN RULE STANDAR ANDROID JUNIT
    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // Helper perakit ViewModel
    private fun createViewModel(repository: TransactionRepository): HomeViewModel {
        return HomeViewModel(
            getAllTransactionsUseCase = GetAllTransactionsUseCase(repository),
            deleteTransactionUseCase = DeleteTransactionUseCase(repository),
            userPreferences = UserPreferences(FakeDataStoreForUI())
        )
    }

    // ==================== UI TEST 1: KONDISI BELUM ADA TRANSAKSI ====================
    @Test
    fun emptyState_noTransactions_shouldShowWelcomeMessage() {
        val viewModel = createViewModel(FakeUiTransactionRepository(emptyList()))

        // 🛠️ PERBEDAAN: Menggunakan composeTestRule.setContent
        composeTestRule.setContent {
            HomeScreen(onNavigateToAdd = { _, _ -> }, onNavigateToDetail = {}, viewModel = viewModel)
        }

        composeTestRule.onNodeWithText("Belum Ada Transaksi").assertIsDisplayed()
        composeTestRule.onNodeWithText("Mulai catat keuanganmu sekarang").assertIsDisplayed()
    }

    // ==================== UI TEST 2: HASIL PENCARIAN KOSONG ====================
    @Test
    fun emptyState_withSearchQuery_shouldShowNotFoundMessage() {
        val dummyData = Transaction(
            id = 1, amount = 25000.0, description = "Beli Kopi",
            category = TransactionCategory.OTHER, type = TransactionType.EXPENSE,
            createdAt = Clock.System.now().toEpochMilliseconds()
        )
        val viewModel = createViewModel(FakeUiTransactionRepository(listOf(dummyData)))

        composeTestRule.setContent {
            HomeScreen(onNavigateToAdd = { _, _ -> }, onNavigateToDetail = {}, viewModel = viewModel)
        }

        // Simulasikan pengetikan
        viewModel.onSearchQueryChange("Belanja Mewah")

        composeTestRule.onNodeWithText("Tidak Ditemukan").assertIsDisplayed()
    }

    // ==================== UI TEST 3: BERHASIL MEMUAT SALDO ====================
    @Test
    fun successState_shouldShowBalanceCardAndRespondToFabClick() {
        val dummyData = Transaction(
            id = 1, amount = 1250000.0, description = "Gaji Pokok",
            category = TransactionCategory.SALARY, type = TransactionType.INCOME,
            createdAt = Clock.System.now().toEpochMilliseconds()
        )
        val viewModel = createViewModel(FakeUiTransactionRepository(listOf(dummyData)))

        composeTestRule.setContent {
            HomeScreen(onNavigateToAdd = { _, _ -> }, onNavigateToDetail = {}, viewModel = viewModel)
        }

        composeTestRule.onNodeWithText("Total Saldo").assertIsDisplayed()
        composeTestRule.onNodeWithText("Rp 1.250.000").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Tambah Transaksi").performClick()
    }
}

/* =====================================================================
 * FAKE REPOSITORY (PENGGANTI MOCKK)
 * ===================================================================== */
class FakeUiTransactionRepository(initialList: List<Transaction>) : TransactionRepository {
    private val transactions = MutableStateFlow(initialList)
    override fun getAllTransactions(): Flow<List<Transaction>> = transactions
    override suspend fun insertTransaction(transaction: Transaction): Long = 0L
    override suspend fun deleteTransaction(id: Long) {}
}

class FakeDataStoreForUI : DataStore<Preferences> {
    private val _data = MutableStateFlow(emptyPreferences())
    override val data: Flow<Preferences> = _data
    override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
        val newPrefs = transform(_data.value)
        _data.value = newPrefs
        return newPrefs
    }
}