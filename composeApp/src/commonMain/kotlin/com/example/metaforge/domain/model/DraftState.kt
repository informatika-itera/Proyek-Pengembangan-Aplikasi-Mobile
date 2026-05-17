package com.example.metaforge.domain.model

data class DraftState(
    val allySlots: List<Hero?> = List(5) { null },
    val enemySlots: List<Hero?> = List(5) { null }
) {
    val isReadyToAnalyze: Boolean
        get() = allySlots.any { it != null } && enemySlots.any { it != null }

    val allyCount: Int get() = allySlots.count { it != null }
    val enemyCount: Int get() = enemySlots.count { it != null }

    fun pickAlly(index: Int, hero: Hero): DraftState {
        val newSlots = allySlots.toMutableList()
        newSlots[index] = hero
        return copy(allySlots = newSlots)
    }

    fun pickEnemy(index: Int, hero: Hero): DraftState {
        val newSlots = enemySlots.toMutableList()
        newSlots[index] = hero
        return copy(enemySlots = newSlots)
    }

    fun removeAlly(index: Int): DraftState {
        val newSlots = allySlots.toMutableList()
        newSlots[index] = null
        return copy(allySlots = newSlots)
    }

    fun removeEnemy(index: Int): DraftState {
        val newSlots = enemySlots.toMutableList()
        newSlots[index] = null
        return copy(enemySlots = newSlots)
    }

    fun getAllPickedHeroes(): List<Hero> =
        (allySlots + enemySlots).filterNotNull()
}