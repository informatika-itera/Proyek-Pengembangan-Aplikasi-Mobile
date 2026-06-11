package com.studymate

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.studymate.core.util.GoogleAuthHelper
import com.studymate.core.util.LocalGoogleAuth
import com.studymate.presentation.screens.profile.ProfileViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val profileViewModel: ProfileViewModel by inject()
    private val googleAuthHelper by lazy { GoogleAuthHelper(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            val scope = rememberCoroutineScope()
            
            CompositionLocalProvider(
                LocalGoogleAuth provides {
                    scope.launch {
                        println("Google Sign-In Triggered")
                        googleAuthHelper.signInWithGoogle()
                            .onSuccess { user ->
                                println("Google Sign-In Success: ${user.email}")
                                profileViewModel.signInWithGoogle(
                                    email = user.email,
                                    displayName = user.displayName,
                                    photoUrl = user.photoUrl
                                )
                            }
                            .onFailure { error ->
                                println("Google Sign-In Failure: ${error.message}")
                                Toast.makeText(this@MainActivity, "Gagal Login: ${error.message}", Toast.LENGTH_LONG).show()
                            }
                    }
                }
            ) {
                App()
            }
        }
    }
}
