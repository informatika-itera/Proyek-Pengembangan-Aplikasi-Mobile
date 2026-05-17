package com.example.tabungin.data.repository

import com.example.tabungin.domain.model.Target
import com.example.tabungin.domain.repository.TargetRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeTargetRepository : TargetRepository {
    private val targetsFlow = MutableStateFlow<List<Target>>(emptyList())
    private var nextId = 1L

    override fun getAllTargets(): Flow<List<Target>> = targetsFlow

    override fun getTargetById(id: Long): Flow<Target?> =
        targetsFlow.map { list -> list.find { it.id == id } }

    override suspend fun insertTarget(target: Target): Long {
        val t = target.copy(id = nextId++)
        targetsFlow.update { currentList -> currentList + t }
        return t.id
    }

    override suspend fun updateTarget(target: Target) {
        targetsFlow.update { currentList ->
            currentList.map { if (it.id == target.id) target else it }
        }
    }

    override suspend fun deleteTarget(id: Long) {
        targetsFlow.update { currentList ->
            currentList.filterNot { it.id == id }
        }
    }

    override fun getSetoranByTarget(targetId: Long) = flowOf(emptyList<com.example.tabungin.domain.model.Setoran>())
    override fun getAllSetoran() = flowOf(emptyList<com.example.tabungin.domain.model.Setoran>())
    override suspend fun insertSetoran(setoran: com.example.tabungin.domain.model.Setoran) {}
    override suspend fun deleteSetoran(id: Long) {}
}

class TargetRepositoryTest {
    private lateinit var repo: FakeTargetRepository

    @BeforeTest
    fun setUp() { repo = FakeTargetRepository() }

    @Test
    fun `insertTarget menambah target ke list`() = runTest {
        val target = Target(nama = "Beli Laptop", targetAmount = 10_000_000.0, deadline = "2025-12-31")
        repo.insertTarget(target)

        val all = repo.getAllTargets().first()
        assertEquals(1, all.size)
        assertEquals("Beli Laptop", all.first().nama)
    }

    @Test
    fun `deleteTarget menghapus target dari list`() = runTest {
        val id = repo.insertTarget(
            Target(nama = "Liburan", targetAmount = 5_000_000.0, deadline = "2025-06-01")
        )
        repo.deleteTarget(id)

        val all = repo.getAllTargets().first()
        assertTrue(all.isEmpty())
    }

    @Test
    fun `updateTarget mengubah data target`() = runTest {
        val id = repo.insertTarget(
            Target(nama = "Old Name", targetAmount = 1_000_000.0, deadline = "2025-01-01")
        )
        repo.updateTarget(Target(id = id, nama = "New Name", targetAmount = 2_000_000.0, deadline = "2025-06-30"))

        val updated = repo.getTargetById(id).first()
        assertEquals("New Name", updated?.nama)
        assertEquals(2_000_000.0, updated?.targetAmount)
    }

    @Test
    fun `getTargetById mengembalikan null bila tidak ada`() = runTest {
        val result = repo.getTargetById(999L).first()
        assertNull(result)
    }

    @Test
    fun `getAllTargets mengembalikan list kosong di awal`() = runTest {
        val all = repo.getAllTargets().first()
        assertTrue(all.isEmpty())
    }
}

