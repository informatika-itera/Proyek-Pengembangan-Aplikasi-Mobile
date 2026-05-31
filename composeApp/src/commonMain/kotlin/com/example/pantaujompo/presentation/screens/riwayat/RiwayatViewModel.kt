package com.example.pantaujompo.presentation.screens.riwayat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pantaujompo.data.local.room.MakananDao
import com.example.pantaujompo.data.local.room.MakananEntity
import com.example.pantaujompo.data.local.room.RiwayatDao
import com.example.pantaujompo.data.local.room.RiwayatEntity
import com.example.pantaujompo.data.local.datastore.UserPreferences
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone

enum class TimeFilter { HARIAN, MINGGUAN, BULANAN, SEMUA }

class RiwayatViewModel(
    private val riwayatDao: RiwayatDao,
    private val makananDao: MakananDao,
    private val userPreferences: UserPreferences
) : ViewModel() {

    // State Filter Waktu
    private val _timeFilter = MutableStateFlow(TimeFilter.SEMUA)
    val timeFilter: StateFlow<TimeFilter> = _timeFilter.asStateFlow()

    fun setTimeFilter(filter: TimeFilter) {
        _timeFilter.value = filter
    }

    private fun getStartTimestamp(filter: TimeFilter): Long {
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        when (filter) {
            TimeFilter.HARIAN -> {
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
            }
            TimeFilter.MINGGUAN -> {
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
            }
            TimeFilter.BULANAN -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
            }
            TimeFilter.SEMUA -> return 0L
        }
        return calendar.timeInMillis
    }

    // 1. Ambil data Olahraga (Lari) dengan Filter
    val riwayatLariState: StateFlow<List<RiwayatEntity>> = combine(
        riwayatDao.getAllRiwayat(),
        _timeFilter
    ) { riwayatList, filter ->
        val startTs = getStartTimestamp(filter)
        riwayatList.filter { it.tanggal >= startTs }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 2. Ambil data Makanan (AI) dengan Filter
    val makananState: StateFlow<List<MakananEntity>> = combine(
        makananDao.getAllMakanan(),
        _timeFilter
    ) { makananList, filter ->
        val startTs = getStartTimestamp(filter)
        makananList.filter { it.tanggal >= startTs }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        
    // 3. Target Kalori
    val targetKalori = userPreferences.targetKalori
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 2000)

    val languageState = userPreferences.language
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "id")

    fun updateTargetKalori(target: Int) {
        viewModelScope.launch {
            userPreferences.setTargetKalori(target)
        }
    }

    // 3. Fungsi Hapus Lari
    fun deleteActivity(id: Int) {
        viewModelScope.launch {
            riwayatDao.hapusRiwayatById(id)
        }
    }

    // 4. Fungsi Simpan Makanan
    fun insertMakanan(makanan: MakananEntity) {
        viewModelScope.launch {
            makananDao.insertMakanan(makanan)
        }
    }

    // 5. Fungsi Hapus Makanan
    fun deleteMakanan(id: Int) {
        viewModelScope.launch {
            makananDao.hapusMakananById(id)
        }
    }
}