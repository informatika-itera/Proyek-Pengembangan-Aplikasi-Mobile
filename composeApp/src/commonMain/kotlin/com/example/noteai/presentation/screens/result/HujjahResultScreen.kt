package com.example.noteai.presentation.screens.result

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.BookmarkBorder
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.noteai.presentation.components.hujjah.HujjahEmptyState
import com.example.noteai.presentation.components.hujjah.HujjahErrorState
import com.example.noteai.presentation.components.hujjah.HujjahLoadingState
import com.example.noteai.presentation.components.hujjah.HujjahMenuItem
import com.example.noteai.presentation.components.hujjah.HujjahSprint2MenuBar
import com.example.noteai.presentation.components.hujjah.HujjahReferenceCard
import com.example.noteai.presentation.components.hujjah.HujjahUiColors
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HujjahResultScreen(
    topicId: String,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToBookmarks: () -> Unit,
    onNavigateToLens: () -> Unit,
    onNavigateToDemoResult: () -> Unit,
    onNavigateToDemoDetail: () -> Unit,
    viewModel: HujjahResultViewModel = koinViewModel(parameters = { parametersOf(topicId) })
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = HujjahUiColors.Background,
        topBar = {
            TopAppBar(
                title = { Text("Hasil Hujjah") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToBookmarks) {
                        Icon(Icons.Outlined.BookmarkBorder, contentDescription = "Tersimpan")
                    }
                }
            )
        },
        bottomBar = {
            HujjahSprint2MenuBar(
                currentItem = HujjahMenuItem.RESULT,
                onNavigateToLens = onNavigateToLens,
                onNavigateToResult = onNavigateToDemoResult,
                onNavigateToDetail = onNavigateToDemoDetail,
                onNavigateToBookmarks = onNavigateToBookmarks
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is HujjahResultUiState.Loading -> HujjahLoadingState()
            is HujjahResultUiState.Error -> HujjahErrorState(
                message = state.message,
                onRetry = viewModel::loadReferences
            )
            is HujjahResultUiState.Empty -> HujjahEmptyState(
                title = "Belum ada referensi",
                message = "Topik ini belum memiliki data dalil."
            )
            is HujjahResultUiState.Success -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(20.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(26.dp))
                            .background(HujjahUiColors.Primary)
                            .padding(20.dp)
                    ) {
                        Text(
                            text = state.topicTitle,
                            color = HujjahUiColors.White,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${state.references.size} referensi ditemukan",
                            color = HujjahUiColors.White.copy(alpha = 0.92f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Solusi Berdalil",
                        color = HujjahUiColors.TextDark,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Berikut dalil awal yang dapat menjadi pegangan. Integrasi AI/Gemini bisa dilanjutkan pada Sprint 3.",
                        color = HujjahUiColors.TextMuted,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                items(state.references) { reference ->
                    HujjahReferenceCard(
                        reference = reference,
                        onClick = { onNavigateToDetail(reference.id) },
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }
        }
    }
}
