package com.kosthub.app.data.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kosthub.app.data.local.DatabaseDriverFactory
import com.kosthub.app.data.local.KostDatabase
import com.kosthub.app.domain.model.Kost
import com.kosthub.app.platform.PlatformContext
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
class KostRepositoryTest {

    private lateinit var database: KostDatabase
    private lateinit var repository: KostRepositoryImpl
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        context.deleteDatabase("kosthub.db")
        
        val driver = DatabaseDriverFactory(PlatformContext(context)).createDriver()
        database = KostDatabase(driver)
        repository = KostRepositoryImpl(database)
    }

    @Test
    fun testInsertAndGetKostById() = runBlocking {
        val testKost = Kost(
            id = 999L,
            namaKos = "Kost Test",
            nomorTelepon = "08999",
            jarakKm = 0.5,
            hargaTahunan = 4500000L,
            tipeKos = "Campur",
            kamarMandi = "Dalam",
            wifi = "Ada",
            furniturKasur = "Ada",
            furniturLemari = "Ada",
            furniturMejaBelajar = "Ada",
            fasilitasPendingin = "Ada",
            areaLaundry = "Tidak",
            areaDapur = "Ada",
            keamananCctv = "Ada",
            isFavorite = false
        )
        
        assertNull(repository.getById(999L))
        
        database.kostQueries.insertKost(
            id = testKost.id,
            namaKos = testKost.namaKos,
            nomorTelepon = testKost.nomorTelepon,
            jarakKm = testKost.jarakKm,
            hargaTahunan = testKost.hargaTahunan,
            tipeKos = testKost.tipeKos,
            kamarMandi = testKost.kamarMandi,
            wifi = testKost.wifi,
            furniturKasur = testKost.furniturKasur,
            furniturLemari = testKost.furniturLemari,
            furniturMejaBelajar = testKost.furniturMejaBelajar,
            fasilitasPendingin = testKost.fasilitasPendingin,
            areaLaundry = testKost.areaLaundry,
            areaDapur = testKost.areaDapur,
            keamananCctv = testKost.keamananCctv
        )
        
        val retrieved = repository.getById(999L)
        assertNotNull(retrieved)
        assertEquals("Kost Test", retrieved.namaKos)
        assertEquals(0.5, retrieved.jarakKm)
        assertEquals(4500000L, retrieved.hargaTahunan)
    }

    @Test
    fun testUpdateFavoriteStatus() = runBlocking {
        val testKost = Kost(
            id = 999L,
            namaKos = "Kost Test",
            nomorTelepon = "08999",
            jarakKm = 0.5,
            hargaTahunan = 4500000L,
            tipeKos = "Campur",
            kamarMandi = "Dalam",
            wifi = "Ada",
            furniturKasur = "Ada",
            furniturLemari = "Ada",
            furniturMejaBelajar = "Ada",
            fasilitasPendingin = "Ada",
            areaLaundry = "Tidak",
            areaDapur = "Ada",
            keamananCctv = "Ada",
            isFavorite = false
        )
        
        database.kostQueries.insertKost(
            id = testKost.id,
            namaKos = testKost.namaKos,
            nomorTelepon = testKost.nomorTelepon,
            jarakKm = testKost.jarakKm,
            hargaTahunan = testKost.hargaTahunan,
            tipeKos = testKost.tipeKos,
            kamarMandi = testKost.kamarMandi,
            wifi = testKost.wifi,
            furniturKasur = testKost.furniturKasur,
            furniturLemari = testKost.furniturLemari,
            furniturMejaBelajar = testKost.furniturMejaBelajar,
            fasilitasPendingin = testKost.fasilitasPendingin,
            areaLaundry = testKost.areaLaundry,
            areaDapur = testKost.areaDapur,
            keamananCctv = testKost.keamananCctv
        )
        
        val initial = repository.getById(999L)
        assertNotNull(initial)
        assertEquals(false, initial.isFavorite)
        
        repository.update(initial.copy(isFavorite = true))
        
        val afterUpdate = repository.getById(999L)
        assertNotNull(afterUpdate)
        assertEquals(true, afterUpdate.isFavorite)
    }
}
