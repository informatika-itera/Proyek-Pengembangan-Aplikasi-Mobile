package com.example.noteai.presentation.screens.reference

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.noteai.presentation.components.hujjah.HujjahEmptyState
import com.example.noteai.presentation.components.hujjah.HujjahErrorState
import com.example.noteai.presentation.components.hujjah.HujjahLoadingState
import com.example.noteai.presentation.components.hujjah.HujjahMenuItem
import com.example.noteai.presentation.components.hujjah.HujjahSprint2MenuBar
import com.example.noteai.presentation.components.hujjah.HujjahPrimaryButton
import com.example.noteai.presentation.components.hujjah.HujjahUiColors
import com.example.noteai.presentation.components.hujjah.SourceBadge
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferenceDetailScreen(
    referenceId: String,
    onNavigateBack: () -> Unit,
    onNavigateToBookmarks: () -> Unit,
    onNavigateToLens: () -> Unit,
    onNavigateToDemoResult: () -> Unit,
    onNavigateToDemoDetail: () -> Unit,
    viewModel: ReferenceDetailViewModel = koinViewModel(parameters = { parametersOf(referenceId) })
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = HujjahUiColors.Background,
        topBar = {
            TopAppBar(
                title = { Text("Detail Referensi") },
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
                currentItem = HujjahMenuItem.DETAIL,
                onNavigateToLens = onNavigateToLens,
                onNavigateToResult = onNavigateToDemoResult,
                onNavigateToDetail = onNavigateToDemoDetail,
                onNavigateToBookmarks = onNavigateToBookmarks
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> HujjahLoadingState()
            uiState.errorMessage != null -> HujjahErrorState(
                message = uiState.errorMessage ?: "Terjadi kesalahan"
            )
            uiState.reference == null -> HujjahEmptyState(
                title = "Referensi tidak ditemukan",
                message = "Data dalil belum tersedia."
            )
            else -> {
                val reference = uiState.reference ?: return@Scaffold

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(28.dp))
                            .background(HujjahUiColors.Primary)
                            .padding(22.dp)
                    ) {
                        SourceBadge(
                            sourceType = reference.sourceType,
                            modifier = Modifier.background(HujjahUiColors.White.copy(alpha = 0f))
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = reference.title,
                            color = HujjahUiColors.White,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = reference.sourceName,
                            color = HujjahUiColors.White.copy(alpha = 0.92f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = reference.topicTitle,
                            color = HujjahUiColors.White.copy(alpha = 0.82f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = reference.arabicText,
                        color = HujjahUiColors.PrimaryDark,
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Terjemahan",
                        color = HujjahUiColors.TextDark,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = reference.translation,
                        color = HujjahUiColors.TextDark,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Penjelasan",
                        color = HujjahUiColors.TextDark,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = reference.explanation,
                        color = HujjahUiColors.TextMuted,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = uiState.note,
                        onValueChange = viewModel::onNoteChanged,
                        label = { Text("Catatan pribadi") },
                        placeholder = { Text("Tulis catatan singkat untuk referensi ini...") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (uiState.isBookmarked) {
                        HujjahPrimaryButton(
                            text = "Update Catatan",
                            onClick = viewModel::updateNote
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        HujjahPrimaryButton(
                            text = "Hapus dari Tersimpan",
                            onClick = viewModel::deleteBookmark
                        )
                    } else {
                        HujjahPrimaryButton(
                            text = "Simpan ke Tersimpan",
                            onClick = viewModel::saveBookmark
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    HujjahPrimaryButton(
                        text = "Buka Tersimpan",
                        onClick = onNavigateToBookmarks
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
