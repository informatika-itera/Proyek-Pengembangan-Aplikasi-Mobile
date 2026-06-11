package com.studymate.core.util

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.studymate.core.network.ApiConfig
import org.json.JSONObject
import android.util.Base64

class GoogleAuthHelper(private val context: Context) {
    private val credentialManager = CredentialManager.create(context)

    data class GoogleUser(
        val email: String,
        val displayName: String?,
        val photoUrl: String?
    )

    suspend fun signInWithGoogle(): Result<GoogleUser> {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(ApiConfig.googleWebClientId)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            val result = credentialManager.getCredential(context, request)
            val credential = result.credential
            
            // Log the type for debugging
            println("Credential Type: ${credential.type}")
            
            when (credential.type) {
                GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL -> {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val user = decodeIdToken(googleIdTokenCredential.idToken)
                    Result.success(user)
                }
                else -> {
                    Result.failure(Exception("Unexpected credential type: ${credential.type}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun decodeIdToken(idToken: String): GoogleUser {
        val parts = idToken.split(".")
        val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
        val json = JSONObject(payload)
        return GoogleUser(
            email = json.getString("email"),
            displayName = json.optString("name"),
            photoUrl = json.optString("picture")
        )
    }
}
