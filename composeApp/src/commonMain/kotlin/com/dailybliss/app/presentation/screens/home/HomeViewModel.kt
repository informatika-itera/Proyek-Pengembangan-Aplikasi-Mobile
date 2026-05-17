package com.dailybliss.app.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailybliss.app.data.local.datastore.UserPreferences
import com.dailybliss.app.domain.repository.AIRepository
import com.dailybliss.app.presentation.screens.ai.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val greeting: String) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

class HomeViewModel(
    private val aiRepository: AIRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        observeSettings()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            kotlinx.coroutines.flow.combine(
                userPreferences.nickname,
                userPreferences.aiLanguageStyle
            ) { nickname, style ->
                nickname to style
            }.collectLatest { (nickname, style) ->
                fetchGreeting(nickname, style)
            }
        }
    }

    private fun fetchGreeting(nickname: String, style: String) {
        viewModelScope.launch {
            try {
                _uiState.value = HomeUiState.Loading
                val greeting = aiRepository.chat(
                    listOf(
                        ChatMessage(
                            role = "user",
                            text = """
                                Berikan sapaan singkat, hangat, dan puitis untuk pengguna bernama '$nickname' di aplikasi jurnal 'DailyBliss'. 
                                Gunakan gaya bahasa: '$style'.
                                Maksimal 2 kalimat. 
                                Berikan kesan tenang dan blissful. 
                                Sapa pengguna dengan namanya. 
                                Jangan gunakan markdown.
                            """.trimIndent()
                        )
                    )
                )
                _uiState.value = HomeUiState.Success(greeting)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Success("Selamat datang kembali, $nickname. Semoga harimu menyenangkan.")
            }
        }
    }
}
