package com.example.pocketguard.presentation.screens.ai

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pocketguard.presentation.theme.PgPrimary
import com.example.pocketguard.presentation.theme.PgPrimaryLight
import com.example.pocketguard.presentation.theme.PgWarning
import org.koin.compose.viewmodel.koinViewModel

// Struktur data pesan lokal pembantu jika belum didefinisikan secara eksplisit
data class ChatMessage(
    val text: String,
    val isUser: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIAssistantScreen(
    initialText: String?,
    onNavigateBack: () -> Unit,
    viewModel: AIAssistantViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // State lokal untuk menangani teks input obrolan pengguna
    var promptInput by remember { mutableStateOf("") }

    // Simulasi daftar chat history lokal yang disinkronkan dengan respons arsitektur Anda
    val chatMessages = remember { mutableStateListOf<ChatMessage>() }
    val listState = rememberLazyListState()

    // Memuat teks awal jika dikirimkan lewat argument navigasi halaman lain
    LaunchedEffect(initialText) {
        if (!initialText.isNullOrBlank()) {
            promptInput = initialText
        }
    }

    // Mengotomatiskan scroll ke baris chat paling bawah setiap ada pesan baru masuk
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Asisten Keuangan", fontWeight = FontWeight.SemiBold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        // 5. AI INPUT ROW - Kotak input pengetikan pesan di bagian bawah layar
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth().imePadding(),
                tonalElevation = 2.dp,
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .navigationBarsPadding(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = promptInput,
                        onValueChange = { promptInput = it },
                        placeholder = { Text("Tanya sesuatu ke PocketGuard AI...") },
                        maxLines = 3,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PgPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )

                    IconButton(
                        onClick = {
                            if (promptInput.isNotBlank()) {
                                chatMessages.add(ChatMessage(text = promptInput, isUser = true))
                                // Integrasikan dengan fungsi pengiriman ViewModel/AIRepository Anda di sini
                                // viewModel.sendFinancialPrompt(promptInput)
                                promptInput = ""

                                // Simulasi jawaban pintar tiruan dari AI untuk testing Sprint 2 demo
                                chatMessages.add(ChatMessage(
                                    text = "Analisis terdeteksi. Pengeluaran hiburanmu sudah 85% dari batas anggaran bulanan. Pertimbangkan membatasi streaming service untuk menghemat Rp 150.000! 💡",
                                    isUser = false
                                ))
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = PgPrimary),
                        modifier = Modifier.size(44.dp),
                        enabled = promptInput.isNotBlank()
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Kirim",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // 1. AI ACTIVE STATUS BADGE
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .background(color = PgPrimaryLight, shape = RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(color = PgPrimary, shape = CircleShape)
                )
                Text(
                    text = "AI Aktif",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PgPrimary
                )
            }

            // 2. AI INSIGHT PANEL - Bar Indikator Batas Alokasi Anggaran Bulanan
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Analisis bulan ini",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // Baris Indikator Kategori Makanan (Status Aman)
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("🍜 Makanan (62%)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { 0.62f },
                            color = PgPrimary,
                            trackColor = MaterialTheme.colorScheme.outlineVariant,
                            strokeCap = StrokeCap.Round,
                            modifier = Modifier.fillMaxWidth().height(6.dp)
                        )
                    }

                    // Baris Indikator Kategori Hiburan (Status Melebihi Batas Anggaran)
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("🎬 Hiburan (85%)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("⚠️ Melebihi batas", fontSize = 11.sp, color = PgWarning, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { 0.85f },
                            color = PgWarning,
                            trackColor = MaterialTheme.colorScheme.outlineVariant,
                            strokeCap = StrokeCap.Round,
                            modifier = Modifier.fillMaxWidth().height(6.dp)
                        )
                    }
                }
            }

            // 3 & 4. SCROLLABLE CHAT ITEMS & ASYMMETRIC CORNER BUBBLES
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(chatMessages) { message ->
                    val isUser = message.isUser

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isUser) PgPrimary else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(
                                topStart = 12.dp,
                                topEnd = 12.dp,
                                bottomStart = if (isUser) 12.dp else 2.dp, // Lengkungan asimetris bot CSS
                                bottomEnd = if (isUser) 2.dp else 12.dp   // Lengkungan asimetris user CSS
                            ),
                            border = if (isUser) null else BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.fillMaxWidth(0.85f)
                        ) {
                            Text(
                                text = message.text,
                                fontSize = 13.sp,
                                color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}