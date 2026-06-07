package com.kelazzz.app.presentation

import androidx.lifecycle.SavedStateHandle
import com.kelazzz.app.domain.model.ChatRole
import com.kelazzz.app.domain.model.JenisJadwal
import com.kelazzz.app.domain.model.ReminderOption
import com.kelazzz.app.presentation.screens.ai.AIViewModel
import com.kelazzz.app.presentation.screens.jadwal.addedit.JadwalAddEditViewModel
import com.kelazzz.app.presentation.screens.jadwal.JadwalListViewModel
import com.kelazzz.app.presentation.screens.home.HomeViewModel
import com.kelazzz.app.presentation.screens.login.LoginViewModel
import com.kelazzz.app.presentation.screens.presensi.PresensiUiState
import com.kelazzz.app.presentation.screens.presensi.PresensiViewModel
import com.kelazzz.app.testutil.FakeAIRepository
import com.kelazzz.app.testutil.FakeAuthRepository
import com.kelazzz.app.testutil.FakeJadwalRepository
import com.kelazzz.app.testutil.FakePresensiRepository
import com.kelazzz.app.testutil.TestMainDispatcher
import com.kelazzz.app.testutil.sampleJadwal
import com.kelazzz.app.testutil.sampleSummary
import com.kelazzz.app.testutil.sampleUser
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelTest {
    private val mainDispatcher = TestMainDispatcher()

    @BeforeTest
    fun setUp() {
        mainDispatcher.setUp()
    }

    @AfterTest
    fun tearDown() {
        mainDispatcher.tearDown()
    }

    @Test
    fun loginViewModelShowsValidationErrorsForEmptyForm() = runTest(mainDispatcher.dispatcher) {
        val viewModel = LoginViewModel(FakeAuthRepository())

        viewModel.login()

        val state = viewModel.uiState.value
        assertEquals("Email ITERA tidak boleh kosong", state.usernameError)
        assertEquals("Password tidak boleh kosong", state.passwordError)
        assertFalse(state.isLoading)
    }

    @Test
    fun loginViewModelAppendsStudentEmailForNimLogin() = runTest(mainDispatcher.dispatcher) {
        val repository = FakeAuthRepository().apply {
            loginResult = Result.success(sampleUser(nama = "Bintang", nim = "123140098"))
        }
        val viewModel = LoginViewModel(repository)

        viewModel.onUsernameChange("123140098")
        viewModel.onPasswordChange("password")
        viewModel.login()
        advanceUntilIdle()
        advanceTimeBy(1_500L)

        assertEquals("123140098@student.itera.ac.id", repository.lastUsername)
        assertEquals("password", repository.lastPassword)
        assertEquals("Halo, Bintang!", viewModel.uiState.value.loginSuccessMessage)
    }

    @Test
    fun loginViewModelRejectsNonStudentIteraEmailWithoutRepositoryCall() = runTest(mainDispatcher.dispatcher) {
        val repository = FakeAuthRepository()
        val viewModel = LoginViewModel(repository)

        viewModel.onUsernameChange("dosen@itera.ac.id")
        viewModel.onPasswordChange("password")
        viewModel.login()

        val state = viewModel.uiState.value
        assertEquals("Gunakan email mahasiswa ITERA (@student.itera.ac.id)", state.usernameError)
        assertFalse(state.isLoading)
        assertNull(repository.lastUsername)
        assertNull(repository.lastPassword)
    }

    @Test
    fun loginViewModelNormalizesUppercaseStudentEmailBeforeLogin() = runTest(mainDispatcher.dispatcher) {
        val repository = FakeAuthRepository().apply {
            loginResult = Result.success(sampleUser(nama = "Rifael", nim = "123140077"))
        }
        val viewModel = LoginViewModel(repository)

        viewModel.onUsernameChange(" Nama.123@STUDENT.ITERA.AC.ID ")
        viewModel.onPasswordChange("password")
        viewModel.login()
        advanceUntilIdle()

        assertEquals("nama.123@student.itera.ac.id", repository.lastUsername)
        assertEquals("Halo, Rifael!", viewModel.uiState.value.loginSuccessMessage)
    }

    @Test
    fun presensiViewModelRejectsBlankTokenWithoutRepositoryCall() = runTest(mainDispatcher.dispatcher) {
        val repository = FakePresensiRepository()
        val viewModel = PresensiViewModel(repository)

        viewModel.submitPresensi(" ")

        val state = assertIs<PresensiUiState.Error>(viewModel.uiState.value)
        assertEquals("Token presensi tidak boleh kosong.", state.message)
        assertEquals(emptyList(), repository.submittedTokens)
    }

    @Test
    fun presensiViewModelShowsSuccessAfterRepositorySubmit() = runTest(mainDispatcher.dispatcher) {
        val repository = FakePresensiRepository()
        val viewModel = PresensiViewModel(repository)

        viewModel.submitPresensi(" TOKEN-OK ")
        advanceUntilIdle()

        assertIs<PresensiUiState.Success>(viewModel.uiState.value)
        assertEquals(listOf("TOKEN-OK"), repository.submittedTokens)
    }

    @Test
    fun aiViewModelAddsUserAndAssistantMessagesOnSuccess() = runTest(mainDispatcher.dispatcher) {
        val repository = FakeAIRepository().apply {
            chatResult = Result.success("Kehadiran Anda aman.")
        }
        val viewModel = AIViewModel(repository)

        viewModel.sendMessage("Rekap kehadiran saya")
        advanceUntilIdle()

        val messages = viewModel.uiState.value.messages
        assertEquals(2, messages.size)
        assertEquals(ChatRole.USER, messages[0].role)
        assertEquals("Rekap kehadiran saya", messages[0].content)
        assertEquals(ChatRole.ASSISTANT, messages[1].role)
        assertEquals("Kehadiran Anda aman.", messages[1].content)
        assertEquals("Rekap kehadiran saya", repository.lastMessage)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun aiViewModelShowsFriendlyErrorMessageOnFailure() = runTest(mainDispatcher.dispatcher) {
        val repository = FakeAIRepository().apply {
            chatResult = Result.failure(IllegalStateException("API key kosong"))
        }
        val viewModel = AIViewModel(repository)

        viewModel.sendMessage("Halo")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("API key kosong", state.error)
        assertTrue(state.messages.last().content.contains("Maaf, terjadi kesalahan"))
        assertFalse(state.isLoading)
    }

    @Test
    fun aiViewModelClearChatClearsMessagesAndRepositoryHistory() = runTest(mainDispatcher.dispatcher) {
        val repository = FakeAIRepository()
        val viewModel = AIViewModel(repository)
        viewModel.sendMessage("Halo")
        advanceUntilIdle()

        viewModel.clearChat()

        assertEquals(emptyList(), viewModel.uiState.value.messages)
        assertTrue(repository.clearCalled)
    }

    @Test
    fun jadwalListViewModelFiltersBySearchAndJenis() = runTest(mainDispatcher.dispatcher) {
        val repository = FakeJadwalRepository().apply {
            seed(
                listOf(
                    sampleJadwal(id = 1L, judul = "Tugas Mobile", jenis = JenisJadwal.TUGAS),
                    sampleJadwal(id = 2L, judul = "Kuis AI", jenis = JenisJadwal.KUIS),
                    sampleJadwal(id = 3L, judul = "Presentasi Basis Data", jenis = JenisJadwal.PRESENTASI)
                )
            )
        }
        val viewModel = JadwalListViewModel(repository)
        advanceUntilIdle()

        viewModel.onSearchQueryChange("mobile")
        viewModel.onJenisFilterChange(JenisJadwal.TUGAS)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals(listOf("Tugas Mobile"), state.jadwalList.map { it.judul })
    }

    @Test
    fun jadwalListViewModelShowsEmptyResultForUnmatchedFilter() = runTest(mainDispatcher.dispatcher) {
        val repository = FakeJadwalRepository().apply {
            seed(listOf(sampleJadwal(id = 1L, judul = "Tugas Mobile", jenis = JenisJadwal.TUGAS)))
        }
        val viewModel = JadwalListViewModel(repository)
        advanceUntilIdle()

        viewModel.onSearchQueryChange("tidak ada")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state)
        assertEquals(emptyList(), state.jadwalList)
    }

    @Test
    fun jadwalAddEditViewModelRejectsInvalidReminderDate() = runTest(mainDispatcher.dispatcher) {
        val repository = FakeJadwalRepository()
        val viewModel = JadwalAddEditViewModel(repository, SavedStateHandle())

        viewModel.onJudulChange("Kuis")
        viewModel.onTanggalChange("2026-02-31")
        viewModel.onWaktuChange("08:00-09:40")
        viewModel.onReminderOptionChange(ReminderOption.TEN_MINUTES)
        viewModel.saveJadwal(onSuccess = {})
        advanceUntilIdle()

        assertEquals(
            "Pilih tanggal dan waktu mulai agar notifikasi bisa dijadwalkan.",
            viewModel.uiState.value.formError
        )
        assertEquals(emptyList(), repository.getAllJadwal().first())
    }

    @Test
    fun jadwalAddEditViewModelSavesValidAgenda() = runTest(mainDispatcher.dispatcher) {
        val repository = FakeJadwalRepository()
        var successCalled = false
        val viewModel = JadwalAddEditViewModel(repository, SavedStateHandle())

        viewModel.onJudulChange("Ujian PAM")
        viewModel.onTanggalChange("2026-06-20")
        viewModel.onWaktuChange("10:00-12:00")
        viewModel.onJenisChange(JenisJadwal.UJIAN)
        viewModel.saveJadwal(onSuccess = { successCalled = true })
        advanceUntilIdle()

        val stored = repository.getAllJadwal().first().single()
        assertEquals("Ujian PAM", stored.judul)
        assertEquals(JenisJadwal.UJIAN, stored.jenis)
        assertTrue(successCalled)
    }

    @Test
    fun homeViewModelShowsOnlyUpcomingAgenda() = runTest(mainDispatcher.dispatcher) {
        val jadwalRepository = FakeJadwalRepository().apply {
            seed(
                listOf(
                    sampleJadwal(id = 1L, judul = "Agenda Lama", tanggal = "2000-01-01"),
                    sampleJadwal(id = 2L, judul = "Agenda Depan", tanggal = "2100-01-01")
                )
            )
        }
        val viewModel = HomeViewModel(FakeAuthRepository(), jadwalRepository, FakePresensiRepository())

        val upcoming = viewModel.upcomingJadwal.first { it.isNotEmpty() }

        assertEquals(listOf("Agenda Depan"), upcoming.map { it.judul })
    }

    @Test
    fun homeViewModelShowsTopAttendanceWarnings() = runTest(mainDispatcher.dispatcher) {
        val presensiRepository = FakePresensiRepository().apply {
            summaryFlow.value = listOf(
                sampleSummary(mataKuliahNama = "Aman", totalAlpha = 1),
                sampleSummary(mataKuliahNama = "Warning", totalAlpha = 2),
                sampleSummary(mataKuliahNama = "Bahaya", totalAlpha = 5)
            )
        }
        val viewModel = HomeViewModel(FakeAuthRepository(), FakeJadwalRepository(), presensiRepository)

        val warnings = viewModel.attendanceWarnings.first { it.isNotEmpty() }

        assertEquals(listOf("Bahaya", "Warning"), warnings.map { it.mataKuliahNama })
    }
}
