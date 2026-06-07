package com.kelazzz.app.domain.usecase

import com.kelazzz.app.domain.model.JenisJadwal
import com.kelazzz.app.testutil.FakeJadwalRepository
import com.kelazzz.app.testutil.FakePresensiRepository
import com.kelazzz.app.testutil.sampleJadwal
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UseCasesTest {

    @Test
    fun submitPresensiRejectsBlankToken() = runTest {
        val repository = FakePresensiRepository()
        val useCase = SubmitPresensiUseCase(repository)

        val result = useCase("   ")

        assertFalse(result.isSuccess)
        assertEquals(0, repository.submittedTokens.size)
    }

    @Test
    fun submitPresensiDelegatesValidToken() = runTest {
        val repository = FakePresensiRepository()
        val useCase = SubmitPresensiUseCase(repository)

        val result = useCase("TOKEN-123")

        assertTrue(result.isSuccess)
        assertEquals(listOf("TOKEN-123"), repository.submittedTokens)
    }

    @Test
    fun saveJadwalRejectsBlankTitle() = runTest {
        val repository = FakeJadwalRepository()
        val useCase = SaveJadwalUseCase(repository)

        val result = useCase(sampleJadwal(judul = "   "))

        assertFalse(result.isSuccess)
        assertEquals(emptyList(), repository.getAllJadwal().first())
    }

    @Test
    fun saveJadwalInsertsNewItem() = runTest {
        val repository = FakeJadwalRepository()
        val useCase = SaveJadwalUseCase(repository)

        val result = useCase(sampleJadwal(id = 0L, judul = "Kuis Mobile", jenis = JenisJadwal.KUIS))

        assertTrue(result.isSuccess)
        val stored = repository.getAllJadwal().first().single()
        assertEquals(result.getOrThrow(), stored.id)
        assertEquals("Kuis Mobile", stored.judul)
        assertEquals(JenisJadwal.KUIS, stored.jenis)
    }

    @Test
    fun saveJadwalUpdatesExistingItem() = runTest {
        val repository = FakeJadwalRepository().apply {
            seed(listOf(sampleJadwal(id = 7L, judul = "Judul Lama")))
        }
        val useCase = SaveJadwalUseCase(repository)

        val result = useCase(sampleJadwal(id = 7L, judul = "Judul Baru"))

        assertTrue(result.isSuccess)
        assertEquals(7L, result.getOrThrow())
        assertEquals("Judul Baru", repository.getJadwalById(7L).first()?.judul)
    }

    @Test
    fun getJadwalUpcomingFiltersPastItemsAndAppliesLimit() = runTest {
        val repository = FakeJadwalRepository().apply {
            seed(
                listOf(
                    sampleJadwal(id = 1L, judul = "Lampau", tanggal = "2026-06-01"),
                    sampleJadwal(id = 2L, judul = "Hari Ini", tanggal = "2026-06-07"),
                    sampleJadwal(id = 3L, judul = "Besok", tanggal = "2026-06-08")
                )
            )
        }
        val useCase = GetJadwalUseCase(repository)

        val result = useCase.upcoming(fromDate = "2026-06-07", limit = 1).first()

        assertEquals(listOf("Hari Ini"), result.map { it.judul })
    }
}
