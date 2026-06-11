package com.studymate.presentation.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
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
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.ceil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel(),
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    onNavigateToPlanner: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    var showEditDialog by remember { mutableStateOf(false) }
    
    val onGoogleSignIn = LocalGoogleAuth.current

    if ((showEditDialog && uiState.user != null)) {
        EditProfileDialog(
            user = uiState.user!!,
            onDismiss = { showEditDialog = false },
            onSave = { name, major, nim ->
                viewModel.updateProfile(name, major, nim)
                showEditDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profil Saya", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.logout() },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onThemeToggle,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode, 
                            null,
                            tint = if (isDarkTheme) Color.Yellow else MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
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
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(28.dp)
            ) {
                // 1. Header Profil
                uiState.user?.let { user ->
                    ProfileHeader(
                        name = user.displayName,
                        nim = user.nim,
                        major = user.major,
                        photoUrl = user.displayPhoto,
                        onEdit = { showEditDialog = true }
                    )
                } ?: run {
                    Button(
                        onClick = onGoogleSignIn,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Text("Masuk dengan Google", color = MaterialTheme.colorScheme.onSurface)
                    }
                }

                // 2. Reminder Card
                ReminderCard(
                    reminder = uiState.closestReminder,
                    onNavigateToPlanner = onNavigateToPlanner
                )

                // 3. Learning Heatmap
                HeatmapSection(uiState.heatmap)

                // 4. Badges Section
                BadgesSection(uiState.achievementTier)
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun ProfileHeader(name: String, nim: String, major: String, photoUrl: String?, onEdit: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.BottomEnd) {
            Surface(
                modifier = Modifier
                    .size(110.dp)
                    .shadow(8.dp, CircleShape),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(3.dp, PrimaryLight)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (photoUrl != null) {
                        AsyncImage(
                            model = photoUrl,
                            contentDescription = "Profile Photo",
                            modifier = Modifier.fillMaxSize().clip(CircleShape)
                        )
                    } else {
                        Icon(
                            Icons.Default.Person, 
                            contentDescription = null, 
                            modifier = Modifier.size(64.dp), 
                            tint = PrimaryLight.copy(alpha = 0.6f)
                        )
                    }
                }
            }
            
            // Edit Button Overlay
            Surface(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .clickable { onEdit() },
                color = PrimaryLight,
                shadowElevation = 4.dp
            ) {
                Icon(
                    Icons.Default.Edit, 
                    contentDescription = null, 
                    tint = Color.White, 
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        Text(name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
        Text(
            if (major.isNotBlank()) "$major • $nim" else nim, 
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun ReminderCard(reminder: Reminder?, onNavigateToPlanner: () -> Unit) {
    if (reminder == null) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFFF72585), Color(0xFFB5179E))
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Tidak ada pengingat", fontWeight = FontWeight.Bold, color = Color.White, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    } else {
        val now = Clock.System.now().toEpochMilliseconds()
        val daysLeft = ceil((reminder.dueDate - now) / (1000.0 * 60 * 60 * 24)).toInt()
        val descriptionTruncated = if (reminder.description != null && reminder.description.length > 50) {
            reminder.description.take(47) + "..."
        } else {
            reminder.description ?: ""
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFFF72585), Color(0xFFB5179E))
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .clickable { onNavigateToPlanner() }
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.PriorityHigh, contentDescription = null, tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(reminder.title, fontWeight = FontWeight.Bold, color = Color.White, style = MaterialTheme.typography.labelMedium)
                    if (descriptionTruncated.isNotEmpty()) {
                        Text(descriptionTruncated, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Black)
                    }
                }
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "$daysLeft Hari", 
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
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
fun WarningCard(examName: String, daysLeft: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFFF72585), Color(0xFFB5179E))
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.PriorityHigh, contentDescription = null, tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Ujian Terdekat", fontWeight = FontWeight.Bold, color = Color.White, style = MaterialTheme.typography.labelMedium)
                Text(examName, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Black)
            }
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "$daysLeft Hari", 
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontWeight = FontWeight.Black, 
                    color = Color(0xFFF72585),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun HeatmapSection(heatmap: List<ActivityDay>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Aktivitas Belajar", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium)
            Text("30 Hari Terakhir", style = MaterialTheme.typography.labelSmall, color = PrimaryLight)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                val heatmapMap = heatmap.associate { it.date to it.totalPoints }
                val today = kotlinx.datetime.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                
                repeat(7) { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(vertical = 3.dp)) {
                        repeat(15) { col ->
                            val daysAgo = (14 - col) * 7 + (6 - row)
                            val date = today.minus(daysAgo, DateTimeUnit.DAY).toString()
                            val points = heatmapMap[date] ?: 0
                            val intensity = when {
                                points >= 5 -> 4
                                points >= 3 -> 3
                                points >= 1 -> 2
                                else -> 0
                            }
                            
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(
                                        if (intensity == 0) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) 
                                        else SuccessStreak.copy(alpha = intensity * 0.25f)
                                    )
                            )
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
                    Spacer(modifier = Modifier.width(4.dp))
                    repeat(5) { i ->
                        Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(SuccessStreak.copy(alpha = i * 0.25f + 0.1f)))
                        Spacer(modifier = Modifier.width(2.dp))
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Rajin", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun BadgesSection(tier: AchievementTier) {
    val badges = listOf(
        Badge("Bronze Learner", Icons.Default.EmojiEvents, tier >= AchievementTier.BRONZE, Color(0xFFCD7F32)),
        Badge("Silver Learner", Icons.Default.EmojiEvents, tier >= AchievementTier.SILVER, Color(0xFFC0C0C0)),
        Badge("Gold Learner", Icons.Default.EmojiEvents, tier >= AchievementTier.GOLD, Color(0xFFFFD700)),
        Badge("Master AI", Icons.Default.AutoAwesome, false, AIColorLight)
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Pencapaian (Kuis 30 Hari)", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(badges) { badge ->
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(85.dp)) {
                    Surface(
                        modifier = Modifier
                            .size(70.dp)
                            .shadow(if (badge.isEarned) 4.dp else 0.dp, CircleShape),
                        shape = CircleShape,
                        color = if (badge.isEarned) badge.color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = if (badge.isEarned) BorderStroke(2.dp, badge.color) else null
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                badge.icon, 
                                contentDescription = null, 
                                tint = if (badge.isEarned) badge.color else Color.Gray.copy(alpha = 0.5f),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        badge.name, 
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (badge.isEarned) FontWeight.Bold else FontWeight.Medium,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = if (badge.isEarned) MaterialTheme.colorScheme.onSurface else Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun EditProfileDialog(
    user: com.studymate.domain.model.UserProfile,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(user.localName ?: user.googleName ?: "") }
    var major by remember { mutableStateOf(user.major) }
    var nim by remember { mutableStateOf(user.nim) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profil Aplikasi") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Lengkap") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = major,
                    onValueChange = { major = it },
                    label = { Text("Program Studi") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = nim,
                    onValueChange = { nim = it },
                    label = { Text("NIM") },
                    singleLine = true
                )
                Text(
                    "Data ini hanya berubah di aplikasi StudyMate, tidak mengubah data akun Google Anda.",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSave(name, major, nim) }) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
