package com.example.tabungin.presentation.screens.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tabungin.domain.model.Setoran
import com.example.tabungin.domain.model.Target
import com.example.tabungin.domain.usecase.GetAllSetoranUseCase
import com.example.tabungin.domain.usecase.GetAllTargetsUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class StatisticsUiState(
    val targets: List<Target> = emptyList(),
    val setoranList: List<Setoran> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    // Summary statistics
    val totalTabungan: Double = 0.0,
    val totalTarget: Double = 0.0,
    val rataRataTabungan: Double = 0.0,
    val jumlahTarget: Int = 0,
    val targetTercapai: Int = 0,
    val targetAktif: Int = 0,
    val totalSetoran: Int = 0,
    val setoranBulanIni: Int = 0,
    val tabunganBulanIni: Double = 0.0
)

class StatisticsViewModel(
    private val getAllTargetsUseCase: GetAllTargetsUseCase,
    private val getAllSetoranUseCase: GetAllSetoranUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                getAllTargetsUseCase(),
                getAllSetoranUseCase()
            ) { targets, setoran ->
                calculateStatistics(targets, setoran)
            }
                .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
                .collect { stats ->
                    _uiState.value = stats
                }
        }
    }

    private fun calculateStatistics(targets: List<Target>, setoran: List<Setoran>): StatisticsUiState {
        val totalTabungan = targets.sumOf { it.terkumpul }
        val totalTarget = targets.sumOf { it.targetAmount }
        val rataRata = if (targets.isNotEmpty()) totalTabungan / targets.size else 0.0
        val targetTercapai = targets.count { it.terkumpul >= it.targetAmount }
        val targetAktif = targets.count { it.terkumpul < it.targetAmount }

        // Calculate monthly statistics
        val currentLocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val currentMonth = currentLocalDateTime.monthNumber
        val currentYear = currentLocalDateTime.year
        val setoranBulanIni = setoran.count { setoranItem ->
            try {
                val parts = setoranItem.tanggal.split("-")
                if (parts.size >= 2) {
                    parts[0].toIntOrNull() == currentYear && parts[1].toIntOrNull() == currentMonth
                } else false
            } catch (e: Exception) {
                false
            }
        }
        val tabunganBulanIni = setoran
            .filter { setoranItem ->
                try {
                    val parts = setoranItem.tanggal.split("-")
                    if (parts.size >= 2) {
                        parts[0].toIntOrNull() == currentYear && parts[1].toIntOrNull() == currentMonth
                    } else false
                } catch (e: Exception) {
                    false
                }
            }
            .sumOf { it.amount }

        return StatisticsUiState(
            targets = targets,
            setoranList = setoran,
            isLoading = false,
            totalTabungan = totalTabungan,
            totalTarget = totalTarget,
            rataRataTabungan = rataRata,
            jumlahTarget = targets.size,
            targetTercapai = targetTercapai,
            targetAktif = targetAktif,
            totalSetoran = setoran.size,
            setoranBulanIni = setoranBulanIni,
            tabunganBulanIni = tabunganBulanIni
        )
    }

    fun clearError() = _uiState.update { it.copy(error = null) }
}