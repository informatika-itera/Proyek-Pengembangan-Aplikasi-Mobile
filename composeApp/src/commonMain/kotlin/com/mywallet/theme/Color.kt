package com.mywallet.theme

import androidx.compose.ui.graphics.Color

// Modern & Vibrant Palette (Indigo/Violet Dominant)
val PalettePrimary = Color(0xFF6366F1) // Indigo 500
val PalettePrimaryDark = Color(0xFF4F46E5) // Indigo 600
val PaletteSecondary = Color(0xFF8B5CF6) // Violet 500
val PaletteSuccess = Color(0xFF10B981) // Emerald 500 (Income)
val PaletteDanger = Color(0xFFEF4444) // Red 500 (Expense)

val PaletteLightBackground = Color(0xFFF8FAFC) // Slate 50
val PaletteLightSurface = Color(0xFFFFFFFF)
val PaletteLightOnSurface = Color(0xFF0F172A) // Slate 900

val PaletteDarkBackground = Color(0xFF0F172A) // Slate 900
val PaletteDarkSurface = Color(0xFF1E293B) // Slate 800
val PaletteDarkOnSurface = Color(0xFFF1F5F9) // Slate 100

// Material 3 Color Mappings - Light
val primaryLight = PalettePrimary
val onPrimaryLight = Color.White
val primaryContainerLight = Color(0xFFE0E7FF)
val onPrimaryContainerLight = PalettePrimaryDark

val secondaryLight = PaletteSecondary
val onSecondaryLight = Color.White
val secondaryContainerLight = Color(0xFFEDE9FE)
val onSecondaryContainerLight = Color(0xFF7C3AED)

val errorLight = PaletteDanger
val onErrorLight = Color.White
val errorContainerLight = Color(0xFFFEE2E2)
val onErrorContainerLight = Color(0xFF991B1B)

val backgroundLight = PaletteLightBackground
val onBackgroundLight = PaletteLightOnSurface
val surfaceLight = PaletteLightSurface
val onSurfaceLight = PaletteLightOnSurface

// Material 3 Color Mappings - Dark
val primaryDark = Color(0xFF818CF8)
val onPrimaryDark = Color(0xFF1E1B4B)
val primaryContainerDark = Color(0xFF312E81)
val onPrimaryContainerDark = Color(0xFFE0E7FF)

val secondaryDark = Color(0xFFA78BFA)
val onSecondaryDark = Color(0xFF2E1065)
val secondaryContainerDark = Color(0xFF4C1D95)
val onSecondaryContainerDark = Color(0xFFEDE9FE)

val errorDark = Color(0xFFF87171)
val onErrorDark = Color(0xFF450A0A)
val errorContainerDark = Color(0xFF7F1D1D)
val onErrorContainerDark = Color(0xFFFEE2E2)

val backgroundDark = PaletteDarkBackground
val onBackgroundDark = PaletteDarkOnSurface
val surfaceDark = PaletteDarkSurface
val onSurfaceDark = PaletteDarkOnSurface
