package com.example.Roomie.core.util

sealed class RoomieError : Throwable() {
    data class DatabaseError(override val message: String) : RoomieError()
    data class AuthError(override val message: String) : RoomieError()
    data class NetworkError(override val message: String) : RoomieError()
    data class ValidationError(override val message: String) : RoomieError()
    data class UnknownError(override val message: String = "Terjadi kesalahan tidak dikenal") : RoomieError()
}

/**
 * Helper to wrap code into a Result with custom RoomieError
 */
inline fun <T> wrapResult(block: () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: Exception) {
        Result.failure(when (e) {
            is RoomieError -> e
            else -> RoomieError.UnknownError(e.message ?: "Terjadi kesalahan")
        })
    }
}
