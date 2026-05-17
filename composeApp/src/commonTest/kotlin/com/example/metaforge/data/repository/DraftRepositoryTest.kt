package com.example.metaforge.data.repository

import com.example.metaforge.domain.model.Hero
import com.example.metaforge.domain.model.HeroRole
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DraftRepositoryTest {

    // Test tanpa DataStore — pakai fake/stub
    private val fakePreferences = FakeDraftPreferences()
    private val repository = DraftRepositoryImpl(fakePreferences)

    private val testHero = Hero(
        id = 1,
        name = "Fanny",
        role = HeroRole.ASSASSIN,
        imageUrl = ""
    )

    @Test
    fun `initial draft state should have empty slots`() = runTest {
        val state = repository.getDraftState().first()
        assertTrue(state.allySlots.all { it == null })
        assertTrue(state.enemySlots.all { it == null })
    }

    @Test
    fun `pick ally hero should fill correct slot`() = runTest {
        repository.pickHero(0, isAlly = true, hero = testHero)
        val state = repository.getDraftState().first()
        assertEquals(testHero, state.allySlots[0])
    }

    @Test
    fun `pick enemy hero should fill correct slot`() = runTest {
        repository.pickHero(2, isAlly = false, hero = testHero)
        val state = repository.getDraftState().first()
        assertEquals(testHero, state.enemySlots[2])
    }

    @Test
    fun `remove hero should set slot to null`() = runTest {
        repository.pickHero(0, isAlly = true, hero = testHero)
        repository.removeHero(0, isAlly = true)
        val state = repository.getDraftState().first()
        assertNull(state.allySlots[0])
    }

    @Test
    fun `reset draft should clear all slots`() = runTest {
        repository.pickHero(0, true, testHero)
        repository.pickHero(0, false, testHero)
        repository.resetDraft()
        val state = repository.getDraftState().first()
        assertTrue(state.allySlots.all { it == null })
        assertTrue(state.enemySlots.all { it == null })
    }

    @Test
    fun `isReadyToAnalyze should be true when both sides have hero`() = runTest {
        repository.pickHero(0, true, testHero)
        repository.pickHero(0, false, testHero)
        val state = repository.getDraftState().first()
        assertTrue(state.isReadyToAnalyze)
    }

    @Test
    fun `getAllHeroes should return 30 heroes`() = runTest {
        val heroes = repository.getAllHeroes().first()
        assertEquals(30, heroes.size)
    }

    @Test
    fun `getHeroesByRole should return only matching role`() = runTest {
        val tanks = repository.getHeroesByRole(HeroRole.TANK).first()
        assertTrue(tanks.all { it.role == HeroRole.TANK })
    }
}