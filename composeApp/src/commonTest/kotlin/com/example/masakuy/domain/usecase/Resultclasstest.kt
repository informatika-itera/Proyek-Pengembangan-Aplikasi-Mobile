package com.example.masakuy.domain.usecase

import com.example.masakuy.core.network.Result
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ResultClassTest {

    @Test
    fun `Result Success equals dan hashCode konsisten`() {
        val s1 = Result.Success("data")
        val s2 = Result.Success("data")
        val s3 = Result.Success("lain")

        assertEquals(s1, s2)
        assertEquals(s1.hashCode(), s2.hashCode())
        assertNotEquals(s1, s3)
        assertTrue(s1.toString().contains("data"))
    }

    @Test
    fun `Result Success copy berfungsi`() {
        val s1 = Result.Success("data")
        val s2 = s1.copy(data = "baru")

        assertEquals("baru", s2.data)
        assertEquals("data", s1.component1())
    }

    @Test
    fun `Result Error equals dan hashCode konsisten`() {
        val e1 = Result.Error(Exception("err"))
        val e2 = Result.Error(e1.exception)

        assertEquals(e1, e2)
        assertEquals(e1.hashCode(), e2.hashCode())
        assertTrue(e1.toString().contains("Error"))
        assertEquals(e1.exception, e1.component1())
    }

    @Test
    fun `Result Error copy berfungsi`() {
        val original = Exception("err1")
        val replacement = Exception("err2")
        val e1 = Result.Error(original)
        val e2 = e1.copy(exception = replacement)

        assertEquals(replacement, e2.exception)
    }

    @Test
    fun `Result Loading toString dan equals`() {
        val l1 = Result.Loading
        val l2 = Result.Loading

        assertEquals(l1, l2)
        assertTrue(l1.toString().contains("Loading"))
        assertEquals(l1.hashCode(), l2.hashCode())
    }
}