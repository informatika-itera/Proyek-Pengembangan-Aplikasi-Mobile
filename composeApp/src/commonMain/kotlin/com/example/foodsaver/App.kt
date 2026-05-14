package com.example.foodsaver

import androidx.compose.runtime.Composable
import com.example.foodsaver.presentation.navigation.AppNavHost
import com.example.foodsaver.presentation.theme.FoodSaverTheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        FoodSaverTheme {
            AppNavHost()
        }
    }
}
