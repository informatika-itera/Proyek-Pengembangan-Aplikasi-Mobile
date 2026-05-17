package com.example.noteai.presentation.screens.lens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.noteai.presentation.components.hujjah.HujjahEmptyState
import com.example.noteai.presentation.components.hujjah.HujjahErrorState
import com.example.noteai.presentation.components.hujjah.HujjahHeroCard
import com.example.noteai.presentation.components.hujjah.HujjahLoadingState
import com.example.noteai.presentation.components.hujjah.HujjahPrimaryButton
import com.example.noteai.presentation.components.hujjah.HujjahMenuItem
import com.example.noteai.presentation.components.hujjah.HujjahSprint2MenuBar
import com.example.noteai.presentation.components.hujjah.HujjahTopicChip
import com.example.noteai.presentation.components.hujjah.HujjahUiColors
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HujjahLensScreen(
    onNavigateToResult: (String) -> Unit,
    onNavigateToBookmarks: () -> Unit,
    onNavigateToLens: () -> Unit,
    onNavigateToDemoResult: () -> Unit,
    onNavigateToDemoDetail: () -> Unit,
    viewModel: HujjahLensViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = HujjahUiColors.Background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Assalamualaikum",
                            color = HujjahUiColors.TextDark,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Temukan hujjah hari ini",
                            color = HujjahUiColors.TextMuted,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToBookmarks) {
                        Icon(
                            imageVector = Icons.Outlined.BookmarkBorder,
                            contentDescription = "Tersimpan",
                            tint = HujjahUiColors.PrimaryDark
                        )
                    }
                }
            )
        },
        bottomBar = {
            HujjahSprint2MenuBar(
                currentItem = HujjahMenuItem.LENS,
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
            uiState.topics.isEmpty() -> HujjahEmptyState(
                title = "Belum ada topik",
                message = "Data topik belum tersedia."
            )
            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                HujjahHeroCard()

                Spacer(modifier = Modifier.height(20.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(HujjahUiColors.White)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Ceritakan kondisimu",
                        color = HujjahUiColors.TextDark,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "",
                        color = HujjahUiColors.TextMuted,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = uiState.inputText,
                        onValueChange = viewModel::onInputTextChanged,
                        placeholder = {
                            Text("Contoh: Saya sedang mudah marah...")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    HujjahPrimaryButton(
                        text = "Cari Dalil",
                        onClick = { onNavigateToResult(viewModel.selectedTopicId()) }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Topik Cepat",
                    color = HujjahUiColors.TextDark,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.topics.forEach { topic ->
                        HujjahTopicChip(
                            icon = topic.icon,
                            text = topic.title,
                            selected = topic.id == uiState.selectedTopicId,
                            onClick = { viewModel.onTopicSelected(topic.id) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
