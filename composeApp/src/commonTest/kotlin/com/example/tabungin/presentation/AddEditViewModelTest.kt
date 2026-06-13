package com.example.tabungin.presentation.screens.add_edit

import com.example.tabungin.domain.model.Setoran
import com.example.tabungin.domain.model.Target
import com.example.tabungin.domain.usecase.GetTargetByIdUseCase
import com.example.tabungin.domain.usecase.InsertTargetUseCase
import com.example.tabungin.domain.usecase.UpdateTargetUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repo: FakeTestTargetRepository
    private lateinit var viewModel: AddEditViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repo = FakeTestTargetRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        viewModel = AddEditViewModel(
            targetId = null,
            getTargetByIdUseCase = GetTargetByIdUseCase(repo),
            insertTargetUseCase = InsertTargetUseCase(repo),
            updateTargetUseCase = UpdateTargetUseCase(repo)
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("", state.nama)
        assertEquals("", state.targetAmount)
        assertEquals("", state.deadline)
        assertEquals("🎯", state.icon)
        assertEquals("#4CAF50", state.warna)
        assertFalse(state.isLoading)
        assertFalse(state.isSaved)
        assertFalse(state.isEditMode)
    }

    @Test
    fun `onNamaChange updates state`() = runTest {
        viewModel = AddEditViewModel(
            targetId = null,
            getTargetByIdUseCase = GetTargetByIdUseCase(repo),
            insertTargetUseCase = InsertTargetUseCase(repo),
            updateTargetUseCase = UpdateTargetUseCase(repo)
        )

        viewModel.onNamaChange("Tabungan Baru")
        advanceUntilIdle()

        assertEquals("Tabungan Baru", viewModel.uiState.value.nama)
    }

    @Test
    fun `onTargetAmountChange updates state`() = runTest {
        viewModel = AddEditViewModel(
            targetId = null,
            getTargetByIdUseCase = GetTargetByIdUseCase(repo),
            insertTargetUseCase = InsertTargetUseCase(repo),
            updateTargetUseCase = UpdateTargetUseCase(repo)
        )

        viewModel.onTargetAmountChange("1000000")
        advanceUntilIdle()

        assertEquals("1000000", viewModel.uiState.value.targetAmount)
    }

    @Test
    fun `onDeadlineChange updates state`() = runTest {
        viewModel = AddEditViewModel(
            targetId = null,
            getTargetByIdUseCase = GetTargetByIdUseCase(repo),
            insertTargetUseCase = InsertTargetUseCase(repo),
            updateTargetUseCase = UpdateTargetUseCase(repo)
        )

        viewModel.onDeadlineChange("2025-12-31")
        advanceUntilIdle()

        assertEquals("2025-12-31", viewModel.uiState.value.deadline)
    }

    @Test
    fun `onIconChange updates state`() = runTest {
        viewModel = AddEditViewModel(
            targetId = null,
            getTargetByIdUseCase = GetTargetByIdUseCase(repo),
            insertTargetUseCase = InsertTargetUseCase(repo),
            updateTargetUseCase = UpdateTargetUseCase(repo)
        )

        viewModel.onIconChange("💰")
        advanceUntilIdle()

        assertEquals("💰", viewModel.uiState.value.icon)
    }

    @Test
    fun `onWarnaChange updates state`() = runTest {
        viewModel = AddEditViewModel(
            targetId = null,
            getTargetByIdUseCase = GetTargetByIdUseCase(repo),
            insertTargetUseCase = InsertTargetUseCase(repo),
            updateTargetUseCase = UpdateTargetUseCase(repo)
        )

        viewModel.onWarnaChange("#FF5722")
        advanceUntilIdle()

        assertEquals("#FF5722", viewModel.uiState.value.warna)
    }

    @Test
    fun `isFormValid returns false when form is empty`() = runTest {
        viewModel = AddEditViewModel(
            targetId = null,
            getTargetByIdUseCase = GetTargetByIdUseCase(repo),
            insertTargetUseCase = InsertTargetUseCase(repo),
            updateTargetUseCase = UpdateTargetUseCase(repo)
        )

        assertFalse(viewModel.isFormValid)
    }

    @Test
    fun `isFormValid returns false when nama is empty`() = runTest {
        viewModel = AddEditViewModel(
            targetId = null,
            getTargetByIdUseCase = GetTargetByIdUseCase(repo),
            insertTargetUseCase = InsertTargetUseCase(repo),
            updateTargetUseCase = UpdateTargetUseCase(repo)
        )

        viewModel.onTargetAmountChange("1000000")
        viewModel.onDeadlineChange("2025-12-31")

        assertFalse(viewModel.isFormValid)
    }

    @Test
    fun `isFormValid returns false when targetAmount is invalid`() = runTest {
        viewModel = AddEditViewModel(
            targetId = null,
            getTargetByIdUseCase = GetTargetByIdUseCase(repo),
            insertTargetUseCase = InsertTargetUseCase(repo),
            updateTargetUseCase = UpdateTargetUseCase(repo)
        )

        viewModel.onNamaChange("Test")
        viewModel.onTargetAmountChange("invalid")
        viewModel.onDeadlineChange("2025-12-31")

        assertFalse(viewModel.isFormValid)
    }

    @Test
    fun `isFormValid returns false when targetAmount is zero`() = runTest {
        viewModel = AddEditViewModel(
            targetId = null,
            getTargetByIdUseCase = GetTargetByIdUseCase(repo),
            insertTargetUseCase = InsertTargetUseCase(repo),
            updateTargetUseCase = UpdateTargetUseCase(repo)
        )

        viewModel.onNamaChange("Test")
        viewModel.onTargetAmountChange("0")
        viewModel.onDeadlineChange("2025-12-31")

        assertFalse(viewModel.isFormValid)
    }

    @Test
    fun `isFormValid returns true when all fields are valid`() = runTest {
        viewModel = AddEditViewModel(
            targetId = null,
            getTargetByIdUseCase = GetTargetByIdUseCase(repo),
            insertTargetUseCase = InsertTargetUseCase(repo),
            updateTargetUseCase = UpdateTargetUseCase(repo)
        )

        viewModel.onNamaChange("Test")
        viewModel.onTargetAmountChange("1000000")
        viewModel.onDeadlineChange("2025-12-31")

        assertTrue(viewModel.isFormValid)
    }

    @Test
    fun `saveTarget sets isSaved when form is valid`() = runTest {
        viewModel = AddEditViewModel(
            targetId = null,
            getTargetByIdUseCase = GetTargetByIdUseCase(repo),
            insertTargetUseCase = InsertTargetUseCase(repo),
            updateTargetUseCase = UpdateTargetUseCase(repo)
        )

        viewModel.onNamaChange("Tabungan Baru")
        viewModel.onTargetAmountChange("1000000")
        viewModel.onDeadlineChange("2025-12-31")

        viewModel.saveTarget()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isSaved)
        assertFalse(state.isLoading)
    }

    @Test
    fun `saveTarget does nothing when form is invalid`() = runTest {
        viewModel = AddEditViewModel(
            targetId = null,
            getTargetByIdUseCase = GetTargetByIdUseCase(repo),
            insertTargetUseCase = InsertTargetUseCase(repo),
            updateTargetUseCase = UpdateTargetUseCase(repo)
        )

        // Only fill nama, missing other fields
        viewModel.onNamaChange("Test")

        viewModel.saveTarget()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isSaved)
        assertFalse(state.isLoading)
    }

    @Test
    fun `clearError sets error to null`() = runTest {
        viewModel = AddEditViewModel(
            targetId = null,
            getTargetByIdUseCase = GetTargetByIdUseCase(repo),
            insertTargetUseCase = InsertTargetUseCase(repo),
            updateTargetUseCase = UpdateTargetUseCase(repo)
        )

        viewModel.clearError()
        advanceUntilIdle()

        assertEquals(null, viewModel.uiState.value.error)
    }

    @Test
    fun `loadTarget populates form in edit mode`() = runTest {
        // Insert a target first
        val targetId = repo.insertTarget(
            Target(nama = "Edit Test", targetAmount = 5_000_000.0, deadline = "2025-12-31")
        )

        viewModel = AddEditViewModel(
            targetId = targetId,
            getTargetByIdUseCase = GetTargetByIdUseCase(repo),
            insertTargetUseCase = InsertTargetUseCase(repo),
            updateTargetUseCase = UpdateTargetUseCase(repo)
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Edit Test", state.nama)
        assertEquals("5000000", state.targetAmount)
        assertEquals("2025-12-31", state.deadline)
        assertTrue(state.isEditMode)
    }

    @Test
    fun `updateTarget updates existing target`() = runTest {
        val targetId = repo.insertTarget(
            Target(nama = "Original", targetAmount = 1_000_000.0, deadline = "2025-06-01")
        )

        viewModel = AddEditViewModel(
            targetId = targetId,
            getTargetByIdUseCase = GetTargetByIdUseCase(repo),
            insertTargetUseCase = InsertTargetUseCase(repo),
            updateTargetUseCase = UpdateTargetUseCase(repo)
        )
        advanceUntilIdle()

        viewModel.onNamaChange("Updated Name")
        viewModel.saveTarget()
        advanceUntilIdle()

        val updated = repo.getTargetById(targetId)
        // Verify update was called (isSaved should be true)
        assertTrue(viewModel.uiState.value.isSaved)
    }
}

// Fake repository for testing
class FakeTestTargetRepository : com.example.tabungin.domain.repository.TargetRepository {
    private val targets = mutableListOf<Target>()
    private var nextId = 1L

    override fun getAllTargets(): Flow<List<Target>> = flowOf(targets.toList())

    override fun getTargetById(id: Long): Flow<Target?> = flowOf(targets.find { it.id == id })

    override suspend fun insertTarget(target: Target): Long {
        val newTarget = target.copy(id = nextId++)
        targets.add(newTarget)
        return newTarget.id
    }

    override suspend fun updateTarget(target: Target) {
        val index = targets.indexOfFirst { it.id == target.id }
        if (index >= 0) {
            targets[index] = target
        }
    }

    override suspend fun deleteTarget(id: Long) {
        val toRemove = targets.filter { it.id == id }
        targets.removeAll(toRemove)
    }

    override fun getSetoranByTarget(targetId: Long): Flow<List<Setoran>> = flowOf(emptyList())

    override fun getAllSetoran(): Flow<List<Setoran>> = flowOf(emptyList())

    override suspend fun insertSetoran(setoran: Setoran) {}

    override suspend fun deleteSetoran(id: Long) {}
}