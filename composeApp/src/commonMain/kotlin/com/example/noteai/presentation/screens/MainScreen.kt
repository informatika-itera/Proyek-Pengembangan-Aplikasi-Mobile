package com.example.noteai.presentation.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.noteai.presentation.screens.chat.ChatScreen
import com.example.noteai.presentation.screens.pantry.PantryScreen
import com.example.noteai.presentation.screens.recipe.RecipeScreen

@Composable
fun MainScreen(
    onRecipeClick: (Long) -> Unit,
    onAddRecipeClick: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Chat, contentDescription = "AI Chef") },
                    label = { Text("AI Chef") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Kitchen, contentDescription = "Pantry") },
                    label = { Text("Pantry") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.RestaurantMenu, contentDescription = "Recipes") },
                    label = { Text("Recipes") }
                )
            }
        }
    ) { padding ->
        Surface(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> ChatScreen()
                1 -> PantryScreen()
                2 -> RecipeScreen(
                    onRecipeClick = onRecipeClick,
                    onAddRecipeClick = onAddRecipeClick
                )
            }
        }
    }
}
