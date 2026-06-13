package com.example.Roomie.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class SupabaseServiceTest {

    private val mockSupabase = mockk<SupabaseClient>(relaxed = true)
    private lateinit var service: SupabaseServiceImpl

    @BeforeTest
    fun setup() {
        service = SupabaseServiceImpl(mockSupabase)
    }

    @Test
    fun `uploadReportImage - coverage catch block`() = runTest {
        // Triggering the catch block for coverage
        val result = service.uploadReportImage(byteArrayOf(1))
        assertNull(result)
    }

    @Test
    fun `uploadAvatar - coverage catch block`() = runTest {
        val result = service.uploadAvatar(byteArrayOf(1))
        assertNull(result)
    }
}
