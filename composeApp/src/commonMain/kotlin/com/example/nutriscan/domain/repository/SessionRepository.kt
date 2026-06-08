package com.example.nutriscan.domain.repository

import com.example.nutriscan.domain.model.UserRole
import kotlinx.coroutines.flow.Flow

/**
 * State snapshot dari sesi pengguna yang sedang aktif.
 */
data class SessionState(
    val isLoggedIn: Boolean,
    val role: UserRole,
    val userName: String,
    val coins: Int,
    val onboardingCompleted: Boolean
)

/**
 * Repository untuk mengelola sesi login dan coin pengguna.
 */
interface SessionRepository {
    /** Observe state sesi saat ini. */
    val state: Flow<SessionState>

    /** Observe saldo koin saat ini. */
    val coins: Flow<Int>

    /** Login dengan role dan nama yang dipilih. */
    suspend fun login(role: UserRole, name: String)

    /** Logout dari sesi saat ini. */
    suspend fun logout()

    /** Tambah koin (top-up). */
    suspend fun topUp(amount: Int)

    /**
     * Coba belanjakan [amount] koin.
     * Return true jika saldo cukup dan berhasil dikurangi, false jika tidak.
     */
    suspend fun trySpend(amount: Int): Boolean
}