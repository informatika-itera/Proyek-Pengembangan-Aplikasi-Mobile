package com.example.nutriscan.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UserProfileTest {

    private fun makeProfile(
        weight: Float = 70f,
        height: Float = 170f,
        age: Int = 30,
        conditions: List<Disease> = emptyList()
    ) = UserProfile(
        name = "Test",
        age = age,
        weight = weight,
        height = height,
        healthConditions = conditions
    )

    @Test
    fun `BMI dihitung dengan benar`() {
        // 70 / (1.70^2) = 70 / 2.89 ≈ 24.22
        val profile = makeProfile(weight = 70f, height = 170f)
        assertEquals(24.22f, profile.bmi, absoluteTolerance = 0.1f)
    }

    @Test
    fun `BMI kategori Normal untuk 18_5 sampai 25`() {
        val profile = makeProfile(weight = 70f, height = 170f)  // BMI ~24.2
        assertEquals("Normal", profile.bmiCategory)
    }

    @Test
    fun `BMI kategori Kurus untuk BMI di bawah 18_5`() {
        val profile = makeProfile(weight = 50f, height = 170f)  // BMI ~17.3
        assertEquals("Kurus", profile.bmiCategory)
    }

    @Test
    fun `BMI kategori Obesitas untuk BMI 30 ke atas`() {
        val profile = makeProfile(weight = 100f, height = 170f)  // BMI ~34.6
        assertEquals("Obesitas", profile.bmiCategory)
    }

    @Test
    fun `hasDiabetes true jika kondisi mengandung DIABETES`() {
        val profile = makeProfile(conditions = listOf(Disease.DIABETES))
        assertTrue(profile.hasDiabetes)
    }

    @Test
    fun `hasDiabetes false jika tidak ada DIABETES`() {
        val profile = makeProfile(conditions = listOf(Disease.HYPERTENSION))
        assertFalse(profile.hasDiabetes)
    }

    @Test
    fun `hasObesity true jika BMI lebih dari atau sama dengan 30`() {
        val profile = makeProfile(weight = 100f, height = 170f)  // BMI ~34.6
        assertTrue(profile.hasObesity)
    }

    @Test
    fun `hasObesity true jika kondisi mengandung OBESITY`() {
        val profile = makeProfile(
            weight = 60f,  // BMI normal
            height = 170f,
            conditions = listOf(Disease.OBESITY)
        )
        assertTrue(profile.hasObesity)
    }

    @Test
    fun `dailyCalorieNeed lebih besar untuk berat badan lebih tinggi`() {
        val lighter = makeProfile(weight = 60f)
        val heavier = makeProfile(weight = 90f)
        assertTrue(heavier.dailyCalorieNeed > lighter.dailyCalorieNeed)
    }

    @Test
    fun `Disease fromCsv dan toCsv harus simetris`() {
        val diseases = listOf(Disease.DIABETES, Disease.HYPERTENSION)
        val csv      = Disease.toCsv(diseases)
        val parsed   = Disease.fromCsv(csv)
        assertEquals(diseases, parsed)
    }

    @Test
    fun `Disease fromCsv string kosong harus return list kosong`() {
        val result = Disease.fromCsv("")
        assertTrue(result.isEmpty())
    }
}