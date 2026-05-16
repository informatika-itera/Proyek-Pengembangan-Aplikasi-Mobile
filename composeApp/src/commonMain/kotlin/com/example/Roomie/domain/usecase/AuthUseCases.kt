package com.example.Roomie.domain.usecase

import com.example.Roomie.domain.model.User
import com.example.Roomie.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class GetCurrentUserUseCase(private val repository: AuthRepository) {
    operator fun invoke(): Flow<User?> = repository.getCurrentUser()
}

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(idNumber: String): Result<User> = repository.login(idNumber)
}

class LogoutUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke() = repository.logout()
}
