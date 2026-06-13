package com.example.neurodeck.core.util

import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit test untuk extension function di [Extensions.kt].
 *
 * Catatan TZ: formatToDisplay / formatDateOnly / formatTimeOnly bergantung pada
 * timezone sistem yang menjalankan test, jadi kita TIDAK assert nilai harfiah.
 * Cukup verifikasi STRUKTUR output (mengandung "/" untuk tanggal, ":" untuk jam).
 * Fungsi murni lain (truncate, capitalizeFirst, Result helpers) di-test penuh.
 */
class ExtensionsTest {

    // ==================== String.truncate ====================

    @Test
    fun `truncate string lebih panjang dari maxLength menambah ellipsis`() {
        val result = "Pemrograman Mobile".truncate(10)
        assertEquals("Pemrogr...", result)
        assertEquals(10, result.length, "Hasil truncate tepat maxLength karakter")
    }

    @Test
    fun `truncate string lebih pendek dikembalikan apa adanya`() {
        assertEquals("Halo", "Halo".truncate(10))
    }

    @Test
    fun `truncate string tepat maxLength tidak berubah`() {
        assertEquals("1234567890", "1234567890".truncate(10))
    }

    // ==================== String.capitalizeFirst ====================

    @Test
    fun `capitalizeFirst mengubah huruf pertama jadi kapital`() {
        assertEquals("Halo", "halo".capitalizeFirst())
        assertEquals("Mobile", "mobile".capitalizeFirst())
    }

    @Test
    fun `capitalizeFirst string kosong tetap kosong`() {
        assertEquals("", "".capitalizeFirst())
    }

    @Test
    fun `capitalizeFirst tidak mengubah huruf yang sudah kapital`() {
        assertEquals("Android", "Android".capitalizeFirst())
    }

    // ==================== Result.mapSuccess ====================

    @Test
    fun `mapSuccess mentransformasi nilai success`() {
        val result: Result<Int> = Result.success(21)
        val mapped = result.mapSuccess { it * 2 }
        assertTrue(mapped.isSuccess)
        assertEquals(42, mapped.getOrNull())
    }

    @Test
    fun `mapSuccess mempertahankan failure tanpa memanggil transform`() {
        var transformCalled = false
        val result: Result<Int> = Result.failure(RuntimeException("boom"))
        val mapped = result.mapSuccess { transformCalled = true; it * 2 }
        assertTrue(mapped.isFailure)
        assertFalse(transformCalled, "transform tidak boleh dipanggil untuk failure")
    }

    // ==================== Result.handle ====================

    @Test
    fun `handle memanggil onSuccess untuk hasil sukses`() {
        var successValue: String? = null
        var failureCalled = false
        Result.success("data").handle(
            onSuccess = { successValue = it },
            onFailure = { failureCalled = true },
        )
        assertEquals("data", successValue)
        assertFalse(failureCalled)
    }

    @Test
    fun `handle memanggil onFailure untuk hasil gagal`() {
        var successCalled = false
        var caught: Throwable? = null
        Result.failure<String>(IllegalStateException("gagal")).handle(
            onSuccess = { successCalled = true },
            onFailure = { caught = it },
        )
        assertFalse(successCalled)
        assertTrue(caught is IllegalStateException)
    }

    // ==================== retryWithBackoff ====================

    @Test
    fun `retryWithBackoff sukses di percobaan pertama tidak retry`() = runTest {
        var attempts = 0
        val result = retryWithBackoff(times = 3, initialDelay = 1) {
            attempts++
            "ok"
        }
        assertEquals("ok", result)
        assertEquals(1, attempts)
    }

    @Test
    fun `retryWithBackoff mencoba ulang sampai sukses`() = runTest {
        var attempts = 0
        val result = retryWithBackoff(times = 3, initialDelay = 1) {
            attempts++
            if (attempts < 2) throw RuntimeException("belum")
            "akhirnya sukses"
        }
        assertEquals("akhirnya sukses", result)
        assertEquals(2, attempts)
    }

    // ==================== Instant formatters (structural only) ====================

    @Test
    fun `formatDateOnly mengandung pemisah tanggal`() {
        val instant = Instant.parse("2026-05-19T10:30:00Z")
        assertTrue(instant.formatDateOnly().contains("/"), "Format tanggal pakai '/'")
    }

    @Test
    fun `formatTimeOnly mengandung pemisah jam`() {
        val instant = Instant.parse("2026-05-19T10:30:00Z")
        assertTrue(instant.formatTimeOnly().contains(":"), "Format jam pakai ':'")
    }

    @Test
    fun `formatToDisplay mengandung tanggal dan jam`() {
        val instant = Instant.parse("2026-05-19T10:30:00Z")
        val formatted = instant.formatToDisplay()
        assertTrue(formatted.contains("/"))
        assertTrue(formatted.contains(":"))
    }
}
