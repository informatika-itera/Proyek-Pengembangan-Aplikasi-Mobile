package id.pusakakata.domain.usecase

import id.pusakakata.domain.model.LegendaryCard
import id.pusakakata.domain.model.Rarity
import kotlin.random.Random

class GachaSystem(private val availableCards: List<LegendaryCard>) {

    fun drawCard(): LegendaryCard {
        val randomValue = Random.nextInt(1, 101) // 1-100
        
        val selectedRarity = when {
            randomValue <= Rarity.MYTHIC.weight -> Rarity.MYTHIC
            randomValue <= (Rarity.MYTHIC.weight + Rarity.EPIC.weight) -> Rarity.EPIC
            randomValue <= (Rarity.MYTHIC.weight + Rarity.EPIC.weight + Rarity.RARE.weight) -> Rarity.RARE
            else -> Rarity.COMMON
        }

        val cardsInRarity = availableCards.filter { it.rarity == selectedRarity }
        
        // Fallback jika tidak ada kartu di rarity tersebut (opsional)
        return if (cardsInRarity.isNotEmpty()) {
            cardsInRarity.random()
        } else {
            availableCards.random()
        }
    }
}
