package com.example.mapenumkm.domain.model

import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ModelTest {

    @Test
    fun `Note model instantiation`() {
        val now = Clock.System.now()
        val note = Note(
            id = 1,
            title = "Test",
            content = "Content",
            createdAt = now,
            updatedAt = now
        )
        assertEquals(1L, note.id)
        assertEquals("Test", note.title)
    }

    @Test
    fun `Transaction model instantiation`() {
        val now = Clock.System.now()
        val transaction = Transaction(
            id = 1,
            items = emptyList(),
            subtotal = 100.0,
            total = 100.0,
            paymentAmount = 100.0,
            changeAmount = 0.0,
            createdAt = now
        )
        assertEquals(1L, transaction.id)
        assertNotNull(transaction.items)
    }

    @Test
    fun `User model instantiation`() {
        val user = User(
            id = 1,
            name = "user",
            email = "user@test.com",
            phone = "123",
            password = "pass",
            createdAt = 123456789L
        )
        assertEquals(1L, user.id)
        assertEquals("user", user.name)
    }
}
