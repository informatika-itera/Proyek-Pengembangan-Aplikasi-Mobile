package com.example.nutriscan.data.repository

import app.cash.turbine.test
import com.example.nutriscan.domain.model.Nutriments
import com.example.nutriscan.domain.model.NutritionAnalysis
import com.example.nutriscan.domain.model.NutritionStatus
import com.example.nutriscan.domain.model.Product
import com.example.nutriscan.domain.model.ScanResult
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ScanHistoryRepositoryTest {

    private lateinit var repository: FakeScanHistoryRepository

    private fun makeScan(
        barcode: String = "1234567890",
        productName: String = "Test Product"
    ) = ScanResult(
        product = Product(
            barcode    = barcode,
            name       = productName,
            nutriments = Nutriments()
        ),
        analysis = NutritionAnalysis(overallStatus = NutritionStatus.SAFE)
    )

    @BeforeTest
    fun setup() {
        repository = FakeScanHistoryRepository()
    }

    @Test
    fun `getAllHistory awalnya kosong`() = runTest {
        repository.getAllHistory().test {
            assertTrue(awaitItem().isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveScan menambahkan item ke history`() = runTest {
        repository.saveScan(makeScan(productName = "Aqua"))

        repository.getAllHistory().test {
            val list = awaitItem()
            assertEquals(1, list.size)
            assertEquals("Aqua", list.first().product.name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveScan dua kali harus ada dua item`() = runTest {
        repository.saveScan(makeScan(barcode = "111", productName = "Produk A"))
        repository.saveScan(makeScan(barcode = "222", productName = "Produk B"))

        repository.getAllHistory().test {
            assertEquals(2, awaitItem().size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getScanById mengembalikan scan yang benar`() = runTest {
        repository.saveScan(makeScan(productName = "Teh Botol"))
        val savedId = repository.currentScans().first().id

        val result = repository.getScanById(savedId)
        assertNotNull(result)
        assertEquals("Teh Botol", result.product.name)
    }

    @Test
    fun `getScanById mengembalikan null jika tidak ada`() = runTest {
        val result = repository.getScanById(999L)
        assertNull(result)
    }

    @Test
    fun `deleteScan menghapus item dari history`() = runTest {
        repository.saveScan(makeScan(productName = "Indomie"))
        val savedId = repository.currentScans().first().id

        repository.deleteScan(savedId)

        assertNull(repository.getScanById(savedId))
    }

    @Test
    fun `clearHistory menghapus semua item`() = runTest {
        repository.saveScan(makeScan(barcode = "111"))
        repository.saveScan(makeScan(barcode = "222"))

        repository.clearHistory()

        repository.getAllHistory().test {
            assertTrue(awaitItem().isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getRecentHistory membatasi jumlah item`() = runTest {
        repeat(5) { i ->
            repository.saveScan(makeScan(barcode = "barcode_$i", productName = "Produk $i"))
        }

        repository.getRecentHistory(limit = 3L).test {
            assertEquals(3, awaitItem().size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getAllHistory emit ulang saat data berubah`() = runTest {
        repository.getAllHistory().test {
            assertEquals(0, awaitItem().size)   // initial

            repository.saveScan(makeScan())
            assertEquals(1, awaitItem().size)   // after save

            cancelAndIgnoreRemainingEvents()
        }
    }
}