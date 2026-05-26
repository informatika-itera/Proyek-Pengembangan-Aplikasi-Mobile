package com.example.hujjah.presentation.screens.bookmark

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
import com.example.hujjah.presentation.components.hujjah.HujjahBookmarkCard
import com.example.hujjah.presentation.components.hujjah.HujjahEmptyState
import com.example.hujjah.presentation.components.hujjah.HujjahErrorState
import com.example.hujjah.presentation.components.hujjah.HujjahLoadingState
import com.example.hujjah.presentation.components.hujjah.HujjahMenuItem
import com.example.hujjah.presentation.components.hujjah.HujjahSprint2MenuBar
import com.example.hujjah.presentation.components.hujjah.HujjahUiColors
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import com.example.hujjah.data.local.datastore.UserPreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarkScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToLens: () -> Unit,
    onNavigateToQuran: () -> Unit,
    onNavigateToHadith: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: BookmarkViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val userPreferences = koinInject<UserPreferences>()
    val arabicFontSize by userPreferences.arabicFontSize.collectAsStateWithLifecycle(initialValue = 22)

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
                currentItem = HujjahMenuItem.PROFILE,
                onNavigateToHome = onNavigateToHome,
                onNavigateToLens = onNavigateToLens,
                onNavigateToQuran = onNavigateToQuran,
                onNavigateToHadith = onNavigateToHadith,
                onNavigateToProfile = onNavigateToProfile
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
                            arabicFontSize = arabicFontSize,
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
