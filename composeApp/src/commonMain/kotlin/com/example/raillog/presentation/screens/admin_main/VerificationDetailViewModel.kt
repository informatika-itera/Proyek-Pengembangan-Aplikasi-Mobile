package com.example.raillog.presentation.screens.admin_main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raillog.data.remote.api.SystemPrompts
import com.example.raillog.domain.model.SupplyItem
import com.example.raillog.domain.model.SupplyStatus
import com.example.raillog.domain.model.TechnicalDocument
import com.example.raillog.domain.model.VerificationStatus
import com.example.raillog.domain.repository.AIRepository
import com.example.raillog.domain.repository.SupplyRepository
import com.example.raillog.domain.repository.TechnicalDocumentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

// ==================== AI VALIDATION STATE ====================

enum class AIValidationStatus { IDLE, LOADING, SUCCESS, ERROR }

data class AIValidationState(
    val status: AIValidationStatus = AIValidationStatus.IDLE,
    val result: String? = null,
    val errorMessage: String? = null
)

// ==================== VIEWMODEL ====================

class VerificationDetailViewModel(
    private val supplyRepository: SupplyRepository,
    private val technicalDocumentRepository: TechnicalDocumentRepository,
    private val aiRepository: AIRepository
) : ViewModel() {

    private val _selectedItem = MutableStateFlow<SupplyItem?>(null)
    val selectedItem: StateFlow<SupplyItem?> = _selectedItem.asStateFlow()

    private val _document = MutableStateFlow<TechnicalDocument?>(null)
    val document = _document.asStateFlow()

    private val _aiValidation = MutableStateFlow(AIValidationState())
    val aiValidation: StateFlow<AIValidationState> = _aiValidation.asStateFlow()

    fun loadItem(id: Long) {
        viewModelScope.launch {
            supplyRepository
                .getItemById(id)
                .collect { item ->
                    _selectedItem.value = item

                    // FIX: Ganti nested collect dengan firstOrNull.
                    // Nested collect sebelumnya memblokir outer collect,
                    // sehingga update status dari database tidak ter-propagasi.
                    if (item != null && _document.value == null) {
                        val documentTitle = item.documentRef
                        if (!documentTitle.isNullOrBlank()) {
                            val doc = technicalDocumentRepository
                                .getDocumentByTitle(documentTitle)
                                .firstOrNull()
                            _document.value = doc
                        }
                    }

                    // Auto-run AI validation sekali saat item pertama kali dimuat
                    if (item != null && _aiValidation.value.status == AIValidationStatus.IDLE) {
                        runAIValidation(item)
                    }
                }
        }
    }

    fun retryAiValidation() {
        _selectedItem.value?.let { runAIValidation(it) }
    }

    private fun runAIValidation(item: SupplyItem) {
        _aiValidation.value = AIValidationState(status = AIValidationStatus.LOADING)

        viewModelScope.launch {
            val docContent = _document.value?.content ?: "Tidak ada dokumen terlampir."

            val prompt = """
                Berikut adalah data pengajuan material yang perlu diverifikasi:
                - Project Code: ${item.partCode}
                - Material: ${item.name}
                - Kategori: ${item.category.name}
                - Kuantitas: ${item.quantity} ${item.unit}
                - Supplier: ${item.supplier}
                - Prioritas: ${item.priority.name}
                - Catatan: ${item.notes.ifBlank { "Tidak ada catatan" }}
                - Status Stok: ${item.status.name}
                - Referensi Dokumen: ${item.documentRef ?: "Tidak ada"}
                
                Konten Dokumen OCR:
                $docContent
            """.trimIndent()

            val result = aiRepository.chat(prompt, SystemPrompts.FORM_VALIDATOR)

            result.fold(
                onSuccess = { validationText ->
                    _aiValidation.value = AIValidationState(
                        status = AIValidationStatus.SUCCESS,
                        result = validationText
                    )
                },
                onFailure = { error ->
                    _aiValidation.value = AIValidationState(
                        status = AIValidationStatus.ERROR,
                        errorMessage = error.message ?: "Terjadi kesalahan saat validasi AI."
                    )
                }
            )
        }
    }

    fun verifyItem(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _selectedItem.value?.let { item ->
                // FIX: Gunakan updateStatus (lebih ringan) daripada updateItem
                // untuk menghindari overwrite field lain secara tidak sengaja.
                // updateItem hanya dipanggil jika ada perubahan data penuh.
                supplyRepository.updateStatus(item.id, SupplyStatus.VERIFIED)

                _document.value?.let { document ->
                    technicalDocumentRepository.updateVerification(
                        id = document.id,
                        status = VerificationStatus.APPROVED,
                        aiSummary = _aiValidation.value.result ?: document.aiSummary
                    )
                }
                // FIX: onSuccess() dipanggil setelah updateStatus selesai
                // (karena updateStatus adalah suspend fun — dijamin selesai dulu
                // sebelum baris ini dieksekusi).
                onSuccess()
            }
        }
    }

    fun rejectItem(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _selectedItem.value?.let { item ->
                supplyRepository.updateStatus(item.id, SupplyStatus.REJECTED)

                _document.value?.let { document ->
                    technicalDocumentRepository.updateVerification(
                        id = document.id,
                        status = VerificationStatus.FLAGGED,
                        aiSummary = _aiValidation.value.result ?: document.aiSummary
                    )
                }
                onSuccess()
            }
        }
    }
}