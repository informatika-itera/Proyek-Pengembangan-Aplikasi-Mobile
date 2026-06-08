package com.example.nutriscan.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class ProductModelTest {

    @Test
    fun `displayName mengembalikan nama produk jika tidak kosong`() {
        val product = Product(
            barcode = "123",
            name = "Aqua"
        )

        assertEquals("Aqua", product.displayName)
    }

    @Test
    fun `displayName mengembalikan fallback jika nama kosong`() {
        val product = Product(
            barcode = "123",
            name = ""
        )

        assertEquals("Produk Tidak Dikenal", product.displayName)
    }

    @Test
    fun `nutrimentsPerServing menghitung nutrisi sesuai serving size`() {
        val product = Product(
            barcode = "123",
            name = "Snack",
            servingSize = 50f,
            nutriments = Nutriments(
                calories = 200f,
                fat = 10f,
                saturatedFat = 4f,
                sugar = 20f,
                sodium = 500f,
                protein = 6f,
                carbs = 30f
            )
        )

        val serving = product.nutrimentsPerServing

        assertEquals(100f, serving.calories)
        assertEquals(5f, serving.fat)
        assertEquals(2f, serving.saturatedFat)
        assertEquals(10f, serving.sugar)
        assertEquals(250f, serving.sodium)
        assertEquals(3f, serving.protein)
        assertEquals(15f, serving.carbs)
    }
}