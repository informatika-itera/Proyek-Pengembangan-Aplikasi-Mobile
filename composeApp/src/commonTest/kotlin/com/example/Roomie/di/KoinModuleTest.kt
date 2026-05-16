package com.example.Roomie.di

import com.example.Roomie.domain.model.Building
import com.example.Roomie.domain.model.Report
import com.example.Roomie.domain.model.ReportStatus
import com.example.Roomie.domain.model.Room
import com.example.Roomie.domain.repository.FacilityRepository
import com.example.Roomie.domain.repository.ReportRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
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
            single<ReportRepository> { 
                object : ReportRepository {
                    override fun getAllReports(): Flow<List<Report>> = flowOf(emptyList())
                    override suspend fun updateReportStatus(reportId: String, status: ReportStatus) {}
                    override suspend fun submitReport(report: Report) {}
                }
            }
            single<FacilityRepository> { 
                object : FacilityRepository {
                    override fun getBuildings(): Flow<List<Building>> = flowOf(emptyList())
                    override fun getRoomsByFloor(buildingId: String, floor: Int): Flow<List<Room>> = flowOf(emptyList())
                    override fun getRoomById(roomId: String): Flow<Room?> = flowOf(null)
                }
            }
        }

        checkModules {
            modules(mockDataModule, domainModule, viewModelModule)
        }
    }
}
