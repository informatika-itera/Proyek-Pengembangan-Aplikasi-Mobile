package com.example.metaforge.domain.repository

import com.example.metaforge.domain.model.DraftState
import com.example.metaforge.domain.model.Hero
import kotlinx.coroutines.flow.Flow

interface DraftRepository {
    fun getDraftState(): Flow<DraftState>
    fun getAllHeroes(): Flow<List<Hero>>
    suspend fun pickHero(slotIndex: Int, isAlly: Boolean, hero: Hero?)
    suspend fun banHero(slotIndex: Int, isAlly: Boolean, hero: Hero?)
    suspend fun clearDraft()
}