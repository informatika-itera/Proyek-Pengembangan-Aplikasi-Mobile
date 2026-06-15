package com.studymate.presentation.screens.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.studymate.core.util.LocalGoogleAuth
import com.studymate.domain.model.AchievementTier
import com.studymate.domain.model.ActivityDay
import com.studymate.domain.model.Badge
import com.studymate.domain.model.Reminder
import com.studymate.presentation.components.LoadingIndicator
import com.studymate.presentation.theme.*
import kotlinx.datetime.*
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel(),
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    onNavigateToPlanner: (LocalDate?) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    var showEditDialog by remember { mutableStateOf(false) }
    
    val onGoogleSignIn = LocalGoogleAuth.current

    if (showEditDialog && uiState.user != null) {
        EditProfileDialog(
            user = uiState.user!!,
            onDismiss = { showEditDialog = false },
            onSave = { name, major, nim, goals ->
                viewModel.updateProfile(name, major, nim, goals)
                showEditDialog = false
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            ProfileTopBar(
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle,
                onLogout = { viewModel.logout() }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            LoadingIndicator()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(28.dp)
            ) {
                // 1. Avatar & Identity
                val user = uiState.user
                if (user != null) {
                    IdentitySection(
                        name = user.displayName,
                        lifeGoals = user.lifeGoals,
                        photoUrl = user.displayPhoto,
                        onEdit = { showEditDialog = true }
                    )
                } else {
                    LoginPromptCard(onSignIn = { onGoogleSignIn() })
                }

                // 2. Main Reminder Card
                ReminderSection(
                    reminder = uiState.closestReminder,
                    timeRemaining = uiState.timeRemaining,
                    onNavigate = onNavigateToPlanner
                )

                // 3. Learning Heatmap
                LearningHeatmapSection(uiState.heatmap)

                // 4. Achievements
                AchievementSection(uiState.achievementTier)
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar(isDarkTheme: Boolean, onThemeToggle: () -> Unit, onLogout: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                "Profil Saya",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
            )
        },
        navigationIcon = {
            IconButton(onClick = onLogout) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Logout",
                    tint = Color(0xFFE91E63)
                )
            }
        },
        actions = {
            IconButton(
                onClick = onThemeToggle,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(44.dp)
                    .background(
                        if (isDarkTheme) Color(0xFF3F37C9).copy(alpha = 0.2f) 
                        else Color(0xFF4361EE).copy(alpha = 0.1f), 
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = if (isDarkTheme) Icons.Default.NightsStay else Icons.Default.WbSunny,
                    contentDescription = "Toggle Theme",
                    tint = if (isDarkTheme) Color(0xFF4CC9F0) else Color(0xFFF72585),
                    modifier = Modifier.size(22.dp)
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

@Composable
fun IdentitySection(
    name: String,
    lifeGoals: String,
    photoUrl: String?,
    onEdit: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.BottomEnd) {
            Surface(
                modifier = Modifier.size(120.dp).shadow(12.dp, CircleShape),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
            ) {
                if (!photoUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = "Profile",
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Default.Person, 
                        null, 
                        modifier = Modifier.padding(24.dp).size(64.dp),
                        tint = Color.Gray.copy(alpha = 0.5f)
                    )
                }
            }
            Surface(
                modifier = Modifier
                    .size(36.dp)
                    .offset(x = (-4).dp, y = (-4).dp)
                    .clickable { onEdit() },
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 6.dp,
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
            ) {
                Icon(
                    Icons.Default.Edit,
                    null,
                    modifier = Modifier.padding(8.dp),
                    tint = Color(0xFF4361EE)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onEdit() }
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                Icons.Default.Edit,
                null,
                modifier = Modifier.size(16.dp),
                tint = Color.Gray.copy(alpha = 0.6f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LifeGoalsRibbon(lifeGoals)
    }
}

@Composable
fun LifeGoalsRibbon(text: String) {
    val ribbonColor = Brush.horizontalGradient(
        colors = listOf(Color(0xFF4361EE), Color(0xFFF72585))
    )
    
    Box(
        modifier = Modifier.fillMaxWidth().height(48.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(42.dp)) {
            val width = size.width
            val height = size.height
            val arrowWidth = 16.dp.toPx()
            
            val path = Path().apply {
                moveTo(0f, height / 2)
                lineTo(arrowWidth, 0f)
                lineTo(width - arrowWidth, 0f)
                lineTo(width, height / 2)
                lineTo(width - arrowWidth, height)
                lineTo(arrowWidth, height)
                close()
            }
            drawPath(path, ribbonColor)
        }
        Text(
            text = "Live Goals: ${if (text.isBlank()) "Lulus IPK 3.8+" else text}",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
fun ReminderSection(
    reminder: Reminder?,
    timeRemaining: String,
    onNavigate: (LocalDate?) -> Unit
) {
    val gradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFFF72585), Color(0xFF7209B7))
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(24.dp))
            .background(gradient, RoundedCornerShape(24.dp))
            .clickable { 
                reminder?.let { 
                    val date = Instant.fromEpochMilliseconds(it.dueDate).toLocalDateTime(TimeZone.currentSystemDefault()).date
                    onNavigate(date)
                }
            }
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (reminder == null) {
                    Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(32.dp))
                } else {
                    Text("!", color = Color.White, fontWeight = FontWeight.Black, fontSize = 36.sp)
                }
            }
            
            Spacer(modifier = Modifier.width(20.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                if (reminder != null) {
                    Text(
                        reminder.title.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "DL akhir",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                } else {
                    Text(
                        "Semua tugas selesai!",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                }
            }
            
            if (reminder != null) {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(20.dp),
                    shadowElevation = 4.dp
                ) {
                    Text(
                        timeRemaining.ifBlank { "Tersisa" },
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFF72585),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun LearningHeatmapSection(heatmap: List<ActivityDay>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text("Aktivitas Belajar", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium)
            Text("Kontribusi Kualitas", color = Color(0xFF4361EE), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                val heatmapMap = heatmap.associate { it.date to it.totalPoints }
                val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                
                repeat(7) { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        repeat(18) { col ->
                            val daysAgo = (17 - col) * 7 + (6 - row)
                            val date = today.minus(daysAgo, DateTimeUnit.DAY).toString()
                            val points = heatmapMap[date] ?: 0
                            val color = when {
                                points >= 10 -> Color(0xFF4361EE)
                                points >= 5 -> Color(0xFF4361EE).copy(alpha = 0.7f)
                                points >= 1 -> Color(0xFF4361EE).copy(alpha = 0.3f)
                                else -> Color.LightGray.copy(alpha = 0.2f)
                            }
                            Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(2.dp)).background(color))
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Kurang", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Spacer(modifier = Modifier.width(6.dp))
                    listOf(0.1f, 0.3f, 0.7f, 1f).forEach { 
                        Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(Color(0xFF4361EE).copy(alpha = it)))
                        Spacer(modifier = Modifier.width(3.dp))
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Rajin", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun AchievementSection(tier: AchievementTier) {
    val badges = listOf(
        Badge("Bronze", Icons.Default.EmojiEvents, tier >= AchievementTier.BRONZE, Color(0xFFCD7F32)),
        Badge("Silver", Icons.Default.EmojiEvents, tier >= AchievementTier.SILVER, Color(0xFFC0C0C0)),
        Badge("Gold Learner", Icons.Default.EmojiEvents, tier >= AchievementTier.GOLD, Color(0xFFFFD700)),
        Badge("Master AI", Icons.Default.AutoAwesome, false, Color(0xFF7209B7))
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Pencapaian (Kuis 30 Hari)", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(badges) { badge ->
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(80.dp)) {
                    Surface(
                        modifier = Modifier.size(72.dp).shadow(if (badge.isEarned) 6.dp else 0.dp, CircleShape),
                        shape = CircleShape,
                        color = if (badge.isEarned) badge.color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                        border = if (badge.isEarned) BorderStroke(2.dp, badge.color) else null
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                badge.icon, 
                                null, 
                                tint = if (badge.isEarned) badge.color else Color.Gray.copy(alpha = 0.4f),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        badge.name, 
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (badge.isEarned) FontWeight.Bold else FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun LoginPromptCard(onSignIn: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.AccountCircle, null, modifier = Modifier.size(64.dp), tint = PrimaryLight)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Belum Masuk", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
            Text("Masuk untuk sinkronisasi data Anda.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onSignIn, 
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(48.dp).fillMaxWidth(0.7f)
            ) {
                Text("Masuk dengan Google")
            }
        }
    }
}

@Composable
fun EditProfileDialog(
    user: com.studymate.domain.model.UserProfile,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(user.localName ?: user.googleName ?: "") }
    var major by remember { mutableStateOf(user.major) }
    var nim by remember { mutableStateOf(user.nim) }
    var goals by remember { mutableStateOf(user.lifeGoals) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profil") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Lengkap") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = major, onValueChange = { major = it }, label = { Text("Prodi") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = nim, onValueChange = { nim = it }, label = { Text("NIM") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = goals, onValueChange = { goals = it }, label = { Text("Life Goals") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = { Button(onClick = { onSave(name, major, nim, goals) }) { Text("Simpan") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}
