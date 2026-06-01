package com.mywallet

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mywallet.navigation.MainNavigation
import com.mywallet.theme.MyWalletTheme

val LocalDarkMode = compositionLocalOf { mutableStateOf(false) }

@Composable
@Preview
fun App() {
    val systemDark = isSystemInDarkTheme()
    val darkModeState = remember { mutableStateOf(systemDark) }

    CompositionLocalProvider(LocalDarkMode provides darkModeState) {
        MyWalletTheme(darkTheme = darkModeState.value) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                MainNavigation()
            }
        }
    }
}
