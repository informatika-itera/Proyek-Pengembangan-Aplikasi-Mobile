package com.example.nutriscan.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class UserRoleTest {

    @Test
    fun `fromString mengembalikan role USER`() {
        assertEquals(UserRole.USER, UserRole.fromString("USER"))
    }

    @Test
    fun `fromString mengembalikan role NUTRITIONIST`() {
        assertEquals(UserRole.NUTRITIONIST, UserRole.fromString("NUTRITIONIST"))
    }

    @Test
    fun `fromString fallback ke USER jika null atau salah`() {
        assertEquals(UserRole.USER, UserRole.fromString(null))
        assertEquals(UserRole.USER, UserRole.fromString("SALAH"))
    }
}