package com.example.masakuy.domain.usecase

import com.example.masakuy.core.util.calculateEstimatedTime
import com.example.masakuy.core.util.formatCurrency
import com.example.masakuy.core.util.handleError
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ExtensionsTest {

    @Test
    fun `formatCurrency memformat angka dengan benar`() = runTest {
        val result = formatCurrency(15000)
        assertTrue(result.contains("15"))
        assertTrue(result.startsWith("Rp"))
    }

    @Test
    fun `formatCurrency angka nol`() = runTest {
        val result = formatCurrency(0)
        assertTrue(result.startsWith("Rp"))
    }

    @Test
    fun `formatCurrency angka besar`() = runTest {
        val result = formatCurrency(1000000)
        assertTrue(result.startsWith("Rp"))
        assertTrue(result.contains("1"))
    }

    @Test
    fun `calculateEstimatedTime menghitung waktu dengan kompleksitas`() = runTest {
        val result = calculateEstimatedTime(baseTime = 20, complexity = 2)
        assertEquals(30, result)
    }

    @Test
    fun `calculateEstimatedTime dengan kompleksitas nol`() = runTest {
        val result = calculateEstimatedTime(baseTime = 15, complexity = 0)
        assertEquals(15, result)
    }

    @Test
    fun `calculateEstimatedTime dengan kompleksitas besar`() = runTest {
        val result = calculateEstimatedTime(baseTime = 10, complexity = 5)
        assertEquals(35, result)
    }

    @Test
    fun `handleError memanggil callback saat flow error`() = runTest {
        var capturedError: Throwable? = null
        val flow = flow<Int> {
            throw RuntimeException("Test error")
        }.handleError { capturedError = it }

        flow.toList()

        assertEquals("Test error", capturedError?.message)
    }

    @Test
    fun `handleError tidak memanggil callback jika flow sukses`() = runTest {
        var capturedError: Throwable? = null
        val flow = flow {
            emit(1)
            emit(2)
        }.handleError { capturedError = it }

        val results = flow.toList()

        assertEquals(listOf(1, 2), results)
        assertEquals(null, capturedError)
    }
}