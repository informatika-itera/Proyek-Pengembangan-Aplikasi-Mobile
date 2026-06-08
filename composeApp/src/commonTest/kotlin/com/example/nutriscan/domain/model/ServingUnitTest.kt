package com.example.nutriscan.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class ServingUnitTest {

    @Test
    fun `GRAM mengembalikan 1 gram per unit`() {
        assertEquals(1f, ServingUnit.GRAM.gramsPerUnit(servingSize = 100f))
    }

    @Test
    fun `SENDOK_TEH mengembalikan 5 gram per unit`() {
        assertEquals(5f, ServingUnit.SENDOK_TEH.gramsPerUnit(servingSize = 100f))
    }

    @Test
    fun `SENDOK_MAKAN mengembalikan 15 gram per unit`() {
        assertEquals(15f, ServingUnit.SENDOK_MAKAN.gramsPerUnit(servingSize = 100f))
    }

    @Test
    fun `GELAS mengembalikan 240 gram per unit`() {
        assertEquals(240f, ServingUnit.GELAS.gramsPerUnit(servingSize = 100f))
    }

    @Test
    fun `KEMASAN mengikuti serving size produk`() {
        assertEquals(125f, ServingUnit.KEMASAN.gramsPerUnit(servingSize = 125f))
    }

    @Test
    fun `SETENGAH_PORSI mengembalikan setengah serving size produk`() {
        assertEquals(50f, ServingUnit.SETENGAH_PORSI.gramsPerUnit(servingSize = 100f))
    }
}