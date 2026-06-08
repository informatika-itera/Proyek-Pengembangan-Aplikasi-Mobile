package com.example.nutriscan.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class NutritionistTest {

    @Test
    fun `initials mengambil dua huruf dari nama tanpa Dr`() {
        val nutritionist = Nutritionist(
            id = "1",
            name = "Dr. Sinta Wijaya",
            specialty = "Gizi Klinik",
            bio = "Bio",
            experienceYears = 5,
            rating = 4.8,
            reviewCount = 100,
            pricePerChat = 30
        )

        assertEquals("SW", nutritionist.initials)
    }

    @Test
    fun `initials mengambil dua huruf dari satu kata`() {
        val nutritionist = Nutritionist(
            id = "1",
            name = "Maya",
            specialty = "Gizi Klinik",
            bio = "Bio",
            experienceYears = 5,
            rating = 4.8,
            reviewCount = 100,
            pricePerChat = 30
        )

        assertEquals("MA", nutritionist.initials)
    }

    @Test
    fun `initials fallback tanda tanya jika nama kosong`() {
        val nutritionist = Nutritionist(
            id = "1",
            name = "",
            specialty = "Gizi Klinik",
            bio = "Bio",
            experienceYears = 5,
            rating = 4.8,
            reviewCount = 100,
            pricePerChat = 30
        )

        assertEquals("?", nutritionist.initials)
    }
}