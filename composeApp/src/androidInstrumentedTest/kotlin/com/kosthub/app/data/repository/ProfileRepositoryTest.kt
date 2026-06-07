package com.kosthub.app.data.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kosthub.app.data.local.DatabaseDriverFactory
import com.kosthub.app.data.local.KostDatabase
import com.kosthub.app.domain.model.Profile
import com.kosthub.app.platform.PlatformContext
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@RunWith(AndroidJUnit4::class)
class ProfileRepositoryTest {

    private lateinit var database: KostDatabase
    private lateinit var repository: ProfileRepositoryImpl
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        context.deleteDatabase("kosthub.db")
        val driver = DatabaseDriverFactory(PlatformContext(context)).createDriver()
        database = KostDatabase(driver)
        repository = ProfileRepositoryImpl(database)
    }

    @Test
    fun testGetProfileCreatesDefault() = runBlocking {
        val profile = repository.getProfile()
        assertNotNull(profile)
        assertEquals(1L, profile!!.id)
        assertEquals("Anonim", profile.name)
        assertEquals("anonim@kosthub.com", profile.email)
    }

    @Test
    fun testSaveAndGetProfile() = runBlocking {
        val testProfile = Profile(
            id = 1L,
            name = "John Doe",
            email = "john@example.com",
            latitude = -6.2,
            longitude = 106.8
        )
        
        repository.saveProfile(testProfile)
        
        val retrieved = repository.getProfile()
        assertNotNull(retrieved)
        assertEquals("John Doe", retrieved!!.name)
        assertEquals("john@example.com", retrieved.email)
        assertEquals(-6.2, retrieved.latitude)
        assertEquals(106.8, retrieved.longitude)
    }

    @Test
    fun testClearProfileResetsToDefault() = runBlocking {
        val testProfile = Profile(
            id = 1L,
            name = "John Doe",
            email = "john@example.com",
            latitude = -6.2,
            longitude = 106.8
        )
        repository.saveProfile(testProfile)
        
        repository.clearProfile()
        
        val retrieved = repository.getProfile()
        assertNotNull(retrieved)
        assertEquals("Anonim", retrieved!!.name)
        assertEquals("anonim@kosthub.com", retrieved.email)
    }
}
