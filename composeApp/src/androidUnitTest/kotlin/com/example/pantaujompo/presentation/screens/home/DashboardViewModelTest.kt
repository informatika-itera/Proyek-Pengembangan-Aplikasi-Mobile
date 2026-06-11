package com.example.pantaujompo.presentation.screens.home

import com.example.pantaujompo.data.local.datastore.UserPreferences
import com.example.pantaujompo.data.local.room.MakananDao
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

// 🧪 DASHBOARD VIEW MODEL TEST
// Tes ini digunakan untuk menguji logika bisnis pada layar utama (Dashboard).
// Tujuan utamanya:
// 1. Memastikan konversi waktu dan jarak ke Pace (menit/km) dilakukan dengan akurat.
// 2. Memastikan data pengguna (seperti nama) dimuat dengan benar dari preferensi.
@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var dao: RiwayatDao
    private lateinit var makananDao: MakananDao
    private lateinit var userPreferences: UserPreferences
    private lateinit var viewModel: DashboardViewModel

    private val mockRiwayatFlow = MutableStateFlow<List<RiwayatEntity>>(emptyList())
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        dao = mockk(relaxed = true)
        makananDao = mockk(relaxed = true)
        userPreferences = mockk(relaxed = true)

        every { dao.getAllRiwayat() } returns mockRiwayatFlow
        every { makananDao.getAllMakanan() } returns MutableStateFlow(emptyList())
        every { userPreferences.userName } returns MutableStateFlow("John")
        every { userPreferences.profileImageUri } returns MutableStateFlow("")
        every { userPreferences.userAge } returns MutableStateFlow(25)

        viewModel = DashboardViewModel(dao, makananDao, userPreferences)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // 1. Menguji apakah nama pengguna (username) berhasil ditarik
    @Test
    fun `userName emits from userPreferences`() = runTest {
        advanceUntilIdle()
        assertEquals("John", viewModel.userName.value)
    }

    // 2. Menguji perhitungan rata-rata Pace (Misal 5km dalam 30m = 6'00")
    @Test
    fun `getRataRataPace correctly calculates pace`() = runTest {
        val paceStr = viewModel.getRataRataPace(5.0, 30) // 30 mins / 5 km = 6'00"
        assertEquals("6'00\"", paceStr)
    }

    // 3. Menguji Pace jika jarak sangat kecil (menghindari error matematika)
    @Test
    fun `getRataRataPace returns 0'00 when jarak is very small`() = runTest {
        val paceStr = viewModel.getRataRataPace(0.005, 30)
        assertEquals("0'00\"", paceStr)
    }

    // 4. Menguji fungsi simpan aktivitas lari ke database
    @Test
    fun `simpanAktivitas calls dao insertRiwayat`() = runTest {
        viewModel.simpanAktivitas("Lari Pagi", "Lari di taman", "Lari", 5.0, 300, 30, "6'00\"", "rute", null)
        advanceUntilIdle()
        coVerify { dao.insertRiwayat(any()) }
    }

    // 5. Menguji fungsi hapus aktivitas lari dari database
    @Test
    fun `hapusAktivitas calls dao hapusRiwayatById`() = runTest {
        viewModel.hapusAktivitas(10)
        advanceUntilIdle()
        coVerify { dao.hapusRiwayatById(10) }
    }
}
