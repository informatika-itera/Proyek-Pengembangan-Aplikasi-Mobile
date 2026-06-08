package com.example.nutriscan.presentation.screens.consultation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutriscan.domain.model.Conversation
import com.example.nutriscan.domain.model.Nutritionist
import com.example.nutriscan.domain.repository.ConsultationRepository
import com.example.nutriscan.domain.repository.SessionRepository
import com.example.nutriscan.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ConsultationUiState(
    val coins: Int = 0,
    val userName: String = "",
    val nutritionists: List<Nutritionist> = emptyList(),
    val conversations: List<Conversation> = emptyList(),
    val errorMessage: String? = null
)

class ConsultationViewModel(
    private val consultationRepository: ConsultationRepository,
    private val sessionRepository: SessionRepository,
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    private val error = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ConsultationUiState> = combine(
        sessionRepository.state,
        consultationRepository.observeConversations(),
        userProfileRepository.getProfile(),
        error
    ) { session, conversations, profile, err ->
        val userName = profile?.name?.takeIf { it.isNotBlank() }
            ?: session.userName.ifBlank { "Pengguna" }

        ConsultationUiState(
            coins = session.coins,
            userName = userName,
            nutritionists = consultationRepository.getNutritionists(),
            conversations = conversations.filter { it.userName == userName },
            errorMessage = err
        )
    }.catch {
        emit(ConsultationUiState(errorMessage = it.message ?: "Terjadi kesalahan"))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ConsultationUiState()
    )

    fun startConsultation(
        nutritionist: Nutritionist,
        onOpen: (Long) -> Unit
    ) {
        viewModelScope.launch {
            val state = uiState.value

            val existing = state.conversations.find {
                it.nutritionistId == nutritionist.id
            }

            if (existing != null) {
                onOpen(existing.id)
                return@launch
            }

            val paid = sessionRepository.trySpend(nutritionist.pricePerChat)

            if (!paid) {
                error.value =
                    "Coin tidak cukup. Kamu butuh ${nutritionist.pricePerChat} coin — top up dulu ya!"
                return@launch
            }

            val userName = state.userName.ifBlank { "Pengguna" }

            val conversation = consultationRepository.startOrGetConversation(
                nutritionistId = nutritionist.id,
                nutritionistName = nutritionist.name,
                nutritionistSpecialty = nutritionist.specialty,
                userName = userName
            )

            onOpen(conversation.id)
        }
    }

    fun consumeError() {
        error.value = null
    }
}