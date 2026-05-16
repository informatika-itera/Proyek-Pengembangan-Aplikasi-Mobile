package com.kelazzz.app.domain.repository

import com.kelazzz.app.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface untuk autentikasi
 */
interface AuthRepository {
    /** Login dengan kredensial ITERA */
    suspend fun login(username: String, password: String): Result<User>

    /** Logout — clear session */
    suspend fun logout()

    /** Observe login state */
    val isLoggedIn: Flow<Boolean>

    /** Observe current user info */
    val currentUser: Flow<User?>
}
