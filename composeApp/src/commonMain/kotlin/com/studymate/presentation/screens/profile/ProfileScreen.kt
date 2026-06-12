package com.studymate.presentation.screens.profile

import androidx.compose.foundation.*
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.studymate.core.util.LocalGoogleAuth
import com.studymate.core.util.rememberImagePickerLauncher
import com.studymate.domain.model.AchievementTier
import com.studymate.domain.model.ActivityDay
import com.studymate.domain.model.Badge
import com.studymate.domain.model.Reminder
import com.studymate.presentation.components.LoadingIndicator
import com.studymate.presentation.theme.*
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.ceil
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas

// We'll use a simple approach for the demo/implementation 
// In a real KMP app, we'd use a library like Peekaboo or implement expect/actual
// For now, I'll add the UI hooks and ViewModel support.

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
    
    val imagePickerLauncher = rememberImagePickerLauncher { path ->
        viewModel.updateProfilePhoto(path)
    }

    if (showEditDialog && uiState.user != null) {
        EditProfileDialog(
            user = uiState.user!!,
            onDismiss = { showEditDialog = false },
            onSave = { name, major, nim, lifeGoals ->
                viewModel.updateProfile(name, major, nim, lifeGoals)
                showEditDialog = false
            },
            onLogout = {
                viewModel.logout()
                showEditDialog = false
            },
            onPickImage = { imagePickerLauncher() },
            onDeleteImage = { viewModel.deleteProfilePhoto() }
        )
    }

    Scaffold(
        containerColor = Color(0xFFF6F7FB),
        topBar = {
            ProfileTopBar(
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle
            )
        },
        bottomBar = {
            // Placeholder for the bottom navigation mentioned in the prompt
            // In a real app, this is often handled by a NavHost scaffold
            StudyMateBottomNavigation(currentRoute = "Profile")
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
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 1. Identitas Profil (Avatar, Nama, Life Goals)
                uiState.user?.let { user ->
                    IdentitySection(
                        name = user.displayName,
                        lifeGoals = user.lifeGoals,
                        photoUrl = user.displayPhoto,
                        onEdit = { showEditDialog = true },
                        onDeleteLifeGoals = {
                            viewModel.updateProfile(user.displayName, user.major, user.nim, "")
                        }
                    )
                }

                // 2. Deadline Card
                ReminderCard(
                    reminder = uiState.closestReminder,
                    timeRemaining = uiState.timeRemaining,
                    onNavigateToPlanner = onNavigateToPlanner
                )

                // 3. Aktivitas Belajar (Heatmap)
                HeatmapSection(uiState.heatmap)

                // 4. Pencapaian
                BadgesSection(uiState.achievementTier)
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar(isDarkTheme: Boolean, onThemeToggle: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                "Profil Saya",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            )
        },
        actions = {
            IconButton(
                onClick = onThemeToggle,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(40.dp)
                    .background(Color(0xFF9B3BE0).copy(alpha = 0.1f), CircleShape)
            ) {
                Icon(
                    imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Theme",
                    tint = Color(0xFF9B3BE0),
                    modifier = Modifier.size(20.dp)
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
    onEdit: () -> Unit,
    onDeleteLifeGoals: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Avatar with overlapping edit icon
        Box(contentAlignment = Alignment.BottomEnd) {
            Surface(
                modifier = Modifier.size(110.dp),
                shape = CircleShape,
                color = Color(0xFFEDEFF7),
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
            ) {
                if (!photoUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = "Profile Photo",
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = null,
                        modifier = Modifier.padding(28.dp),
                        tint = Color(0xFFBDBDBD)
                    )
                }
            }
            // Small edit pencil icon in bottom right
            Surface(
                modifier = Modifier
                    .size(32.dp)
                    .offset(x = 2.dp, y = 2.dp)
                    .clickable { onEdit() },
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Profile",
                    tint = Color(0xFF9B3BE0),
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Name with Edit icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onEdit() }
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Name",
                tint = Color.Gray.copy(alpha = 0.6f),
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Life Goals Ribbon
        LifeGoalsRibbon(
            lifeGoals = lifeGoals,
            onEdit = onEdit,
            onDelete = onDeleteLifeGoals
        )
    }
}

@Composable
fun LifeGoalsRibbon(
    lifeGoals: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val ribbonGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFFFF5FA0), Color(0xFF9B3BE0))
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        contentAlignment = Alignment.Center
    ) {
        // The Ribbon Body (Pita)
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(42.dp),
            shape = RoundedCornerShape(2.dp), // Straight ribbon look
            shadowElevation = 6.dp
        ) {
            Row(
                modifier = Modifier
                    .background(ribbonGradient)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (lifeGoals.isNotBlank()) "Live Goals: $lifeGoals" else "Live Goals: Lulus IPK 3.8+",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                // Action Icons (Edit & Delete)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StudyMateBottomNavigation(currentRoute: String) {
    val items = listOf(
        "Home" to Icons.Default.Home,
        "Notes" to Icons.Default.Notes,
        "Quiz" to Icons.Default.Quiz,
        "Planner" to Icons.Default.CalendarMonth,
        "Profile" to Icons.Default.Person
    )

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        items.forEach { (label, icon) ->
            val isActive = currentRoute == label
            NavigationBarItem(
                selected = isActive,
                onClick = { /* Handle navigation */ },
                label = {
                    Text(
                        label,
                        color = if (isActive) Color(0xFF9B3BE0) else Color.Gray,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                    )
                },
                icon = {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isActive) Color(0xFF9B3BE0).copy(alpha = 0.15f) 
                                else Color.Transparent
                            )
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            icon,
                            contentDescription = label,
                            tint = if (isActive) Color(0xFF9B3BE0) else Color.Gray
                        )
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent // We use our own pill background
                )
            )
        }
    }
}

@Composable
fun ReminderCard(
    reminder: Reminder?, 
    timeRemaining: String, 
    onNavigateToPlanner: (kotlinx.datetime.LocalDate?) -> Unit
) {
    if (reminder == null) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.horizontalGradient(colors = listOf(Color(0xFFFF007F), Color(0xFF8B00FF))),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(50.dp), shape = CircleShape, color = Color.White.copy(alpha = 0.2f)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text("Semua tugas selesai!", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(12.dp, RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFFFF007F), Color(0xFF8B00FF))
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .clickable { 
                    val date = kotlinx.datetime.Instant.fromEpochMilliseconds(reminder.dueDate)
                        .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date
                    onNavigateToPlanner(date) 
                }
                .padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Circle with ! (As in sketch)
                Surface(
                    modifier = Modifier.size(60.dp),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("!", color = Color.White, fontWeight = FontWeight.Black, fontSize = 32.sp)
                    }
                }
                
                Spacer(modifier = Modifier.width(20.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        reminder.title.uppercase(), 
                        fontWeight = FontWeight.Bold, 
                        color = Color.White.copy(alpha = 0.8f), 
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        "DL akhir", // Specifically requested "DL akhir" look
                        style = MaterialTheme.typography.headlineSmall, 
                        color = Color.White, 
                        fontWeight = FontWeight.Black
                    )
                }
                
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        timeRemaining,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        fontWeight = FontWeight.Black, 
                        color = Color(0xFFE91E63),
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
            Text("Kontribusi Kualitas", style = MaterialTheme.typography.labelSmall, color = PrimaryLight)
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
                
                // GitHub style: 7 rows (Sunday to Saturday) x 15-20 columns (weeks)
                // We show roughly 18 weeks to fill the width nicely
                val columns = 18
                val totalDays = columns * 7
                val startDate = today.minus(totalDays - 1, DateTimeUnit.DAY)

                Column(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(7) { row -> // Days of week
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            repeat(columns) { col -> // Weeks
                                val daysToAdd = col * 7 + row
                                val currentDate = startDate.plus(daysToAdd.toLong(), DateTimeUnit.DAY)
                                val points = heatmapMap[currentDate.toString()] ?: 0
                                
                                val intensity = when {
                                    points >= 10 -> 4
                                    points >= 5 -> 3
                                    points >= 3 -> 2
                                    points >= 1 -> 1
                                    else -> 0
                                }
                                
                                val boxColor = when (intensity) {
                                    4 -> SuccessStreak // Darkest
                                    3 -> SuccessStreak.copy(alpha = 0.7f)
                                    2 -> SuccessStreak.copy(alpha = 0.4f)
                                    1 -> SuccessStreak.copy(alpha = 0.2f)
                                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                }

                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(boxColor)
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Kurang", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Spacer(modifier = Modifier.width(6.dp))
                    listOf(0, 1, 2, 3, 4).forEach { level ->
                        val color = when (level) {
                            4 -> SuccessStreak
                            3 -> SuccessStreak.copy(alpha = 0.7f)
                            2 -> SuccessStreak.copy(alpha = 0.4f)
                            1 -> SuccessStreak.copy(alpha = 0.2f)
                            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        }
                        Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(color))
                        Spacer(modifier = Modifier.width(3.dp))
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
    onSave: (String, String, String, String) -> Unit,
    onLogout: () -> Unit,
    onPickImage: () -> Unit,
    onDeleteImage: () -> Unit
) {
    var name by remember { mutableStateOf(user.localName ?: user.googleName ?: "") }
    var major by remember { mutableStateOf(user.major) }
    var nim by remember { mutableStateOf(user.nim) }
    var lifeGoals by remember { mutableStateOf(user.lifeGoals) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profil Aplikasi") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Profile Photo Section in Dialog
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            Surface(
                                modifier = Modifier.size(80.dp),
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                border = BorderStroke(2.dp, PrimaryLight)
                            ) {
                                if (user.displayPhoto != null) {
                                    AsyncImage(
                                        model = user.displayPhoto,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize().clip(CircleShape)
                                    )
                                } else {
                                    Icon(Icons.Default.Person, null, modifier = Modifier.size(40.dp))
                                }
                            }
                            IconButton(
                                onClick = onPickImage,
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(PrimaryLight, CircleShape)
                                    .padding(4.dp)
                            ) {
                                Icon(Icons.Default.PhotoCamera, null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                        
                        if (user.displayPhoto != null) {
                            TextButton(
                                onClick = onDeleteImage,
                                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("Hapus Foto", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Lengkap") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = major,
                    onValueChange = { major = it },
                    label = { Text("Program Studi") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = nim,
                    onValueChange = { nim = it },
                    label = { Text("NIM") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = lifeGoals,
                    onValueChange = { lifeGoals = it },
                    label = { Text("Life Goals") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                
                TextButton(
                    onClick = onLogout,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Keluar dari Akun")
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSave(name, major, nim, lifeGoals) }) {
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
