package com.example.metaforge.domain.repository

import com.example.metaforge.domain.model.DraftState
import com.example.metaforge.domain.model.Hero
import com.example.metaforge.domain.model.HeroRole
import kotlinx.coroutines.flow.Flow

interface DraftRepository {
    fun getAllHeroes(): Flow<List<Hero>>
    fun getHeroesByRole(role: HeroRole): Flow<List<Hero>>
    fun searchHeroes(query: String): Flow<List<Hero>>
    fun getDraftState(): Flow<DraftState>
    suspend fun pickHero(slotIndex: Int, isAlly: Boolean, hero: Hero)
    suspend fun removeHero(slotIndex: Int, isAlly: Boolean)
    suspend fun resetDraft()
    suspend fun saveDraft()
    fun getSavedDraftExists(): Flow<Boolean>
}