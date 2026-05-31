package com.example.pantaujompo.presentation.screens.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pantaujompo.presentation.theme.DarkBackground
import com.example.pantaujompo.presentation.theme.MeshBackground
import com.example.pantaujompo.presentation.theme.glassCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ChatMessage(val text: String, val isUser: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatScreen(
    onNavigateBack: () -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background == DarkBackground
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant
    // Gunakan warna aksen dari tema agar konsisten di seluruh aplikasi
    val accentColor = MaterialTheme.colorScheme.primary
    val surfaceColor = if (isDark) Color(0xFF151515) else MaterialTheme.colorScheme.surface

    val userPrefs: com.example.pantaujompo.data.local.datastore.UserPreferences = org.koin.compose.koinInject()
    val language by userPrefs.language.collectAsState(initial = "id")
    fun str(key: String) = com.example.pantaujompo.core.util.AppStrings.get(key, language)

    var inputText by remember { mutableStateOf("") }
    // Pesan sambutan AI saat pertama dibuka
    val messages = remember { mutableStateListOf<ChatMessage>() }
    
    // Add welcome message only after language is loaded or on init if language is handled
    LaunchedEffect(language) {
        if (messages.isEmpty()) {
            messages.add(ChatMessage(str("halo_ai"), false))
        }
    }
    var isTyping by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()
    
    // Inisialisasi layanan Gemini AI
    val geminiService = remember { com.example.pantaujompo.data.remote.api.GeminiService() }
    val usia by userPrefs.userAge.collectAsState(initial = 0)
    val namaUser by userPrefs.userName.collectAsState(initial = "")
    val profilData = "Usia $usia tahun, nama $namaUser"
    
    // Auto-scroll ke bawah saat ada pesan baru
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(0)
    }

    MeshBackground(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(48.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(44.dp).clip(CircleShape)
                        .background(surfaceColor)
                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                        .clickable { onNavigateBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ArrowBack, null, tint = textPrimary, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Box(
                    modifier = Modifier.size(44.dp).clip(CircleShape).background(accentColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AutoAwesome, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(str("ai_assistant"), color = textPrimary, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                    Text(str("gaya_hidup_sehat"), color = accentColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)

            // Chat List
            LazyColumn(
                modifier = Modifier.weight(1f).padding(horizontal = 24.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                reverseLayout = true
            ) {
                items(messages.reversed()) { msg ->
                    ChatBubble(msg = msg, isDark = isDark, accentColor = accentColor)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            if (isTyping) {
                Row(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(str("ai_sedang_mengetik"), color = textSecondary, fontSize = 12.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                }
            }

            // Input Area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassCard(shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .navigationBarsPadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text(str("tanya_kesehatan"), color = textSecondary) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = if (isDark) Color(0xFF1E1E1E) else Color(0xFFF5F5F5),
                        unfocusedContainerColor = if (isDark) Color(0xFF1E1E1E) else Color(0xFFF5F5F5),
                        focusedTextColor = textPrimary,
                        unfocusedTextColor = textPrimary
                    ),
                    maxLines = 3
                )
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier.size(52.dp).clip(CircleShape).background(if (inputText.isNotBlank()) accentColor else Color.Gray)
                        .clickable(enabled = inputText.isNotBlank() && !isTyping) {
                            val userText = inputText
                            messages.add(ChatMessage(userText, true))
                            inputText = ""
                            isTyping = true
                            
                            // Memanggil Gemini AI Asli
                            coroutineScope.launch {
                                val hasilAi = geminiService.tanyaChat(userText, profilData)
                                messages.add(ChatMessage(hasilAi, false))
                                isTyping = false
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Send, null, tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun ChatBubble(msg: ChatMessage, isDark: Boolean, accentColor: Color) {
    val surfaceColor = if (isDark) Color(0xFF1E1E1E) else Color.White
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!msg.isUser) {
            Box(
                modifier = Modifier.size(32.dp).clip(CircleShape).background(accentColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AutoAwesome, null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .clip(RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp,
                    bottomStart = if (msg.isUser) 20.dp else 4.dp,
                    bottomEnd = if (msg.isUser) 4.dp else 20.dp
                ))
                .glassCard(shape = RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp,
                    bottomStart = if (msg.isUser) 20.dp else 4.dp,
                    bottomEnd = if (msg.isUser) 4.dp else 20.dp
                ), neonColor = if(msg.isUser) accentColor else Color(0xFF00BCD4))
                .padding(16.dp)
        ) {
            Text(
                text = msg.text,
                color = if (msg.isUser) Color.White else MaterialTheme.colorScheme.onBackground,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}
