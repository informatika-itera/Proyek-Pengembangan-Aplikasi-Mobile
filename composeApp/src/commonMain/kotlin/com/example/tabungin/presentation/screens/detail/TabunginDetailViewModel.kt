package com.example.tabungin.presentation.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tabungin.data.local.datastore.UserPreferences
import com.example.tabungin.domain.model.Setoran
import com.example.tabungin.domain.model.Target
import com.example.tabungin.domain.usecase.*
import com.example.tabungin.notification.NotificationService
import com.example.tabungin.presentation.components.formatRupiah
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class DetailUiState(
    val target: Target?          = null,
    val setoranList: List<Setoran> = emptyList(),
    val isLoading: Boolean       = true,
    val error: String?           = null,
    val showSetoranDialog: Boolean = false,
    val targetAchieved: Boolean  = false,
    val isDeleted: Boolean      = false
)

class DetailViewModel(
    private val targetId: Long,
    private val getTargetByIdUseCase: GetTargetByIdUseCase,
    private val getSetoranByTargetUseCase: GetSetoranByTargetUseCase,
    private val insertSetoranUseCase: InsertSetoranUseCase,
    private val deleteSetoranUseCase: DeleteSetoranUseCase,
    private val deleteTargetUseCase: DeleteTargetUseCase,
    private val notificationService: NotificationService,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init { observeData() }

    private fun observeData() {
        combine(
            getTargetByIdUseCase(targetId),
            getSetoranByTargetUseCase(targetId)
        ) { target, setoran -> target to setoran }
            .onEach { (target, setoran) ->
                val totalTabungan = setoran.sumOf { it.amount }
                val achieved = target != null && totalTabungan >= target.targetAmount && !target.tercapai
                _uiState.update {
                    it.copy(target = target, setoranList = setoran, isLoading = false, targetAchieved = achieved)
                }
            }
            .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
            .launchIn(viewModelScope)
    }

    fun tambahSetoran(amount: Double, catatan: String) {
        viewModelScope.launch {
            val today = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date.toString()

            val currentTarget = _uiState.value.target
            val currentTotal = _uiState.value.setoranList.sumOf { it.amount }
            val newTotal = currentTotal + amount
            val wasNotCompleted = currentTarget != null && !currentTarget.tercapai && currentTotal < currentTarget.targetAmount

            runCatching {
                insertSetoranUseCase(
                    Setoran(targetId = targetId, amount = amount, catatan = catatan, tanggal = today)
                )
            }.onSuccess {
                // Check if target is now achieved
                if (currentTarget != null && wasNotCompleted && newTotal >= currentTarget.targetAmount) {
                    // Check if notification is enabled
                    val isEnabled = userPreferences.notifTargetTercapai.first()
                    if (isEnabled) {
                        val formattedAmount = formatRupiah(currentTarget.targetAmount)
                        notificationService.showTargetAchievedNotification(
                            targetName = currentTarget.nama,
                            amount = formattedAmount
                        )
                    }
                }
            }.onFailure { e ->
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun deleteSetoran(id: Long) {
        viewModelScope.launch {
            runCatching { deleteSetoranUseCase(id) }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun showSetoranDialog()    = _uiState.update { it.copy(showSetoranDialog = true) }
    fun dismissSetoranDialog() = _uiState.update { it.copy(showSetoranDialog = false) }
    fun clearError()           = _uiState.update { it.copy(error = null) }
    fun clearTargetAchieved()  = _uiState.update { it.copy(targetAchieved = false) }

    fun deleteTarget() {
        viewModelScope.launch {
            runCatching { deleteTargetUseCase(targetId) }
                .onSuccess {
                    _uiState.update { it.copy(isDeleted = true) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
        }
    }
}

