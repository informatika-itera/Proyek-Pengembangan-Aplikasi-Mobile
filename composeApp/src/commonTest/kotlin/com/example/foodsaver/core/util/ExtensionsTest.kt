package com.example.foodsaver.core.util

import com.example.foodsaver.core.utility.formatQuantity
import kotlin.test.Test
import kotlin.test.assertEquals

class ExtensionsTest {

    @Test
    fun `formatQuantity should remove decimal when value is whole number`() {
        val quantity = 12.0
        val unit = "pcs"
        assertEquals("12 pcs", quantity.formatQuantity(unit))
    }

    @Test
    fun `formatQuantity should keep decimal when value is not whole number`() {
        val quantity = 12.5
        val unit = "kg"
        assertEquals("12.5 kg", quantity.formatQuantity(unit))
    }

    @Test
    fun `formatQuantity should handle zero correctly`() {
        val quantity = 0.0
        val unit = "gram"
        assertEquals("0 gram", quantity.formatQuantity(unit))
    }
}
