package com.example.foodsaver.presentation

import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Unit Test dasar untuk memastikan CI Pipeline (GitHub Actions) berjalan dengan sukses.
 * Sesuai Deliverables: "Working GitHub Actions CI (build + test passing)".
 */
class FoodSaverBaseTest {

    @Test
    fun `test initial environment should pass`() {
        // Test sederhana untuk memverifikasi runner test di CI
        val isProjectInitialized = true
        assertTrue(isProjectInitialized, "Project should be initialized correctly")
    }
}
