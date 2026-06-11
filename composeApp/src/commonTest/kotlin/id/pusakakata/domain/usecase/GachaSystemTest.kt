package id.pusakakata.domain.usecase

import id.pusakakata.domain.model.LegendaryCard
import id.pusakakata.domain.model.Rarity
import kotlin.test.*

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
    fun drawCard_canDrawMythic() {
        var mythicDrawn = false
        repeat(1000) {
            val card = gachaSystem.drawCard()
            if (card.rarity == Rarity.MYTHIC) mythicDrawn = true
        }
        assertTrue(mythicDrawn, "Should be able to draw a Mythic card in 1000 tries")
    }

    @Test
    fun drawCard_canDrawEpic() {
        var epicDrawn = false
        repeat(500) {
            val card = gachaSystem.drawCard()
            if (card.rarity == Rarity.EPIC) epicDrawn = true
        }
        assertTrue(epicDrawn, "Should be able to draw an Epic card in 500 tries")
    }

    @Test
    fun drawCard_canDrawRare() {
        var rareDrawn = false
        repeat(200) {
            val card = gachaSystem.drawCard()
            if (card.rarity == Rarity.RARE) rareDrawn = true
        }
        assertTrue(rareDrawn, "Should be able to draw a Rare card in 200 tries")
    }

    @Test
    fun drawCard_canDrawCommon() {
        var commonDrawn = false
        repeat(100) {
            val card = gachaSystem.drawCard()
            if (card.rarity == Rarity.COMMON) commonDrawn = true
        }
        assertTrue(commonDrawn, "Should be able to draw a Common card in 100 tries")
    }

    @Test
    fun getAllCards_returnsCorrectSize() {
        assertEquals(testCards.size, gachaSystem.getAllCards().size)
    }

    @Test
    fun drawCard_rarityEmpty_returnsRandomCard() {
        val system = GachaSystem(listOf(LegendaryCard("1", "C", "D", Rarity.COMMON, "", "O")))
        val card = system.drawCard()
        assertEquals(Rarity.COMMON, card.rarity)
    }
}
