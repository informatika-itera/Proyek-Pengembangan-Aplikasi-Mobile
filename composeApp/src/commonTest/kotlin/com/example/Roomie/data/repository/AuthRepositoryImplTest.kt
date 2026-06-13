package com.example.Roomie.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.example.Roomie.data.local.datastore.UserPreferences
import com.example.Roomie.domain.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AuthRepositoryImplTest {
    private lateinit var repository: AuthRepositoryImpl
    private lateinit var userPreferences: UserPreferences

    @BeforeTest
    fun setup() {
        val mockDataStore = object : DataStore<Preferences> {
            private val _data = MutableStateFlow(emptyPreferences())
            override val data = _data.asStateFlow()
            override suspend fun updateData(transform: suspend (Preferences) -> Preferences): Preferences {
                val next = transform(_data.value)
                _data.value = next
                return next
            }
        }
        userPreferences = UserPreferences(mockDataStore)
        repository = AuthRepositoryImpl(userPreferences)
    }

    @Test
    fun `login should save user as ADMIN when idNumber is short`() = runTest {
        val idNumber = "123"
        val result = repository.login(idNumber)
        
        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertEquals(UserRole.ADMIN, user?.role)
        assertEquals(idNumber, user?.nim)
        
        val savedUser = repository.getCurrentUser().first()
        assertEquals(user, savedUser)
    }

    @Test
    fun `login should save user as STUDENT when idNumber is long`() = runTest {
        val idNumber = "121140019"
        val result = repository.login(idNumber)
        
        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertEquals(UserRole.STUDENT, user?.role)
        assertEquals(idNumber, user?.nim)
    }

    @Test
    fun `logout should clear user data`() = runTest {
        repository.login("123")
        repository.logout()
        
        val user = repository.getCurrentUser().first()
        assertEquals(null, user)
    }
}
