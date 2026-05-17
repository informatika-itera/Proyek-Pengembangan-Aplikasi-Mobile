package com.example.metaforge.data.repository

import com.example.metaforge.data.local.HeroDataSource
import com.example.metaforge.data.local.datastore.DraftPreferences
import com.example.metaforge.domain.model.DraftState
import com.example.metaforge.domain.model.Hero
import com.example.metaforge.domain.repository.DraftRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update

class DraftRepositoryImpl(
    // PERBAIKAN: Menjadikan preferensi nullable agar mudah di-test tanpa Fake object
    private val draftPreferences: DraftPreferences? = null
) : DraftRepository {

    private val _draftState = MutableStateFlow(DraftState())

    override fun getDraftState(): Flow<DraftState> = _draftState.asStateFlow()

    override fun getAllHeroes(): Flow<List<Hero>> = flowOf(HeroDataSource.allHeroes)

    override suspend fun pickHero(slotIndex: Int, isAlly: Boolean, hero: Hero?) {
        _draftState.update { current ->
            if (isAlly) current.pickAlly(slotIndex, hero)
            else current.pickEnemy(slotIndex, hero)
        }
    }

    override suspend fun banHero(slotIndex: Int, isAlly: Boolean, hero: Hero?) {
        _draftState.update { current ->
            if (isAlly) current.banAlly(slotIndex, hero)
            else current.banEnemy(slotIndex, hero)
        }
    }

    override suspend fun clearDraft() {
        _draftState.value = DraftState()
    }
}