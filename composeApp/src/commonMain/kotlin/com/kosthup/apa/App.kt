package com.kosthup.apa

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun App() {
    MaterialTheme {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = Routes.Home
        ) {
            composable(Routes.Home) {
                HomeScreen(
                    onNavigateDetail = { navController.navigate(Routes.Detail) }
                )
            }
            composable(Routes.Detail) {
                DetailScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

private object Routes {
    const val Home = "home"
    const val Detail = "detail"
}

@Composable
private fun HomeScreen(onNavigateDetail: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Hello world")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onNavigateDetail) {
            Text(text = "Go to detail")
        }
    }
}

@Composable
private fun DetailScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Detail screen")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack) {
            Text(text = "Back")
        }
    }
}
