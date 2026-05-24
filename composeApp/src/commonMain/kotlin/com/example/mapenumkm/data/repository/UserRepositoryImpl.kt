package com.example.mapenumkm.data.repository

import com.example.mapenumkm.data.local.NoteDatabase
import com.example.mapenumkm.data.local.entity.toDomain
import com.example.mapenumkm.domain.model.User
import com.example.mapenumkm.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class UserRepositoryImpl(private val database: NoteDatabase) : UserRepository {
    private val queries = database.noteQueries

    override suspend fun insertUser(user: User) = withContext(Dispatchers.Default) {
        queries.insertUser(
            name = user.name,
            email = user.email,
            phone = user.phone,
            password = user.password,
            created_at = user.createdAt
        )
    }

    override suspend fun getUserByEmailOrPhone(identifier: String): User? = withContext(Dispatchers.Default) {
        queries.getUserByEmailOrPhone(identifier, identifier)
            .executeAsOneOrNull()?.toDomain()
    }

    override suspend fun updatePassword(identifier: String, newPassword: String) = withContext(Dispatchers.Default) {
        queries.updatePassword(
            password = newPassword,
            email = identifier,
            phone = identifier
        )
    }
}
