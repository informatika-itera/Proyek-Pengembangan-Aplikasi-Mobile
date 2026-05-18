package com.example.Roomie.di

import com.example.Roomie.data.local.datastore.UserPreferences
import com.example.Roomie.data.repository.*
import com.example.Roomie.domain.repository.*
import com.example.Roomie.data.remote.SupabaseService
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.check.checkModules
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(KoinExperimentalAPI::class, ExperimentalCoroutinesApi::class)
class KoinModuleTest : KoinTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun checkViewModelModule() {
        val mockDataModule = module {
            single<ReportRepository> { FakeReportRepository() }
            single<FacilityRepository> { FakeFacilityRepository() }
            single<AuthRepository> { FakeAuthRepository() }
            single<AnnouncementRepository> { FakeAnnouncementRepository() }
            single<BookingRepository> { 
                object : BookingRepository {
                    override fun getAllBookings() = flowOf(emptyList<com.example.Roomie.domain.model.Booking>())
                    override suspend fun addBooking(booking: com.example.Roomie.domain.model.Booking) = Result.success(Unit)
                    override suspend fun updateBookingStatus(id: String, status: com.example.Roomie.domain.model.BookingStatus) = Result.success(Unit)
                    override suspend fun deleteBooking(id: String) = Result.success(Unit)
                    override suspend fun checkConflict(roomId: String, startTime: Long, endTime: Long) = false
                }
            }
            single<NotificationRepository> {
                object : NotificationRepository {
                    override fun getAllNotifications() = flowOf(emptyList<com.example.Roomie.domain.model.Notification>())
                    override suspend fun addNotification(notification: com.example.Roomie.domain.model.Notification) {}
                    override suspend fun markAsRead(id: String) {}
                }
            }
            single<SupabaseClient> { 
                createSupabaseClient("https://fake.com", "fake") {}
            }
            single { SupabaseService(get()) }
            // Add UserPreferences mock
            single<UserPreferences> { 
                // We use a simple object because UserPreferences needs a DataStore
                // For Koin check, we just need the type to be available
                val mockDataStore = object : androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences> {
                    override val data = flowOf(androidx.datastore.preferences.core.emptyPreferences())
                    override suspend fun updateData(transform: suspend (androidx.datastore.preferences.core.Preferences) -> androidx.datastore.preferences.core.Preferences) = androidx.datastore.preferences.core.emptyPreferences()
                }
                UserPreferences(mockDataStore)
            }
        }

        checkModules {
            modules(mockDataModule, domainModule, viewModelModule)
        }
    }
}
