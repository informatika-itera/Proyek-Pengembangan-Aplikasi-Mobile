package com.example.metaforge.domain.model

data class DraftState(
    val allySlots: List<Hero?> = List(5) { null },
    val enemySlots: List<Hero?> = List(5) { null },
    val allyBans: List<Hero?> = List(5) { null },
    val enemyBans: List<Hero?> = List(5) { null }
) {
    val isReadyToAnalyze: Boolean
        get() = allySlots.any { it != null } && enemySlots.any { it != null }

    fun pickAlly(index: Int, hero: Hero?) = copy(allySlots = allySlots.toMutableList().apply { set(index, hero) })
    fun pickEnemy(index: Int, hero: Hero?) = copy(enemySlots = enemySlots.toMutableList().apply { set(index, hero) })
    fun banAlly(index: Int, hero: Hero?) = copy(allyBans = allyBans.toMutableList().apply { set(index, hero) })
    fun banEnemy(index: Int, hero: Hero?) = copy(enemyBans = enemyBans.toMutableList().apply { set(index, hero) })

    fun getAllPickedHeroes(): List<Hero> = (allySlots + enemySlots + allyBans + enemyBans).filterNotNull()

    fun isPickUnlocked(slotIndex: Int, isAllySlot: Boolean, isFirstPick: Boolean): Boolean {
        val allBansFilled = allyBans.all { it != null } && enemyBans.all { it != null }
        if (!allBansFilled) return false

        val blueSlots = if (isFirstPick) allySlots else enemySlots
        val redSlots = if (isFirstPick) enemySlots else allySlots

        val b1 = blueSlots.getOrNull(0) != null
        val b2 = blueSlots.getOrNull(1) != null
        val b3 = blueSlots.getOrNull(2) != null
        val b4 = blueSlots.getOrNull(3) != null
        val b5 = blueSlots.getOrNull(4) != null

        val r1 = redSlots.getOrNull(0) != null
        val r2 = redSlots.getOrNull(1) != null
        val r3 = redSlots.getOrNull(2) != null
        val r4 = redSlots.getOrNull(3) != null

        val isTargetBlue = if (isFirstPick) isAllySlot else !isAllySlot

        return if (isTargetBlue) {
            when (slotIndex) {
                0 -> true
                1, 2 -> b1 && r1 && r2
                3, 4 -> b1 && r1 && r2 && b2 && b3 && r3 && r4
                else -> false
            }
        } else {
            when (slotIndex) {
                0, 1 -> b1
                2, 3 -> b1 && r1 && r2 && b2 && b3
                4 -> b1 && r1 && r2 && b2 && b3 && r3 && r4 && b4 && b5
                else -> false
            }
        }
    }

    // Fungsi baru untuk memandu User di fase manakah mereka berada
    fun getTurnMessage(isFirstPick: Boolean): String {
        val allBansFilled = allyBans.all { it != null } && enemyBans.all { it != null }
        if (!allBansFilled) return "BAN PHASE: FILL ALL 10 BANS"

        val blueSlots = if (isFirstPick) allySlots else enemySlots
        val redSlots = if (isFirstPick) enemySlots else allySlots

        val b1 = blueSlots.getOrNull(0) != null
        val b2 = blueSlots.getOrNull(1) != null
        val b3 = blueSlots.getOrNull(2) != null
        val b4 = blueSlots.getOrNull(3) != null
        val b5 = blueSlots.getOrNull(4) != null

        val r1 = redSlots.getOrNull(0) != null
        val r2 = redSlots.getOrNull(1) != null
        val r3 = redSlots.getOrNull(2) != null
        val r4 = redSlots.getOrNull(3) != null
        val r5 = redSlots.getOrNull(4) != null

        return when {
            !b1 -> "PICK PHASE: BLUE TEAM'S TURN (1 PICK)"
            !r1 || !r2 -> "PICK PHASE: RED TEAM'S TURN (2 PICKS)"
            !b2 || !b3 -> "PICK PHASE: BLUE TEAM'S TURN (2 PICKS)"
            !r3 || !r4 -> "PICK PHASE: RED TEAM'S TURN (2 PICKS)"
            !b4 || !b5 -> "PICK PHASE: BLUE TEAM'S TURN (2 PICKS)"
            !r5 -> "PICK PHASE: RED TEAM'S TURN (LAST PICK)"
            else -> "DRAFT COMPLETE!"
        }
    }
}