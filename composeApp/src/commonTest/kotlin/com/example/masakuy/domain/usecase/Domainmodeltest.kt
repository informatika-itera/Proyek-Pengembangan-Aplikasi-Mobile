package com.example.masakuy.domain.usecase

import com.example.masakuy.domain.model.Ingredient
import com.example.masakuy.domain.model.Recipe
import com.example.masakuy.domain.model.RecipeDetail
import com.example.masakuy.domain.model.User
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class DomainModelTest {

    @Test
    fun `Recipe equals hashCode dan copy berfungsi`() {
        val r1 = Recipe(id = "r1", name = "Nasi", image = "", estimatedCost = 1000, estimatedTime = 10, difficulty = "Mudah")
        val r2 = r1.copy()
        val r3 = r1.copy(name = "Mie")

        assertEquals(r1, r2)
        assertEquals(r1.hashCode(), r2.hashCode())
        assertNotEquals(r1, r3)
        assertTrue(r1.toString().contains("Nasi"))
        assertEquals(false, r1.isFavorite)
    }

    @Test
    fun `RecipeDetail equals hashCode dan copy berfungsi`() {
        val ing = Ingredient(name = "Telur", quantity = "2 butir", estimatedPrice = 3000)
        val d1 = RecipeDetail(
            id = "r1", name = "Nasi", image = "", estimatedCost = 1000, estimatedTime = 10,
            difficulty = "Mudah", ingredients = listOf(ing), instructions = listOf("step1")
        )
        val d2 = d1.copy()
        val d3 = d1.copy(isFavorite = true)

        assertEquals(d1, d2)
        assertEquals(d1.hashCode(), d2.hashCode())
        assertNotEquals(d1, d3)
        assertTrue(d1.toString().contains("Nasi"))
    }

    @Test
    fun `Ingredient equals hashCode dan copy berfungsi`() {
        val i1 = Ingredient(name = "Telur", quantity = "2 butir", estimatedPrice = 3000)
        val i2 = i1.copy()
        val i3 = i1.copy(estimatedPrice = 5000)

        assertEquals(i1, i2)
        assertEquals(i1.hashCode(), i2.hashCode())
        assertNotEquals(i1, i3)
        assertTrue(i1.toString().contains("Telur"))
        assertEquals("Telur", i1.component1())
        assertEquals("2 butir", i1.component2())
        assertEquals(3000, i1.component3())
    }

    @Test
    fun `User equals hashCode dan copy berfungsi`() {
        val u1 = User(id = "u1", email = "a@a.com", name = "Budi", token = "abc123")
        val u2 = u1.copy()
        val u3 = u1.copy(name = "Andi")

        assertEquals(u1, u2)
        assertEquals(u1.hashCode(), u2.hashCode())
        assertNotEquals(u1, u3)
        assertTrue(u1.toString().contains("Budi"))
        assertEquals("u1", u1.component1())
        assertEquals("a@a.com", u1.component2())
    }

    @Test
    fun `Recipe component functions mengembalikan nilai yang benar`() {
        val r = Recipe(id = "r1", name = "Nasi", image = "img", estimatedCost = 1000, estimatedTime = 10, difficulty = "Mudah", isFavorite = true)

        assertEquals("r1", r.component1())
        assertEquals("Nasi", r.component2())
        assertEquals("img", r.component3())
        assertEquals(1000, r.component4())
        assertEquals(10, r.component5())
        assertEquals("Mudah", r.component6())
        assertEquals(true, r.component7())
    }
}