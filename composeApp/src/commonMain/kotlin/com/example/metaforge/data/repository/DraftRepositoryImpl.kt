package com.example.metaforge.data.repository

import com.example.metaforge.data.local.HeroDataSource
import com.example.metaforge.data.local.datastore.DraftPreferences
import com.example.metaforge.domain.model.DraftState
import com.example.metaforge.domain.model.Hero
import com.example.metaforge.domain.model.HeroRole
import com.example.metaforge.domain.repository.DraftRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class DraftRepositoryImpl(
    private val draftPreferences: DraftPreferences
) : DraftRepository {

    private val _draftState = MutableStateFlow(DraftState())

    override fun getAllHeroes(): Flow<List<Hero>> = flow {
        emit(HeroDataSource.allHeroes)
    }

    override fun getHeroesByRole(role: HeroRole): Flow<List<Hero>> = flow {
        emit(HeroDataSource.getByRole(role))
    }

    override fun searchHeroes(query: String): Flow<List<Hero>> = flow {
        emit(HeroDataSource.search(query))
    }

    override fun getDraftState(): Flow<DraftState> =
        _draftState.asStateFlow()

    override suspend fun pickHero(
        slotIndex: Int, isAlly: Boolean, hero: Hero
    ) {
        _draftState.update { current ->
            if (isAlly) current.pickAlly(slotIndex, hero)
            else current.pickEnemy(slotIndex, hero)
        }
    }

    override suspend fun removeHero(slotIndex: Int, isAlly: Boolean) {
        _draftState.update { current ->
            if (isAlly) current.removeAlly(slotIndex)
            else current.removeEnemy(slotIndex)
        }
    }

    override suspend fun resetDraft() {
        _draftState.value = DraftState()
        draftPreferences.clearDraft()
    }

    override suspend fun saveDraft() {
        val state = _draftState.value
        val allyNames = state.allySlots
            .filterNotNull().joinToString(",") { it.name }
        val enemyNames = state.enemySlots
            .filterNotNull().joinToString(",") { it.name }
        draftPreferences.saveLastDraft("$allyNames|$enemyNames")
    }

    override fun getSavedDraftExists(): Flow<Boolean> =
        draftPreferences.getLastDraft().map { it != null && it.isNotEmpty() }
}