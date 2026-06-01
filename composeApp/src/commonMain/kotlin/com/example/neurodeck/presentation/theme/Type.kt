package com.example.neurodeck.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import neurodeck.composeapp.generated.resources.Res
import neurodeck.composeapp.generated.resources.bricolage_grotesque_bold
import neurodeck.composeapp.generated.resources.bricolage_grotesque_medium
import neurodeck.composeapp.generated.resources.bricolage_grotesque_regular
import neurodeck.composeapp.generated.resources.bricolage_grotesque_semibold
import neurodeck.composeapp.generated.resources.jetbrains_mono_medium
import neurodeck.composeapp.generated.resources.jetbrains_mono_regular
import org.jetbrains.compose.resources.Font

// ════════════════════════════════════════════════════════════════════════════
// NeuroDeck Typography System
//
// 2 font families:
//   - Bricolage Grotesque  → headlines, body, labels (display & content)
//   - JetBrains Mono       → numerical data, code, tabular labels
//
// Font files harus tersedia di:
//   composeApp/src/commonMain/composeResources/font/
//     - bricolage_grotesque_regular.ttf
//     - bricolage_grotesque_medium.ttf
//     - bricolage_grotesque_semibold.ttf
//     - bricolage_grotesque_bold.ttf
//     - jetbrains_mono_regular.ttf
//     - jetbrains_mono_medium.ttf
//
// Note: nama file di composeResources HARUS lowercase + underscore — class
// Res yang ter-generate akan camelCase otomatis.
//
// FALLBACK STRATEGY: kalau font file tidak ada di runtime,
// Compose akan throw IllegalStateException saat Font() di-resolve.
// Solusi: gunakan FontFamily.Default sebagai fallback (bukan crash).
// Tapi ini bisa di-handle hanya kalau Font() di-wrap try-catch — yang
// tidak mudah karena @Composable. Trade-off: pastikan font files ADA
// di folder sebelum build, atau revert ke FontFamily.Default seluruhnya.
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun bricolageGrotesqueFontFamily(): FontFamily = FontFamily(
    Font(
        resource = Res.font.bricolage_grotesque_regular,
        weight = FontWeight.Normal,
    ),
    Font(
        resource = Res.font.bricolage_grotesque_medium,
        weight = FontWeight.Medium,
    ),
    Font(
        resource = Res.font.bricolage_grotesque_semibold,
        weight = FontWeight.SemiBold,
    ),
    Font(
        resource = Res.font.bricolage_grotesque_bold,
        weight = FontWeight.Bold,
    ),
)

@Composable
private fun jetBrainsMonoFontFamily(): FontFamily = FontFamily(
    Font(
        resource = Res.font.jetbrains_mono_regular,
        weight = FontWeight.Normal,
    ),
    Font(
        resource = Res.font.jetbrains_mono_medium,
        weight = FontWeight.Medium,
    ),
)

/**
 * Build complete [Typography] dengan custom fonts.
 *
 * @Composable karena Font() butuh ComposableContext untuk resolve resources.
 * Dipanggil dari Theme.kt sebagai parameter MaterialTheme(typography = ...).
 */
@Composable
fun neuroDeckTypography(): Typography {
    val bricolage = bricolageGrotesqueFontFamily()
    val jetbrainsMono = jetBrainsMonoFontFamily()

    return Typography(
        // ════════════════════════════════════════════════════════════════════
        // DISPLAY — biggest text (splash, hero headlines)
        // ════════════════════════════════════════════════════════════════════
        displayLarge = TextStyle(
            fontFamily = bricolage,
            fontWeight = FontWeight.Bold,
            fontSize = 57.sp,
            lineHeight = 64.sp,
            letterSpacing = (-0.25).sp,
        ),
        displayMedium = TextStyle(
            fontFamily = bricolage,
            fontWeight = FontWeight.Bold,
            fontSize = 45.sp,
            lineHeight = 52.sp,
        ),
        displaySmall = TextStyle(
            fontFamily = bricolage,
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp,
            lineHeight = 44.sp,
        ),

        // ════════════════════════════════════════════════════════════════════
        // HEADLINE — section titles, big numbers
        // ════════════════════════════════════════════════════════════════════
        headlineLarge = TextStyle(
            fontFamily = bricolage,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            lineHeight = 40.sp,
        ),
        headlineMedium = TextStyle(
            fontFamily = bricolage,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            lineHeight = 36.sp,
        ),
        headlineSmall = TextStyle(
            fontFamily = bricolage,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
            lineHeight = 32.sp,
        ),

        // ════════════════════════════════════════════════════════════════════
        // TITLE — card headers, dialog titles
        // ════════════════════════════════════════════════════════════════════
        titleLarge = TextStyle(
            fontFamily = bricolage,
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
            lineHeight = 28.sp,
        ),
        titleMedium = TextStyle(
            fontFamily = bricolage,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.15.sp,
        ),
        titleSmall = TextStyle(
            fontFamily = bricolage,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp,
        ),

        // ════════════════════════════════════════════════════════════════════
        // BODY — main content text
        // ════════════════════════════════════════════════════════════════════
        bodyLarge = TextStyle(
            fontFamily = bricolage,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp,
        ),
        bodyMedium = TextStyle(
            fontFamily = bricolage,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp,
        ),
        bodySmall = TextStyle(
            fontFamily = bricolage,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.4.sp,
        ),

        // ════════════════════════════════════════════════════════════════════
        // LABEL — JetBrains Mono untuk numerical/tabular text
        // (Vivid Logic spec: Label pakai JetBrains Mono)
        // ════════════════════════════════════════════════════════════════════
        labelLarge = TextStyle(
            fontFamily = jetbrainsMono,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp,
        ),
        labelMedium = TextStyle(
            fontFamily = jetbrainsMono,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp,
        ),
        labelSmall = TextStyle(
            fontFamily = jetbrainsMono,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp,
        ),
    )
}