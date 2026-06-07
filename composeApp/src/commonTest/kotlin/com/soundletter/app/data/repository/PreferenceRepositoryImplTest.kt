package com.soundletter.app.data.repository

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PreferenceRepositoryImplTest {

    @Test
    fun `test toggleDarkMode updates state correctly`() = runTest {
        // Karena PreferenceRepositoryImpl biasanya membungkus DataStore atau state sederhana
        val repository = PreferenceRepositoryImpl()
        
        // Awalnya false
        assertEquals(false, repository.isDarkMode.value)
        
        // Toggle ke true
        repository.toggleDarkMode(true)
        assertEquals(true, repository.isDarkMode.value)
        
        // Toggle kembali ke false
        repository.toggleDarkMode(false)
        assertEquals(false, repository.isDarkMode.value)
    }
}
