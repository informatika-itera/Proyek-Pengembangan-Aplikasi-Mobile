package com.kelazzz.app.testutil

import com.kelazzz.app.domain.model.AttendanceSummary
import com.kelazzz.app.domain.model.ChatMessage
import com.kelazzz.app.domain.model.Jadwal
import com.kelazzz.app.domain.model.Kelas
import com.kelazzz.app.domain.model.Presensi
import com.kelazzz.app.domain.model.User
import com.kelazzz.app.domain.repository.AIRepository
import com.kelazzz.app.domain.repository.AuthRepository
import com.kelazzz.app.domain.repository.JadwalRepository
import com.kelazzz.app.domain.repository.PresensiRepository
import com.kelazzz.app.domain.repository.PresensiSyncProgress
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class TestMainDispatcher(
    val dispatcher: TestDispatcher = StandardTestDispatcher()
) {
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    fun tearDown() {
        Dispatchers.resetMain()
    }
}

class FakeJadwalRepository : JadwalRepository {
    private val jadwalFlow = MutableStateFlow<List<Jadwal>>(emptyList())
    private var nextId = 1L

    fun seed(jadwal: List<Jadwal>) {
        jadwalFlow.value = jadwal.sortedWith(compareBy<Jadwal> { it.tanggal }.thenBy { it.waktu })
        nextId = (jadwal.maxOfOrNull { it.id } ?: 0L) + 1L
    }

    override fun getAllJadwal(): Flow<List<Jadwal>> = jadwalFlow

    override fun getJadwalByTanggal(tanggal: String): Flow<List<Jadwal>> =
        jadwalFlow.map { list -> list.filter { it.tanggal == tanggal } }

    override fun getUpcomingJadwal(fromDate: String, limit: Int): Flow<List<Jadwal>> =
        jadwalFlow.map { list ->
            list
                .filter { it.tanggal >= fromDate }
                .sortedWith(compareBy<Jadwal> { it.tanggal }.thenBy { it.waktu })
                .take(limit)
        }

    override fun getJadwalById(id: Long): Flow<Jadwal?> =
        jadwalFlow.map { list -> list.firstOrNull { it.id == id } }

    override suspend fun insertJadwal(jadwal: Jadwal): Long {
        val id = nextId++
        seed(jadwalFlow.value + jadwal.copy(id = id))
        return id
    }

    override suspend fun updateJadwal(jadwal: Jadwal) {
        seed(jadwalFlow.value.map { if (it.id == jadwal.id) jadwal else it })
    }

    override suspend fun deleteJadwal(id: Long) {
        seed(jadwalFlow.value.filterNot { it.id == id })
    }
}

class FakePresensiRepository : PresensiRepository {
    val kelasFlow = MutableStateFlow<List<Kelas>>(emptyList())
    val presensiFlow = MutableStateFlow<List<Presensi>>(emptyList())
    val summaryFlow = MutableStateFlow<List<AttendanceSummary>>(emptyList())
    val submittedTokens = mutableListOf<String>()
    var submitResult: Result<Unit> = Result.success(Unit)
    var syncResult: Result<Unit> = Result.success(Unit)
    var syncForKelasResult: Result<Unit> = Result.success(Unit)

    override fun getKelasList(): Flow<List<Kelas>> = kelasFlow

    override suspend fun syncKelas(): Result<Unit> = syncResult

    override fun getAllPresensi(): Flow<List<Presensi>> = presensiFlow

    override fun getPresensiByMataKuliah(mataKuliahId: String): Flow<List<Presensi>> =
        presensiFlow.map { list -> list.filter { it.mataKuliahId == mataKuliahId } }

    override fun getAttendanceSummary(): Flow<List<AttendanceSummary>> = summaryFlow

    override suspend fun submitPresensi(token: String): Result<Unit> {
        submittedTokens += token
        return submitResult
    }

    override suspend fun syncPresensiForKelas(kelasId: String, mataKuliahNama: String): Result<Unit> =
        syncForKelasResult

    override suspend fun syncPresensi(onProgress: (PresensiSyncProgress) -> Unit): Result<Unit> {
        onProgress(PresensiSyncProgress(completed = 1, total = 1, currentMataKuliah = "Basis Data"))
        return syncResult
    }
}

class FakeAuthRepository : AuthRepository {
    val currentUserFlow = MutableStateFlow<User?>(null)
    val loggedInFlow = MutableStateFlow(false)
    var loginResult: Result<User> = Result.failure(IllegalStateException("Login result belum diatur"))
    var lastUsername: String? = null
    var lastPassword: String? = null
    var logoutCalled = false

    override suspend fun login(username: String, password: String): Result<User> {
        lastUsername = username
        lastPassword = password
        return loginResult.onSuccess {
            currentUserFlow.value = it
            loggedInFlow.value = true
        }
    }

    override suspend fun logout() {
        logoutCalled = true
        currentUserFlow.value = null
        loggedInFlow.value = false
    }

    override val isLoggedIn: Flow<Boolean> = loggedInFlow
    override val currentUser: Flow<User?> = currentUserFlow
}

class FakeAIRepository : AIRepository {
    var chatResult: Result<String> = Result.success("Jawaban AI")
    var lastMessage: String? = null
    var lastHistory: List<ChatMessage> = emptyList()
    var clearCalled = false

    override suspend fun analyzeAttendance(attendanceData: String): Result<String> =
        Result.success("Analisis: $attendanceData")

    override suspend fun chat(message: String, history: List<ChatMessage>): Result<String> {
        lastMessage = message
        lastHistory = history
        return chatResult
    }

    override fun clearHistory() {
        clearCalled = true
    }
}
