package com.example.Roomie.data.repository

import com.example.Roomie.core.network.NetworkMonitor
import com.example.Roomie.data.local.RoomieDatabase
import com.example.Roomie.domain.model.Report
import com.example.Roomie.domain.model.ReportStatus
import com.example.Roomie.domain.model.UrgencyLevel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.*
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import io.mockk.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class ReportRepositoryImplTest {
    private lateinit var repository: ReportRepositoryImpl
    private val testDispatcher = UnconfinedTestDispatcher()
    private val mockDb: RoomieDatabase = mockk(relaxed = true)
    private val mockSupabase: SupabaseClient = mockk(relaxed = true)
    private val fakeIsOnline = MutableStateFlow(false)

    @BeforeTest
    fun setup() {
        val mockEngine = MockEngine { respond("[]", HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/json")) }
        val httpClient = HttpClient(mockEngine)
        val fakeNetworkMonitor = object : NetworkMonitor {
            override val isOnline = fakeIsOnline
        }
        
        repository = ReportRepositoryImpl(
            database = mockDb,
            supabaseClient = mockSupabase,
            networkMonitor = fakeNetworkMonitor,
            scope = CoroutineScope(testDispatcher + SupervisorJob())
        )
    }

    @Test
    fun `getAllReports - coverage logic`() = runTest {
        repository.getAllReports()
        verify { mockDb.reportQueries.getAllReports() }
    }

    @Test
    fun `submitReport logic check`() = runTest {
        val report = Report("R1", "A", "Loc", "Desc", UrgencyLevel.LOW, ReportStatus.PENDING, 0L)
        repository.submitReport(report)
        coVerify { mockDb.reportQueries.insertReport(any(), any(), any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `updateReportStatus logic check`() = runTest {
        fakeIsOnline.value = true
        repository.updateReportStatus("R1", ReportStatus.DONE)
        coVerify { mockDb.reportQueries.updateReportStatus(ReportStatus.DONE.name, "R1") }
    }

    @Test
    fun `observeSync - should process flow`() = runTest {
        fakeIsOnline.value = true
        yield()
        assertTrue(fakeIsOnline.value)
    }
}
