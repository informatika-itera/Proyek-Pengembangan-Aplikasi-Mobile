package com.example.noteai.data.repository

import app.cash.turbine.test
import com.example.noteai.domain.model.User
import com.example.noteai.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UserRepositoryTest {
    private lateinit var repository: FakeUserRepository

    @BeforeTest
    fun setup() { repository = FakeUserRepository() }

    @Test
    fun `register should add user to list`() = runTest {
        val user = User(email = "test@mail.com", name = "Test", password = "123")
        assertTrue(repository.register(user).isSuccess)
    }

    @Test
    fun `login with correct credentials should succeed`() = runTest {
        repository.register(User(email = "test@mail.com", name = "Test", password = "123"))
        val result = repository.login("test@mail.com", "123")
        assertTrue(result.isSuccess)
        assertEquals("Test", result.getOrNull()?.name)
    }

    @Test
    fun `logout should clear current user`() = runTest {
        repository.logout()
        repository.getCurrentUser().test {
            assertNull(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}

class FakeUserRepository : UserRepository {
    private val users = mutableListOf<User>()
    private val currentUser = MutableStateFlow<User?>(null)
    override fun getCurrentUser(): Flow<User?> = currentUser
    override suspend fun login(e: String, p: String): Result<User> {
        val u = users.find { it.email == e && it.password == p }
        return if (u != null) { currentUser.value = u; Result.success(u) } 
        else Result.failure(Exception())
    }
    override suspend fun register(u: User): Result<Unit> { users.add(u); return Result.success(Unit) }
    override suspend fun logout() { currentUser.value = null }
}
