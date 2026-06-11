package com.example.pantaujompo.presentation.screens.riwayat

import com.example.pantaujompo.data.local.datastore.UserPreferences
import com.example.pantaujompo.data.local.room.MakananDao
import com.example.pantaujompo.data.local.room.MakananEntity
import com.example.pantaujompo.data.local.room.RiwayatDao
import com.example.pantaujompo.data.local.room.RiwayatEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

// 🧪 RIWAYAT VIEW MODEL TEST
// Tes ini digunakan untuk menguji logika bisnis pada Riwayat Layar.
// Tujuan utamanya:
// 1. Memastikan filter waktu (Hari ini, Minggu ini, Bulan ini) berfungsi.
// 2. Menguji apakah data berhasil dihapus atau ditambah melalui Data Access Object (DAO).
@OptIn(ExperimentalCoroutinesApi::class)
class RiwayatViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var riwayatDao: RiwayatDao
    private lateinit var makananDao: MakananDao
    private lateinit var userPreferences: UserPreferences
    private lateinit var viewModel: RiwayatViewModel

    private val mockRiwayatFlow = MutableStateFlow<List<RiwayatEntity>>(emptyList())
    private val mockMakananFlow = MutableStateFlow<List<MakananEntity>>(emptyList())

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        riwayatDao = mockk(relaxed = true)
        makananDao = mockk(relaxed = true)
        userPreferences = mockk(relaxed = true)

        every { riwayatDao.getAllRiwayat() } returns mockRiwayatFlow
        every { makananDao.getAllMakanan() } returns mockMakananFlow
        every { userPreferences.targetKalori } returns MutableStateFlow(2000)
        every { userPreferences.language } returns MutableStateFlow("id")

        viewModel = RiwayatViewModel(riwayatDao, makananDao, userPreferences)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // 1. Menguji status awal filter waktu (harus "SEMUA")
    @Test
    fun `initial state of timeFilter should be SEMUA`() = runTest {
        assertEquals(TimeFilter.SEMUA, viewModel.timeFilter.value)
    }

    // 2. Menguji apakah filter waktu berhasil diubah (misal ke "HARIAN")
    @Test
    fun `setTimeFilter updates timeFilter state`() = runTest {
        viewModel.setTimeFilter(TimeFilter.HARIAN)
        assertEquals(TimeFilter.HARIAN, viewModel.timeFilter.value)
    }

    // 3. Menguji fungsi penyimpan target kalori ke penyimpanan lokal
    @Test
    fun `updateTargetKalori calls userPreferences setTargetKalori`() = runTest {
        viewModel.updateTargetKalori(2500)
        advanceUntilIdle()
        coVerify { userPreferences.setTargetKalori(2500) }
    }

    // 4. Menguji fungsi penghapusan aktivitas olahraga di database
    @Test
    fun `deleteActivity calls riwayatDao hapusRiwayatById`() = runTest {
        viewModel.deleteActivity(1)
        advanceUntilIdle()
        coVerify { riwayatDao.hapusRiwayatById(1) }
    }

    // 5. Menguji fungsi penambahan catatan makanan ke database
    @Test
    fun `insertMakanan calls makananDao insertMakanan`() = runTest {
        val makanan = MakananEntity(namaMakanan = "Nasi Goreng", protein = 10, karbo = 40, lemak = 15, info = "")
        viewModel.insertMakanan(makanan)
        advanceUntilIdle()
        coVerify { makananDao.insertMakanan(makanan) }
    }

    // 6. Menguji fungsi penghapusan catatan makanan di database
    @Test
    fun `deleteMakanan calls makananDao hapusMakananById`() = runTest {
        viewModel.deleteMakanan(2)
        advanceUntilIdle()
        coVerify { makananDao.hapusMakananById(2) }
    }
}
