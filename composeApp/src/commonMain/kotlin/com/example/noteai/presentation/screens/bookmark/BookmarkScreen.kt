package com.example.noteai.presentation.screens.bookmark

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.noteai.presentation.components.hujjah.HujjahBookmarkCard
import com.example.noteai.presentation.components.hujjah.HujjahEmptyState
import com.example.noteai.presentation.components.hujjah.HujjahErrorState
import com.example.noteai.presentation.components.hujjah.HujjahLoadingState
import com.example.noteai.presentation.components.hujjah.HujjahMenuItem
import com.example.noteai.presentation.components.hujjah.HujjahSprint2MenuBar
import com.example.noteai.presentation.components.hujjah.HujjahUiColors
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarkScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToLens: () -> Unit,
    onNavigateToDemoResult: () -> Unit,
    onNavigateToDemoDetail: () -> Unit,
    onNavigateToBookmarks: () -> Unit,
    viewModel: BookmarkViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = HujjahUiColors.Background,
        topBar = {
            TopAppBar(
                title = { Text("Tersimpan") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        bottomBar = {
            HujjahSprint2MenuBar(
                currentItem = HujjahMenuItem.BOOKMARK,
                onNavigateToLens = onNavigateToLens,
                onNavigateToResult = onNavigateToDemoResult,
                onNavigateToDetail = onNavigateToDemoDetail,
                onNavigateToBookmarks = onNavigateToBookmarks
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is BookmarkUiState.Loading -> HujjahLoadingState()
            is BookmarkUiState.Error -> HujjahErrorState(message = state.message)
            is BookmarkUiState.Empty -> HujjahEmptyState(
                title = "Belum ada dalil tersimpan",
                message = "Simpan ayat atau hadis agar mudah dibuka kembali."
            )
            is BookmarkUiState.Success -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(HujjahUiColors.Background)
                    .padding(paddingValues)
                    .padding(20.dp)
            ) {
                Text(
                    text = "Dalil Tersimpan",
                    color = HujjahUiColors.TextDark,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Referensi yang sudah kamu simpan",
                    color = HujjahUiColors.TextMuted,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {
                    items(state.bookmarks) { bookmark ->
                        HujjahBookmarkCard(
                            bookmark = bookmark,
                            onClick = { onNavigateToDetail(bookmark.referenceId) },
                            onDelete = { viewModel.deleteBookmark(bookmark.referenceId) },
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                }
            }
        }
    }
}
