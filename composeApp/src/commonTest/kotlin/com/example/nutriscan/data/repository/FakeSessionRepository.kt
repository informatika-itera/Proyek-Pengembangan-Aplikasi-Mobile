package com.example.nutriscan.data.repository

import com.example.nutriscan.domain.model.UserRole
import com.example.nutriscan.domain.repository.SessionRepository
import com.example.nutriscan.domain.repository.SessionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * In-memory fake SessionRepository untuk unit test.
 */
class FakeSessionRepository : SessionRepository {

    private val _state = MutableStateFlow(
        SessionState(
            isLoggedIn = false,
            role = UserRole.USER,
            userName = "",
            coins = 100,
            onboardingCompleted = false
        )
    )

    private val _coins = MutableStateFlow(100)

    override val state: Flow<SessionState> = _state
    override val coins: Flow<Int> = _coins

    override suspend fun login(role: UserRole, name: String) {
        _state.value = _state.value.copy(
            isLoggedIn = true,
            role = role,
            userName = name
        )
    }

    override suspend fun logout() {
        _state.value = _state.value.copy(isLoggedIn = false)
    }

    override suspend fun topUp(amount: Int) {
        val updated = _coins.value + amount
        _coins.value = updated
        _state.value = _state.value.copy(coins = updated)
    }

    override suspend fun trySpend(amount: Int): Boolean {
        val current = _coins.value

        return if (current >= amount) {
            val updated = current - amount
            _coins.value = updated
            _state.value = _state.value.copy(coins = updated)
            true
        } else {
            false
        }
    }

    fun setState(state: SessionState) {
        _state.value = state
        _coins.value = state.coins
    }

    fun currentState(): SessionState = _state.value

    fun currentCoins(): Int = _coins.value
}