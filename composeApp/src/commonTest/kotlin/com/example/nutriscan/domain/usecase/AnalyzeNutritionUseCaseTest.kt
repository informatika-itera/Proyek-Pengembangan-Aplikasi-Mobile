package com.example.nutriscan.domain.usecase

import com.example.nutriscan.domain.model.Disease
import com.example.nutriscan.domain.model.Nutriments
import com.example.nutriscan.domain.model.NutritionStatus
import com.example.nutriscan.domain.model.Product
import com.example.nutriscan.domain.model.UserProfile
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AnalyzeNutritionUseCaseTest {

    private val useCase = AnalyzeNutritionUseCase()

    // Helper: profil pengguna normal (tidak ada penyakit)
    private fun normalProfile(
        age: Int = 25,
        weight: Float = 65f,
        height: Float = 170f,
        conditions: List<Disease> = emptyList()
    ) = UserProfile(
        name = "Test User",
        age = age,
        weight = weight,
        height = height,
        healthConditions = conditions
    )

    // Helper: produk dengan nutrisi per 100g, serving default 100g
    private fun productWith(
        sugar: Float = 0f,
        sodium: Float = 0f,
        saturatedFat: Float = 0f,
        calories: Float = 0f,
        protein: Float = 0f,
        fat: Float = 0f,
        carbs: Float = 0f,
        servingSize: Float = 100f
    ) = Product(
        barcode = "000000000000",
        name = "Test Product",
        nutriments = Nutriments(
            calories = calories,
            fat = fat,
            saturatedFat = saturatedFat,
            sugar = sugar,
            sodium = sodium,
            protein = protein,
            carbs = carbs
        ),
        servingSize = servingSize
    )

    // ─── Gula ────────────────────────────────────────────────────────────────

    @Test
    fun `gula rendah harus SAFE untuk pengguna normal`() {
        val product = productWith(sugar = 2f)   // ~8% AKG dari 25g/hari
        val result  = useCase(product, normalProfile())
        val sugarWarning = result.warnings.find { it.nutrientName == "Gula" }
        // Bisa null (tidak masuk warnings) atau SAFE
        assertTrue(
            sugarWarning == null || sugarWarning.status == NutritionStatus.SAFE,
            "Gula rendah harus SAFE"
        )
    }

    @Test
    fun `gula di atas 35 persen AKG harus AVOID untuk pengguna normal`() {
        // AKG gula = 25g/hari; 35% = 8.75g per sajian
        val product = productWith(sugar = 10f, servingSize = 100f)
        val result  = useCase(product, normalProfile())
        val sugarWarning = result.warnings.find { it.nutrientName == "Gula" }
        assertEquals(
            NutritionStatus.AVOID,
            sugarWarning?.status,
            "Gula 10g (40% AKG) harus AVOID untuk pengguna normal"
        )
    }

    @Test
    fun `threshold gula harus setengahnya untuk penderita diabetes`() {
        // AKG gula diabetes = 12.5g/hari; 20% threshold = 2.5g
        // Produk dengan 3g gula → 24% AKG diabetes → CAUTION
        val product = productWith(sugar = 3f)
        val profile = normalProfile(conditions = listOf(Disease.DIABETES))
        val result  = useCase(product, profile)
        val sugarWarning = result.warnings.find { it.nutrientName == "Gula" }
        assertEquals(
            NutritionStatus.CAUTION,
            sugarWarning?.status,
            "3g gula harus CAUTION untuk penderita diabetes"
        )
    }

    @Test
    fun `gula 10g harus AVOID untuk penderita diabetes`() {
        val product = productWith(sugar = 10f)
        val profile = normalProfile(conditions = listOf(Disease.DIABETES))
        val result  = useCase(product, profile)
        assertEquals(
            NutritionStatus.AVOID,
            result.overallStatus,
            "Status keseluruhan harus AVOID jika ada nutrisi AVOID"
        )
    }

    // ─── Natrium ─────────────────────────────────────────────────────────────

    @Test
    fun `natrium di atas 35 persen AKG harus AVOID untuk pengguna normal`() {
        // AKG natrium = 2000mg/hari; 35% = 700mg per sajian
        val product = productWith(sodium = 800f)
        val result  = useCase(product, normalProfile())
        val sodiumWarning = result.warnings.find { it.nutrientName == "Natrium" }
        assertEquals(
            NutritionStatus.AVOID,
            sodiumWarning?.status,
            "800mg natrium (40% AKG) harus AVOID untuk pengguna normal"
        )
    }

    @Test
    fun `threshold natrium harus setengahnya untuk penderita hipertensi`() {
        // AKG natrium hipertensi = 1000mg/hari; 20% threshold = 200mg
        // 250mg → 25% AKG → CAUTION
        val product = productWith(sodium = 250f)
        val profile = normalProfile(conditions = listOf(Disease.HYPERTENSION))
        val result  = useCase(product, profile)
        val sodiumWarning = result.warnings.find { it.nutrientName == "Natrium" }
        assertEquals(
            NutritionStatus.CAUTION,
            sodiumWarning?.status,
            "250mg natrium harus CAUTION untuk penderita hipertensi"
        )
    }

    // ─── Overall Status ───────────────────────────────────────────────────────

    @Test
    fun `produk tanpa nutrisi bermasalah harus SAFE keseluruhan`() {
        val product = productWith(
            sugar = 1f, sodium = 10f, saturatedFat = 0.5f,
            calories = 50f, protein = 2f, fat = 1f
        )
        val result = useCase(product, normalProfile())
        assertEquals(
            NutritionStatus.SAFE,
            result.overallStatus,
            "Produk dengan semua nutrisi rendah harus SAFE"
        )
    }

    @Test
    fun `overall status AVOID jika ada satu nutrisi AVOID`() {
        val product = productWith(sugar = 10f, sodium = 10f)  // gula AVOID, natrium SAFE
        val result  = useCase(product, normalProfile())
        assertEquals(NutritionStatus.AVOID, result.overallStatus)
    }

    @Test
    fun `overall status CAUTION jika tidak ada AVOID tapi ada CAUTION`() {
        // Gula 6g → 24% AKG (antara 20-35%) = CAUTION
        val product = productWith(sugar = 6f)
        val result  = useCase(product, normalProfile())
        assertEquals(NutritionStatus.CAUTION, result.overallStatus)
    }

    // ─── Serving Size ─────────────────────────────────────────────────────────

    @Test
    fun `analisis harus berdasarkan serving size bukan per 100g`() {
        // 20g gula per 100g, serving 50g → 10g per sajian
        // 10g / 25g AKG = 40% → AVOID
        val product = productWith(sugar = 20f, servingSize = 50f)
        val result  = useCase(product, normalProfile())
        val sugarWarning = result.warnings.find { it.nutrientName == "Gula" }
        assertEquals(
            NutritionStatus.AVOID,
            sugarWarning?.status,
            "Analisis harus pakai serving size 50g, bukan 100g"
        )
    }

    @Test
    fun `serving size kecil harus menghasilkan status lebih baik`() {
        // 20g gula per 100g, serving 10g → 2g per sajian → SAFE
        val product = productWith(sugar = 20f, servingSize = 10f)
        val result  = useCase(product, normalProfile())
        val sugarWarning = result.warnings.find { it.nutrientName == "Gula" }
        assertTrue(
            sugarWarning == null || sugarWarning.status == NutritionStatus.SAFE,
            "Dengan serving kecil (10g), gula harus SAFE"
        )
    }

    // ─── Warning Messages ─────────────────────────────────────────────────────

    @Test
    fun `warnings hanya berisi nutrisi yang tidak SAFE`() {
        val product = productWith(sugar = 10f, sodium = 5f)  // gula AVOID, natrium SAFE
        val result  = useCase(product, normalProfile())
        assertTrue(
            result.warnings.none { it.status == NutritionStatus.SAFE },
            "warnings tidak boleh berisi nutrisi SAFE"
        )
    }
}