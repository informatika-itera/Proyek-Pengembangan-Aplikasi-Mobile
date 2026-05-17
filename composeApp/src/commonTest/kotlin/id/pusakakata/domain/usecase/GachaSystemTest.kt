package id.pusakakata.domain.usecase

import id.pusakakata.domain.model.LegendaryCard
import id.pusakakata.domain.model.Rarity
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GachaSystemTest {

    private val testCards = listOf(
        LegendaryCard("1", "Mythic Card", "Desc", Rarity.MYTHIC, "", "Origin"),
        LegendaryCard("2", "Epic Card", "Desc", Rarity.EPIC, "", "Origin"),
        LegendaryCard("3", "Rare Card", "Desc", Rarity.RARE, "", "Origin"),
        LegendaryCard("4", "Common Card", "Desc", Rarity.COMMON, "", "Origin")
    )

    private val gachaSystem = GachaSystem(testCards)

    @Test
    fun drawCard_returnsNotNull() {
        val card = gachaSystem.drawCard()
        assertNotNull(card)
    }

    @Test
    fun drawCard_returnsCardFromList() {
        val card = gachaSystem.drawCard()
        assertTrue(testCards.contains(card))
    }

    @Test
    fun drawCard_distributionTest() {
        val results = mutableMapOf<Rarity, Int>()
        repeat(100) {
            val card = gachaSystem.drawCard()
            results[card.rarity] = (results[card.rarity] ?: 0) + 1
        }
        
        // At least one card drawn (very likely for 100 draws)
        assertTrue(results.values.sum() == 100)
    }
}
