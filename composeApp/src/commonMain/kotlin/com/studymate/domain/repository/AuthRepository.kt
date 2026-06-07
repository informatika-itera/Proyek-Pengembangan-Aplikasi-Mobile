package com.studymate.domain.repository

import com.studymate.domain.model.UserProfile
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val currentUser: StateFlow<UserProfile?>
    suspend fun signIn(
        email: String,
        displayName: String?,
        photoUrl: String?
    ): Result<UserProfile>
    suspend fun signOut()
}
