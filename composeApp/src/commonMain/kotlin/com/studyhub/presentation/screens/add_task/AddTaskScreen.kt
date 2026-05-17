package com.studyhub.presentation.screens.add_task

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studyhub.presentation.theme.*
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    taskId: Long = -1L,
    onNavigateBack: () -> Unit,
    viewModel: AddTaskViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    val categories = listOf("Mobile App", "Website", "Design", "Study")
    val colors = listOf(PastelOrange, PastelGreen, PastelPurple, PastelBlue)

    LaunchedEffect(taskId) {
        viewModel.loadTask(taskId)
    }

    LaunchedEffect(viewModel.eventFlow) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is AddTaskEvent.TaskSaved -> onNavigateBack()
                is AddTaskEvent.Error -> {
                    // In a real app, show a snackbar here
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (taskId == -1L) "Add New Task" else "Edit Task", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppWhite)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = state.title,
                onValueChange = { viewModel.onTitleChange(it) },
                label = { Text("Task Title") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            OutlinedTextField(
                value = state.description,
                onValueChange = { viewModel.onDescriptionChange(it) },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                shape = RoundedCornerShape(16.dp)
            )

            Text("Category", fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = state.category == cat,
                        onClick = { viewModel.onCategoryChange(cat) },
                        label = { Text(cat) }
                    )
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = state.startTime,
                    onValueChange = { viewModel.onStartTimeChange(it) },
                    label = { Text("Start Time") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp)
                )
                OutlinedTextField(
                    value = state.endTime,
                    onValueChange = { viewModel.onEndTimeChange(it) },
                    label = { Text("End Time") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp)
                )
            }

            Text("Select Color", fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                colors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(color, RoundedCornerShape(8.dp))
                            .clickable { viewModel.onColorChange(color.value.toLong()) }
                            .padding(4.dp)
                    ) {
                        if (state.colorHex == color.value.toLong()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(AppBlack.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                            )
                        }
                    }
                }
            }

            if (taskId != -1L) {
                Text("Progress: ${state.progress}%", fontWeight = FontWeight.Bold)
                Slider(
                    value = state.progress.toFloat(),
                    onValueChange = { viewModel.onProgressChange(it.toInt()) },
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(thumbColor = AppBlack, activeTrackColor = AppBlack)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.saveTask() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppBlack)
            ) {
                Text(if (taskId == -1L) "Save Task" else "Update Task", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    }
}
