package com.studymate.presentation.screens.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.material3.MenuAnchorType
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studymate.domain.model.Note
import com.studymate.presentation.theme.PrimaryLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedQuizScreen(
    viewModel: QuizViewModel,
    onNavigateBack: () -> Unit,
    onStartQuiz: (String, List<Note>, Int) -> Unit
) {
    val allNotes by viewModel.notes.collectAsState()
    val allSubjects by viewModel.allSubjects.collectAsState()
    
    var selectedSubject by remember { mutableStateOf("") }
    var selectedNotes by remember { mutableStateOf(setOf<Note>()) }
    var questionCount by remember { mutableStateOf(25f) }
    
    val filteredNotes = remember(allNotes, selectedSubject) {
        allNotes.filter { it.subject == selectedSubject }
            .sortedByDescending { it.updatedAt }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text("Advanced Quiz", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali")
                    }
                }
            )
        },
        bottomBar = {
            if (selectedNotes.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Jumlah Soal: ${questionCount.toInt()}", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.weight(1f))
                            Text("Min 15, Max 50", style = MaterialTheme.typography.bodySmall)
                        }
                        Slider(
                            value = questionCount,
                            onValueChange = { questionCount = it },
                            valueRange = 15f..50f,
                            steps = 34
                        )
                        Button(
                            onClick = { onStartQuiz(selectedSubject, selectedNotes.toList(), questionCount.toInt()) },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Mulai Quiz Gabungan (${selectedNotes.size})", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            Text("Pilih Mata Kuliah", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            
            // Subject Dropdown/Selection
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = selectedSubject,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Pilih Matkul...") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    allSubjects.forEach { subject ->
                        DropdownMenuItem(
                            text = { Text(subject) },
                            onClick = {
                                selectedSubject = subject
                                selectedNotes = emptySet()
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (selectedSubject.isNotBlank()) {
                Text(
                    "Pilih Materi (${selectedNotes.size} terpilih)", 
                    style = MaterialTheme.typography.titleMedium, 
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(filteredNotes) { note ->
                        val isSelected = selectedNotes.contains(note)
                        AdvancedNoteItem(
                            note = note,
                            isSelected = isSelected,
                            onClick = {
                                selectedNotes = if (isSelected) selectedNotes - note else selectedNotes + note
                            }
                        )
                    }
                }
            } else {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.School, null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                        Text("Pilih mata kuliah untuk melihat materi", color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
private fun AdvancedNoteItem(
    note: Note,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) PrimaryLight else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PrimaryLight.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(note.title, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    note.rawContent, 
                    style = MaterialTheme.typography.bodySmall, 
                    maxLines = 1, 
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) PrimaryLight else Color.LightGray.copy(alpha = 0.3f))
                    .border(1.dp, if (isSelected) PrimaryLight else Color.Gray, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
