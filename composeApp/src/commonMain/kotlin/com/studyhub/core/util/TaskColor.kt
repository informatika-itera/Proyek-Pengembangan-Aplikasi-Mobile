package com.studyhub.core.util

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object TaskColor {
    // Warm, muted palette that works on both light/dark
    private val palette = listOf(
        "#E8A87C",   // warm orange
        "#85C1A3",   // sage green
        "#7EB5D6",   // soft blue
        "#C4A0D4",   // lavender
        "#F0C47E",   // golden yellow
        "#E89090",   // soft coral/red
        "#84B5C4",   // steel blue
        "#A8C97A",   // fresh green
        "#D4A5C9",   // dusty pink
        "#9BC4B8",   // mint teal
        "#F4A96A",   // peach
        "#8BB8D4"    // sky blue
    )

    fun random(): String = palette.random()

    fun fromHex(hex: String): Color {
        return try {
            val colorStr = hex.removePrefix("#")
            val argb = when (colorStr.length) {
                6 -> (0xFF000000 or colorStr.toLong(16)).toInt()
                8 -> colorStr.toLong(16).toInt()
                else -> 0xFF85C1A3.toInt()
            }
            Color(argb)
        } catch (e: Exception) {
            Color(0xFF85C1A3.toInt())
        }
    }

    // Get a readable text color for given background
    fun textColorFor(hex: String): Color {
        return try {
            val color = fromHex(hex)
            val r = color.red
            val g = color.green
            val b = color.blue
            val luminance = 0.299 * r + 0.587 * g + 0.114 * b
            if (luminance > 0.6) Color(0xFF2C2C2C)
            else Color.White
        } catch (e: Exception) {
            Color.White
        }
    }

    // Get lighter container version of color
    fun containerColorFor(hex: String, isDark: Boolean): Color {
        return try {
            val color = fromHex(hex)
            if (isDark) {
                // Darker, more saturated for dark mode
                color.copy(alpha = 0.2f)
            } else {
                // Very light tint for light mode
                color.copy(alpha = 0.15f)
            }
        } catch (e: Exception) {
            Color(0xFFE8F5E9)
        }
    }
}

// Compose-friendly extension
@Composable
fun String.toTaskColor(): Color = TaskColor.fromHex(this)

@Composable
fun String.toTaskContainerColor(): Color =
    TaskColor.containerColorFor(this, isSystemInDarkTheme())
