package com.example.Roomie.data.repository

import com.example.Roomie.domain.model.User
import com.example.Roomie.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeAuthRepository : AuthRepository {
    private val _currentUser = MutableStateFlow<User?>(null)
    
    override fun getCurrentUser(): Flow<User?> = _currentUser

    override suspend fun login(idNumber: String): Result<User> {
        return Result.success(User("1", "Test User", idNumber))
    }

    override suspend fun logout() {
        _currentUser.value = null
    }
    
    fun setCurrentUser(user: User?) {
        _currentUser.value = user
    }
}
