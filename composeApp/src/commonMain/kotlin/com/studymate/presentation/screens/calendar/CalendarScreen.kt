package com.studymate.presentation.screens.calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studymate.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen() {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Surface(
                modifier = Modifier.shadow(4.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column {
                    CenterAlignedTopAppBar(
                        title = { Text("Planner", fontWeight = FontWeight.Black) },
                        actions = {
                            IconButton(onClick = {}) {
                                Icon(Icons.Default.Notifications, null, tint = PrimaryLight)
                            }
                        }
                    )
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        divider = {},
                        indicator = { tabPositions ->
                            if (selectedTab < tabPositions.size) {
                                Box(
                                    Modifier
                                        .tabIndicatorOffset(tabPositions[selectedTab])
                                        .height(3.dp)
                                        .padding(horizontal = 40.dp)
                                        .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                                        .background(PrimaryLight)
                                )
                            }
                        }
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            selectedContentColor = PrimaryLight,
                            unselectedContentColor = Color.Gray
                        ) {
                            Text("Bulan", modifier = Modifier.padding(14.dp), fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium)
                        }
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            selectedContentColor = PrimaryLight,
                            unselectedContentColor = Color.Gray
                        ) {
                            Text("Minggu", modifier = Modifier.padding(14.dp), fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium)
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Add Task */ },
                containerColor = ActionFABLight,
                contentColor = Color.White,
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Agenda")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            CalendarHeaderSection()
            CalendarGridSection()
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Agenda Hari Ini",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black
                )
                Surface(
                    color = PrimaryLight.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "3 Tugas",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = PrimaryLight,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            TimelineListSection()
        }
    }
}

@Composable
fun CalendarHeaderSection() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                "Mei 2024", 
                fontWeight = FontWeight.Black, 
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "4 Minggu Tersisa", 
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            IconButton(onClick = {}) { Icon(Icons.Default.ChevronLeft, null, modifier = Modifier.size(20.dp)) }
            IconButton(onClick = {}) { Icon(Icons.Default.ChevronRight, null, modifier = Modifier.size(20.dp)) }
        }
    }
}

@Composable
fun CalendarGridSection() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val days = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")
        val dates = listOf(20, 21, 22, 23, 24, 25, 26)
        dates.forEachIndexed { index, date ->
            val isSelected = date == 24
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .then(
                        if (isSelected) {
                            Modifier
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(PrimaryLight, SecondaryLight)
                                    )
                                )
                                .shadow(8.dp, RoundedCornerShape(16.dp))
                        } else {
                            Modifier.background(Color.Transparent)
                        }
                    )
                    .clickable { /* Select Date */ }
                    .padding(vertical = 12.dp)
            ) {
                Text(
                    days[index], 
                    color = if (isSelected) Color.White else Color.Gray, 
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    date.toString(), 
                    fontWeight = FontWeight.Black, 
                    fontSize = 16.sp,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                )
                
                if (date == 22 || date == 26) {
                    Box(
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) Color.White else PrimaryLight.copy(alpha = 0.5f))
                    )
                }
            }
        }
    }
}

@Composable
fun TimelineListSection() {
    val events = listOf(
        TimelineEventData("09:00", "UTS Pemrograman Mobile", "Ruang H.201", "Ujian", Color(0xFFF72585)),
        TimelineEventData("13:00", "Tugas Basis Data", "LMS ITERA", "Tugas", Color(0xFF4CC9F0)),
        TimelineEventData("19:00", "AI Review Sesi 1", "Materi Minggu 4", "Belajar", AIColorLight)
    )
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(events) { event ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.width(50.dp)
                ) {
                    Text(event.time, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Text("AM", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Surface(
                    modifier = Modifier.weight(1f).shadow(2.dp, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, event.color.copy(alpha = 0.2f))
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(40.dp)
                                .clip(CircleShape)
                                .background(event.color)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = event.color.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        event.category,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = event.color,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(event.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text(event.location, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

data class TimelineEventData(
    val time: String, 
    val title: String, 
    val location: String, 
    val category: String,
    val color: Color
)
