package com.example.foodsaver.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.noteai.presentation.screens.addnote.AddNoteScreen
import com.example.noteai.presentation.screens.ai.AIAssistantScreen
import com.example.noteai.presentation.screens.detail.NoteDetailScreen
import com.example.noteai.presentation.screens.home.HomeScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                onNavigateToAddNote = { navController.navigate("add_note") },
                onNavigateToDetail = { id -> navController.navigate("detail/$id") },
                onNavigateToAI = { navController.navigate("ai") }
            )
        }

        composable(
            route = "add_note?noteId={noteId}",
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong("noteId")?.takeIf { it != -1L }
            AddNoteScreen(
                noteId = noteId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAI = { content -> navController.navigate("ai?text=$content") }
            )
        }

        composable(
            route = "detail/{noteId}",
            arguments = listOf(
                navArgument("noteId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong("noteId") ?: return@composable
            NoteDetailScreen(
                noteId = noteId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { id -> navController.navigate("add_note?noteId=$id") },
                onShare = { /* Implementasi share jika diperlukan */ }
            )
        }

        composable(
            route = "ai?text={text}",
            arguments = listOf(
                navArgument("text") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val initialText = backStackEntry.arguments?.getString("text")
            AIAssistantScreen(
                noteId = null, // Bisa dihubungkan jika ingin apply ke note spesifik
                initialText = initialText,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
