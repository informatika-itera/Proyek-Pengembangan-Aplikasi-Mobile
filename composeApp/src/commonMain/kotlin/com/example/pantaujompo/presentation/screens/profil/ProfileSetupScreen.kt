package com.example.pantaujompo.presentation.screens.profile

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.ErrorOutline
import com.example.pantaujompo.presentation.theme.MeshBackground
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    onSaveClick: (String, Int, Float, Float, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Laki-laki") } // Laki-laki / Perempuan

    var isVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    LaunchedEffect(Unit) {
        delay(150)
        isVisible = true
    }

    MeshBackground(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(800)) + slideInVertically(
                initialOffsetY = { it / 4 }, 
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
            ),
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(top = 64.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Header (Left Aligned, Modern Magazine Style)
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Color(0xFF00E676), Color(0xFF00B359))))
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF121212)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsRun,
                        contentDescription = "Logo PantauJompo",
                        tint = Color(0xFF00E676),
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Selamat Datang di\nPantau Jompo.",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 38.sp
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Bantu AI kami memahami fisikmu agar program olahraga & nutrisi berjalan sempurna.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // Error Message Banner
                AnimatedVisibility(visible = errorMessage != null, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFF5252).copy(alpha = 0.15f))
                            .border(1.dp, Color(0xFFFF5252).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = "Error", tint = Color(0xFFFF5252), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = errorMessage ?: "", color = Color(0xFFFF5252), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                // Scrollable content for fields
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val textFieldColors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00E676),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                        focusedContainerColor = Color(0xFF151515).copy(alpha = 0.8f),
                        unfocusedContainerColor = Color(0xFF151515).copy(alpha = 0.5f)
                    )
                    val textFieldShape = RoundedCornerShape(16.dp)

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it; errorMessage = null },
                        label = { Text("Nama Panggilan") },
                        colors = textFieldColors,
                        modifier = Modifier.fillMaxWidth(),
                        shape = textFieldShape,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            capitalization = KeyboardCapitalization.Words
                        )
                    )

                    OutlinedTextField(
                        value = age,
                        onValueChange = { age = it; errorMessage = null },
                        label = { Text("Usia (Tahun)") },
                        colors = textFieldColors,
                        modifier = Modifier.fillMaxWidth(),
                        shape = textFieldShape,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = weight,
                            onValueChange = { weight = it; errorMessage = null },
                            label = { Text("Berat (kg)") },
                            colors = textFieldColors,
                            modifier = Modifier.weight(1f),
                            shape = textFieldShape,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        OutlinedTextField(
                            value = height,
                            onValueChange = { height = it; errorMessage = null },
                            label = { Text("Tinggi (cm)") },
                            colors = textFieldColors,
                            modifier = Modifier.weight(1f),
                            shape = textFieldShape,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }

                    // Gender Selection
                    Text("Jenis Kelamin", color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        GenderButton(
                            text = "Laki-laki",
                            isSelected = gender == "Laki-laki",
                            onClick = { gender = "Laki-laki" },
                            modifier = Modifier.weight(1f)
                        )
                        GenderButton(
                            text = "Perempuan",
                            isSelected = gender == "Perempuan",
                            onClick = { gender = "Perempuan" },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Tombol Simpan (Glass Button)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .scale(buttonScale)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Brush.linearGradient(listOf(Color(0xFF00E676).copy(alpha = 0.8f), Color(0xFF00BCD4).copy(alpha = 0.6f)))) // Green & Cyan accent glass
                        .border(1.dp, Color.White.copy(alpha=0.5f), RoundedCornerShape(20.dp))
                ) {
                    Button(
                        onClick = {
                            if (name.isBlank() || age.isBlank() || weight.isBlank() || height.isBlank()) {
                                errorMessage = "Mohon lengkapi semua data profil."
                            } else {
                                onSaveClick(
                                    name,
                                    age.toIntOrNull() ?: 0,
                                    weight.toFloatOrNull() ?: 0f,
                                    height.toFloatOrNull() ?: 0f,
                                    gender
                                )
                            }
                        },
                        interactionSource = interactionSource,
                        modifier = Modifier.fillMaxSize(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Mulai Sekarang", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun GenderButton(text: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val bgColor = if (isSelected) Color(0xFF00E676).copy(alpha = 0.2f) else Color(0xFF151515).copy(alpha = 0.5f)
    val borderColor = if (isSelected) Color(0xFF00E676) else Color.White.copy(alpha = 0.15f)
    val textColor = if (isSelected) Color(0xFF00E676) else Color.Gray

    Box(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = textColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}