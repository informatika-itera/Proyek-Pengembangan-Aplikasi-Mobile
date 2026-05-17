package com.example.tabungin.presentation.screens.add_edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tabungin.domain.model.Target
import com.example.tabungin.domain.usecase.GetTargetByIdUseCase
import com.example.tabungin.domain.usecase.InsertTargetUseCase
import com.example.tabungin.domain.usecase.UpdateTargetUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.*

data class AddEditUiState(
    val nama: String         = "",
    val targetAmount: String = "",
    val deadline: String     = "",
    val icon: String         = "🎯",
    val warna: String        = "#4CAF50",
    val isLoading: Boolean   = false,
    val isSaved: Boolean     = false,
    val error: String?       = null,
    val isEditMode: Boolean  = false
)

class AddEditViewModel(
    private val targetId: Long?,
    private val getTargetByIdUseCase: GetTargetByIdUseCase,
    private val insertTargetUseCase: InsertTargetUseCase,
    private val updateTargetUseCase: UpdateTargetUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditUiState())
    val uiState: StateFlow<AddEditUiState> = _uiState.asStateFlow()

    init { if (targetId != null) loadTarget(targetId) }

    private fun loadTarget(id: Long) {
        getTargetByIdUseCase(id)
            .filterNotNull()
            .take(1)
            .onEach { t ->
                _uiState.update {
                    it.copy(
                        nama         = t.nama,
                        targetAmount = t.targetAmount.toLong().toString(),
                        deadline     = t.deadline,
                        icon         = t.icon,
                        warna        = t.warna,
                        isEditMode   = true
                    )
                }
            }
            .catch { e -> _uiState.update { it.copy(error = e.message) } }
            .launchIn(viewModelScope)
    }

    fun onNamaChange(v: String)         = _uiState.update { it.copy(nama = v) }
    fun onTargetAmountChange(v: String) = _uiState.update { it.copy(targetAmount = v) }
    fun onDeadlineChange(v: String)     = _uiState.update { it.copy(deadline = v) }
    fun onIconChange(v: String)         = _uiState.update { it.copy(icon = v) }
    fun onWarnaChange(v: String)        = _uiState.update { it.copy(warna = v) }
    fun clearError()                    = _uiState.update { it.copy(error = null) }

    val isFormValid: Boolean
        get() = uiState.value.run {
            nama.isNotBlank() &&
                    targetAmount.toDoubleOrNull()?.let { it > 0 } == true &&
                    deadline.isNotBlank()
        }

    fun saveTarget() {
        val s = uiState.value
        if (!isFormValid) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            runCatching {
                val target = Target(
                    id           = targetId ?: 0L,
                    nama         = s.nama.trim(),
                    targetAmount = s.targetAmount.toDouble(),
                    deadline     = s.deadline,
                    icon         = s.icon,
                    warna        = s.warna
                )
                if (s.isEditMode) updateTargetUseCase(target) else insertTargetUseCase(target)
            }.onSuccess {
                _uiState.update { it.copy(isLoading = false, isSaved = true) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
