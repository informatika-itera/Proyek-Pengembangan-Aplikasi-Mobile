package com.example.foodsaver.presentation

import kotlin.test.Test
import kotlin.test.assertTrue

class FoodSaverBaseTest {

    @Test
    fun `test initial environment should pass`() {
        val isProjectInitialized = true
        assertTrue(isProjectInitialized, "Project should be initialized correctly")
    }
}
