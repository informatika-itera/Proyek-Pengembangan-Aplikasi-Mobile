package com.example.metaforge.presentation.screens.draft

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.metaforge.data.local.HeroDataSource
import com.example.metaforge.domain.model.DraftState
import com.example.metaforge.domain.repository.DraftRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DraftViewModel(
    private val draftRepository: DraftRepository
) : ViewModel() {

    private var pickPosition = 1
    private var isFirstPick = true
    private var preferredRole = "MARKSMAN"

    fun setupDraft(pickPos: Int, isFirst: Boolean, role: String) {
        this.pickPosition = pickPos
        this.isFirstPick = isFirst
        this.preferredRole = role
    }

    val uiState: StateFlow<DraftUiState> = draftRepository.getDraftState()
        .map { state ->
            val userSlotIndex = pickPosition - 1
            val isTurn = state.isPickUnlocked(userSlotIndex, true, isFirstPick) && state.allySlots.getOrNull(userSlotIndex) == null
            val recommendation = if (isTurn) generateRecommendation(state, preferredRole) else null
            val turnMessage = state.getTurnMessage(isFirstPick)

            DraftUiState.Ready(state, isTurn, recommendation, turnMessage)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DraftUiState.Loading
        )

    fun removeHero(slotIndex: Int, isAlly: Boolean, isBan: Boolean) {
        viewModelScope.launch {
            if (isBan) draftRepository.banHero(slotIndex, isAlly, null)
            else draftRepository.pickHero(slotIndex, isAlly, null)
        }
    }

    private fun generateRecommendation(state: DraftState, roleName: String): DraftRecommendation? {
        val unavailable = state.getAllPickedHeroes().map { it.name }
        val available = HeroDataSource.allHeroes.filter { it.name !in unavailable && it.role.name == roleName }

        if (available.isEmpty()) return null

        val enemyHeroes = state.enemySlots.filterNotNull().map { it.name }

        val ssTier = listOf("Fanny", "Mathilda", "Joy", "Nolan")
        val sTier = listOf("Chou", "Khufra", "Beatrix", "Novaria", "Valentina", "Ling")

        val weakAgainstMap = mapOf(
            "Fanny" to listOf("Khufra", "Chou", "Franco", "Saber"),
            "Joy" to listOf("Phoveus", "Khufra", "Minsitthar"),
            "Beatrix" to listOf("Chou", "Natalia", "Lancelot"),
            "Khufra" to listOf("Valir", "Diggie"),
            "Mathilda" to listOf("Chou", "Kaja", "Franco")
        )

        val strongAgainstMap = mapOf(
            "Khufra" to listOf("Fanny", "Joy", "Ling", "Lancelot"),
            "Chou" to listOf("Fanny", "Beatrix"),
            "Phoveus" to listOf("Joy", "Wanwan", "Ling"),
            "Saber" to listOf("Fanny", "Joy", "Ling")
        )

        val scoredHeroes = available.map { hero ->
            var score = 0
            if (hero.name in ssTier) score += 100
            else if (hero.name in sTier) score += 50
            else score += 10

            val strongAgainst = strongAgainstMap[hero.name] ?: emptyList()
            val counteredEnemiesCount = enemyHeroes.count { it in strongAgainst }
            score += (counteredEnemiesCount * 50)

            hero to score
        }.sortedByDescending { it.second }

        val bestHero = scoredHeroes.firstOrNull()?.first ?: return null

        val countersOfBest = weakAgainstMap[bestHero.name] ?: listOf("Chou", "Franco", "Kaja")
        val unbannedCounters = countersOfBest.filter { it !in unavailable }

        val warning = if (unbannedCounters.isNotEmpty()) {
            "WARNING: ${unbannedCounters.first()} (Counter) is open and can be picked by the enemy!"
        } else null

        val countersEnemy = enemyHeroes.any { it in (strongAgainstMap[bestHero.name] ?: emptyList()) }
        val reason = if (countersEnemy && unbannedCounters.isEmpty()) {
            "Highly effective! Counters current enemy picks and has no direct counters left open."
        } else if (countersEnemy) {
            "Effectively counters enemy composition and fits the meta tier list."
        } else {
            "Highest meta priority available for the ${bestHero.role.name} role."
        }

        return DraftRecommendation(bestHero.name, bestHero.role.name, reason, warning)
    }
}