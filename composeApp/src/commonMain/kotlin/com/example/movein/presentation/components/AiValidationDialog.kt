package com.example.movein.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.movein.presentation.MentalTheme
import kotlinx.coroutines.delay

@Composable
fun AiValidationDialog(
    message: String,
    theme: MentalTheme,
    isLight: Boolean,
    onDismiss: () -> Unit
) {
    var displayedText by remember { mutableStateOf("") }
    var isTyping by remember { mutableStateOf(true) }

    LaunchedEffect(message) {
        displayedText = ""
        isTyping = true
        message.forEach { char ->
            displayedText += char
            delay(35)
        }
        isTyping = false
    }

    Dialog(onDismissRequest = { if (!isTyping) onDismiss() }) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { if (!isTyping) onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            // Backdrop Blur Simulated by Semi-transparent Box
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (isLight) Color.White.copy(alpha = 0.8f) else Color.Black.copy(alpha = 0.8f))
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clip(RoundedCornerShape(40.dp))
                    .background(if (isLight) Color.White else Color(0xFF111111))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (isLight) Color(0xFFEFF6FF) else theme.glow.copy(alpha = 1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = if (isLight) Color(0xFF3B82F6) else Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "movein AI",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isLight) Color(0xFF171717) else Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isLight) Color(0xFFF9FAFB) else Color.Black.copy(alpha = 0.5f))
                        .padding(16.dp)
                ) {
                    Text(
                        text = buildString {
                            append(displayedText)
                            if (isTyping) append("|")
                        },
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Center,
                        color = if (isLight) Color(0xFF374151) else Color(0xFFD1D5DB),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    enabled = !isTyping,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLight) Color(0xFF171717) else Color.White,
                        contentColor = if (isLight) Color.White else Color.Black
                    ),
                    contentPadding = PaddingValues(vertical = 14.dp)
                ) {
                    Text("Makasih ya", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
