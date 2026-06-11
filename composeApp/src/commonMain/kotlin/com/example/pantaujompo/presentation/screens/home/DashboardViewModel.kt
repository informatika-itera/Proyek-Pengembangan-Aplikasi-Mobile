package com.example.pantaujompo.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pantaujompo.data.local.datastore.UserPreferences
import com.example.pantaujompo.data.local.room.RiwayatDao
import com.example.pantaujompo.data.local.room.RiwayatEntity
import com.example.pantaujompo.data.local.room.MakananDao
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone
import java.util.Locale
import com.example.pantaujompo.data.remote.api.GeminiService

class DashboardViewModel(
    private val dao: RiwayatDao,
    private val makananDao: MakananDao, // Ditambahkan MakananDao
    private val userPreferences: UserPreferences
) : ViewModel() {

    // Read userName directly from DataStore (real-time reactive)
    val userName: StateFlow<String> = userPreferences.userName
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    // Profile image URI persisted in DataStore
    val profileImageUri: StateFlow<String> = userPreferences.profileImageUri
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val riwayatList = dao.getAllRiwayat().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val weekStart = Calendar.getInstance(Locale("id", "ID")).apply {
        firstDayOfWeek = Calendar.MONDAY
        set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }.timeInMillis

    val todayStart = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }.timeInMillis

    val weeklyJarak = riwayatList.map { list -> list.filter { it.tanggal >= weekStart }.sumOf { it.jarak } }.stateIn(viewModelScope, SharingStarted.Lazily, 0.0)
    val weeklyKalori = riwayatList.map { list -> list.filter { it.tanggal >= weekStart }.sumOf { it.kalori } }.stateIn(viewModelScope, SharingStarted.Lazily, 0)
    val weeklyDurasi = riwayatList.map { list -> list.filter { it.tanggal >= weekStart }.sumOf { it.durasi } }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    val dailyJarak = riwayatList.map { list -> list.filter { it.tanggal >= todayStart }.sumOf { it.jarak } }.stateIn(viewModelScope, SharingStarted.Lazily, 0.0)
    val dailyKalori = riwayatList.map { list -> list.filter { it.tanggal >= todayStart }.sumOf { it.kalori } }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    // Data Nutrisi dari MakananDao
    val makananList = makananDao.getAllMakanan().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val dailyProtein = makananList.map { list -> list.filter { it.tanggal >= todayStart }.sumOf { it.protein } }.stateIn(viewModelScope, SharingStarted.Lazily, 0)
    val dailyKarbo = makananList.map { list -> list.filter { it.tanggal >= todayStart }.sumOf { it.karbo } }.stateIn(viewModelScope, SharingStarted.Lazily, 0)
    val dailyLemak = makananList.map { list -> list.filter { it.tanggal >= todayStart }.sumOf { it.lemak } }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    private val geminiService = GeminiService()
    private val _aiInsight = MutableStateFlow("Sedang menganalisis data harian Anda...")
    val aiInsight: StateFlow<String> = _aiInsight.asStateFlow()

    fun fetchDailyInsight() {
        viewModelScope.launch {
            _aiInsight.value = "Menghubungkan ke Gemini AI..."
            
            val calendar = Calendar.getInstance(TimeZone.getDefault())
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            val todayStart = calendar.timeInMillis
            
            // Get today's stats
            val currentRiwayat = riwayatList.first().filter { it.tanggal >= todayStart }
            val dailyJarak = currentRiwayat.sumOf { it.jarak }
            val dailyKalori = currentRiwayat.sumOf { it.kalori }
            val dailyDurasi = currentRiwayat.sumOf { it.durasi }
            
            val usia = userPreferences.userAge.first()
            val nama = userPreferences.userName.first()
            val profil = "Nama $nama, Lansia umur $usia tahun."
            
            val insight = geminiService.dapatkanInsightHarian(
                totalJarakKm = dailyJarak,
                totalKalori = dailyKalori,
                durasiMenit = dailyDurasi,
                profil = profil
            )
            _aiInsight.value = insight
        }
    }

    fun simpanAktivitas(judul: String, deskripsi: String, jenis: String, jarak: Double, kalori: Int, durasi: Int, pace: String, ruteString: String, photoUri: String?) {
        viewModelScope.launch {
            dao.insertRiwayat(
                RiwayatEntity(
                    judul = judul,
                    deskripsi = deskripsi,
                    jenis = jenis,
                    jarak = jarak,
                    kalori = kalori,
                    durasi = durasi,
                    pace = pace,
                    ruteString = ruteString,
                    photoUri = photoUri
                )
            )
        }
    }

    fun getRataRataPace(jarakKm: Double, durasiMenit: Int): String {
        if (jarakKm <= 0.01) return "0'00\""
        val paceDouble = durasiMenit / jarakKm
        val paceMin = paceDouble.toInt()
        val paceSec = ((paceDouble - paceMin) * 60).toInt()
        return String.format(Locale.US, "%d'%02d\"", paceMin, paceSec)
    }

    fun hapusAktivitas(id: Int) {
        viewModelScope.launch {
            dao.hapusRiwayatById(id)
        }
    }
}