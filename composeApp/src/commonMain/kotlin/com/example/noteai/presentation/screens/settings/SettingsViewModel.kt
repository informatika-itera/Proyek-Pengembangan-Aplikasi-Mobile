package com.example.noteai.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteai.data.local.datastore.UserPreferences
import com.example.noteai.domain.model.Note
import com.example.noteai.domain.model.VulnSeverity
import com.example.noteai.domain.model.VulnStatus
import com.example.noteai.domain.repository.NoteRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: NoteRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<SettingsEvent>()
    val events: SharedFlow<SettingsEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.getAllNotes(),
                userPreferences.isDarkMode
            ) { notes, isDarkMode ->
                val totalCount = notes.size
                val criticalCount = notes.count { it.severity == VulnSeverity.CRITICAL }
                val highCount = notes.count { it.severity == VulnSeverity.HIGH }
                val mediumCount = notes.count { it.severity == VulnSeverity.MEDIUM }
                val lowCount = notes.count { it.severity == VulnSeverity.LOW }
                
                val paidCount = notes.count { it.status == VulnStatus.PAID }
                val resolvedCount = notes.count { it.status == VulnStatus.RESOLVED }

                val exportData = generateExportJson(notes)

                SettingsUiState(
                    totalFindings = totalCount,
                    criticalCount = criticalCount,
                    highCount = highCount,
                    mediumCount = mediumCount,
                    lowCount = lowCount,
                    paidCount = paidCount,
                    resolvedCount = resolvedCount,
                    isDarkMode = isDarkMode,
                    exportJson = exportData
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setDarkMode(enabled)
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.getAllNotes().take(1).collect { notesList ->
                val ids = notesList.map { it.id }
                if (ids.isNotEmpty()) {
                    repository.deleteNotes(ids)
                }
                _events.emit(SettingsEvent.DataCleared)
            }
        }
    }

    private fun generateExportJson(notes: List<Note>): String {
        val sb = StringBuilder()
        sb.append("[\n")
        notes.forEachIndexed { index, note ->
            sb.append("  {\n")
            sb.append("    \"id\": ${note.id},\n")
            sb.append("    \"title\": \"${escapeJson(note.title)}\",\n")
            sb.append("    \"targetUrl\": \"${escapeJson(note.targetUrl)}\",\n")
            sb.append("    \"severity\": \"${note.severity.name}\",\n")
            sb.append("    \"status\": \"${note.status.name}\",\n")
            sb.append("    \"vulnType\": \"${note.vulnType.name}\",\n")
            sb.append("    \"content\": \"${escapeJson(note.content)}\"\n")
            sb.append("  }")
            if (index < notes.size - 1) sb.append(",")
            sb.append("\n")
        }
        sb.append("]")
        return sb.toString()
    }

    private fun escapeJson(str: String): String {
        return str.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
    }
}

data class SettingsUiState(
    val totalFindings: Int = 0,
    val criticalCount: Int = 0,
    val highCount: Int = 0,
    val mediumCount: Int = 0,
    val lowCount: Int = 0,
    val paidCount: Int = 0,
    val resolvedCount: Int = 0,
    val isDarkMode: Boolean = true,
    val exportJson: String = ""
)

sealed interface SettingsEvent {
    data object DataCleared : SettingsEvent
}
