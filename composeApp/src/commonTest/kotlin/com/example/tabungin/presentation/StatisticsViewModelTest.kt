package com.example.tabungin.presentation.screens.statistics

import com.example.tabungin.domain.model.Setoran
import com.example.tabungin.domain.model.Target
import com.example.tabungin.domain.repository.TargetRepository
import com.example.tabungin.domain.usecase.GetAllSetoranUseCase
import com.example.tabungin.domain.usecase.GetAllTargetsUseCase
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
class StatisticsViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repo: FakeStatisticsTargetRepository
    private lateinit var viewModel: StatisticsViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repo = FakeStatisticsTargetRepository()
        viewModel = StatisticsViewModel(
            getAllTargetsUseCase = GetAllTargetsUseCase(repo),
            getAllSetoranUseCase = GetAllSetoranUseCase(repo)
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `targets are loaded on init`() = runTest {
        repo.insertTarget(Target(nama = "Test Target", targetAmount = 1_000_000.0, deadline = "2025-12-31"))

        val vm = StatisticsViewModel(
            getAllTargetsUseCase = GetAllTargetsUseCase(repo),
            getAllSetoranUseCase = GetAllSetoranUseCase(repo)
        )
        advanceUntilIdle()

        assertEquals(1, vm.uiState.value.targets.size)
        assertEquals("Test Target", vm.uiState.value.targets.first().nama)
    }

    @Test
    fun `targetTercapai counts targets that meet goal`() = runTest {
        repo.insertTarget(Target(nama = "Tercapai", targetAmount = 1_000_000.0, deadline = "2025-12-31", terkumpul = 1_000_000.0))
        repo.insertTarget(Target(nama = "Belum", targetAmount = 2_000_000.0, deadline = "2025-12-31", terkumpul = 500_000.0))
        repo.insertTarget(Target(nama = "Juga Tercapai", targetAmount = 3_000_000.0, deadline = "2025-12-31", terkumpul = 3_000_000.0))

        val vm = StatisticsViewModel(
            getAllTargetsUseCase = GetAllTargetsUseCase(repo),
            getAllSetoranUseCase = GetAllSetoranUseCase(repo)
        )
        advanceUntilIdle()

        assertEquals(2, vm.uiState.value.targetTercapai)
        assertEquals(1, vm.uiState.value.targetAktif)
    }

    @Test
    fun `rataRataTabungan calculated correctly`() = runTest {
        repo.insertTarget(Target(nama = "A", targetAmount = 1_000_000.0, deadline = "2025-12-31", terkumpul = 100_000.0))
        repo.insertTarget(Target(nama = "B", targetAmount = 1_000_000.0, deadline = "2025-12-31", terkumpul = 200_000.0))

        val vm = StatisticsViewModel(
            getAllTargetsUseCase = GetAllTargetsUseCase(repo),
            getAllSetoranUseCase = GetAllSetoranUseCase(repo)
        )
        advanceUntilIdle()

        assertEquals(150_000.0, vm.uiState.value.rataRataTabungan)
    }

    @Test
    fun `rataRataTabungan is 0 when no targets`() = runTest {
        val vm = StatisticsViewModel(
            getAllTargetsUseCase = GetAllTargetsUseCase(repo),
            getAllSetoranUseCase = GetAllSetoranUseCase(repo)
        )
        advanceUntilIdle()

        assertEquals(0.0, vm.uiState.value.rataRataTabungan)
    }

    @Test
    fun `totalSetoran counts setoran correctly`() = runTest {
        repo.insertSetoran(Setoran(targetId = 1, amount = 100_000.0, tanggal = "2025-06-01"))
        repo.insertSetoran(Setoran(targetId = 1, amount = 200_000.0, tanggal = "2025-06-15"))

        val vm = StatisticsViewModel(
            getAllTargetsUseCase = GetAllTargetsUseCase(repo),
            getAllSetoranUseCase = GetAllSetoranUseCase(repo)
        )
        advanceUntilIdle()

        assertEquals(2, vm.uiState.value.totalSetoran)
    }

    @Test
    fun `targets are sorted by deadline`() = runTest {
        repo.insertTarget(Target(nama = "Later", targetAmount = 1_000_000.0, deadline = "2025-12-31"))
        repo.insertTarget(Target(nama = "Earlier", targetAmount = 1_000_000.0, deadline = "2025-01-01"))

        val vm = StatisticsViewModel(
            getAllTargetsUseCase = GetAllTargetsUseCase(repo),
            getAllSetoranUseCase = GetAllSetoranUseCase(repo)
        )
        advanceUntilIdle()

        val targets = vm.uiState.value.targets
        assertEquals("Earlier", targets[0].nama)
        assertEquals("Later", targets[1].nama)
    }


    @Test
    fun `isLoading becomes false after data loads`() = runTest {
        val vm = StatisticsViewModel(
            getAllTargetsUseCase = GetAllTargetsUseCase(repo),
            getAllSetoranUseCase = GetAllSetoranUseCase(repo)
        )
        advanceUntilIdle()

        assertFalse(vm.uiState.value.isLoading)
    }

    @Test
    fun `setoran list is loaded correctly`() = runTest {
        repo.insertSetoran(Setoran(targetId = 1, amount = 100_000.0, tanggal = "2025-06-01"))

        val vm = StatisticsViewModel(
            getAllTargetsUseCase = GetAllTargetsUseCase(repo),
            getAllSetoranUseCase = GetAllSetoranUseCase(repo)
        )
        advanceUntilIdle()

        assertEquals(1, vm.uiState.value.setoranList.size)
    }

    @Test
    fun `all zero values when no data`() = runTest {
        val vm = StatisticsViewModel(
            getAllTargetsUseCase = GetAllTargetsUseCase(repo),
            getAllSetoranUseCase = GetAllSetoranUseCase(repo)
        )
        advanceUntilIdle()

        val state = vm.uiState.value
        assertEquals(0.0, state.totalTabungan)
        assertEquals(0.0, state.totalTarget)
        assertEquals(0.0, state.rataRataTabungan)
        assertEquals(0, state.jumlahTarget)
        assertEquals(0, state.targetTercapai)
        assertEquals(0, state.targetAktif)
        assertEquals(0, state.totalSetoran)
    }
}

// Fake repository for Statistics tests
class FakeStatisticsTargetRepository : TargetRepository {
    private val targets = mutableListOf<Target>()
    private val setoranList = mutableListOf<Setoran>()
    private var nextTargetId = 1L
    private var nextSetoranId = 1L

    override fun getAllTargets(): Flow<List<Target>> = flowOf(targets.sortedBy { it.deadline })

    override fun getTargetById(id: Long): Flow<Target?> = flowOf(targets.find { it.id == id })

    override suspend fun insertTarget(target: Target): Long {
        val newTarget = target.copy(id = nextTargetId++)
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

    override fun getSetoranByTarget(targetId: Long) = flowOf(setoranList.filter { it.targetId == targetId })

    override fun getAllSetoran(): Flow<List<Setoran>> = flowOf(setoranList.toList())

    override suspend fun insertSetoran(setoran: Setoran) {
        val newSetoran = setoran.copy(id = nextSetoranId++)
        setoranList.add(newSetoran)
    }

    override suspend fun deleteSetoran(id: Long) {
        val toRemove = setoranList.filter { it.id == id }
        setoranList.removeAll(toRemove)
    }
}