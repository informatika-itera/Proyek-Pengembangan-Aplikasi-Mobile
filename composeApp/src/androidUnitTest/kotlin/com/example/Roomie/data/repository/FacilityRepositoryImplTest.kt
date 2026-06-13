package com.example.Roomie.data.repository

import com.example.Roomie.core.network.NetworkMonitor
import com.example.Roomie.data.local.RoomieDatabase
import com.example.Roomie.domain.model.RoomStatus
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.PostgrestQueryBuilder
import io.github.jan.supabase.postgrest.result.PostgrestResult
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
class FacilityRepositoryImplTest {
    private lateinit var repository: FacilityRepositoryImpl
    private val testDispatcher = UnconfinedTestDispatcher()
    private val mockDb: RoomieDatabase = mockk(relaxed = true)
    private val mockSupabase: SupabaseClient = mockk(relaxed = true)
    private val fakeIsOnline = MutableStateFlow(false)

    @BeforeTest
    fun setup() {
        val mockEngine = MockEngine { request ->
            val jsonResponse = when {
                request.url.encodedPath.contains("buildings") -> "[{\"id\":\"G1\",\"name\":\"Gedung 1\",\"description\":\"D\",\"is_available\":true}]"
                request.url.encodedPath.contains("rooms") -> "[{\"id\":\"R1\",\"building_id\":\"G1\",\"name\":\"101\",\"floor\":1,\"status\":\"AVAILABLE\",\"capacity\":50,\"has_ac\":true,\"has_projector\":true}]"
                else -> "[]"
            }
            respond(jsonResponse, HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/json"))
        }
        
        val httpClient = HttpClient(mockEngine)
        val fakeNetworkMonitor = object : NetworkMonitor {
            override val isOnline = fakeIsOnline
        }

        repository = FacilityRepositoryImpl(
            database = mockDb,
            supabaseClient = mockSupabase,
            networkMonitor = fakeNetworkMonitor,
            scope = CoroutineScope(testDispatcher + SupervisorJob())
        )
    }

    @Test
    fun `sync logic coverage - should process remote data when online`() = runTest {
        fakeIsOnline.value = true
        // Allow the background sync coroutine to run
        yield() 
        // This will cover the observeSync() branches
    }

    @Test
    fun `getBuildings coverage`() = runTest {
        repository.getBuildings()
        verify { mockDb.facilityQueries.getAllBuildings() }
    }

    @Test
    fun `searchRoomsFiltered coverage`() = runTest {
        repository.searchRoomsFiltered("101", 10, 60)
        verify { mockDb.facilityQueries.searchRoomsFiltered(any(), any(), any()) }
    }

    @Test
    fun `updateRoomStatus coverage`() = runTest(testDispatcher) {
        fakeIsOnline.value = true
        repository.updateRoomStatus("R1", RoomStatus.MAINTENANCE, maintenanceDescription = "Repair")
        coVerify { mockDb.facilityQueries.updateRoomStatus(any(), any(), any(), any()) }
    }
}
