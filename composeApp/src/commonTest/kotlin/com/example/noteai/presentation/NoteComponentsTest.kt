package com.example.noteai.presentation

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import com.example.noteai.domain.model.VulnSeverity
import com.example.noteai.domain.model.VulnStatus
import com.example.noteai.presentation.components.CategoryBadge
import com.example.noteai.presentation.components.EmptyState
import com.example.noteai.presentation.components.SeverityBadge
import com.example.noteai.presentation.components.StatusBadge
import kotlin.test.Test

/**
 * UI Tests untuk Komponen UI VulnLog menggunakan Compose Multiplatform UI Testing API
 */
@OptIn(ExperimentalTestApi::class)
class NoteComponentsTest {

    @Test
    fun emptyState_showsCorrectTitleAndMessage() = runComposeUiTest {
        setContent {
            EmptyState(
                title = "Belum Ada Temuan",
                message = "Silakan tambah temuan baru"
            )
        }

        onNodeWithText("Belum Ada Temuan").assertIsDisplayed()
        onNodeWithText("Silakan tambah temuan baru").assertIsDisplayed()
    }

    @Test
    fun severityBadge_showsCorrectText() = runComposeUiTest {
        setContent {
            SeverityBadge(severity = VulnSeverity.CRITICAL)
        }

        onNodeWithText("CRITICAL").assertIsDisplayed()
    }

    @Test
    fun statusBadge_showsCorrectText() = runComposeUiTest {
        setContent {
            StatusBadge(status = VulnStatus.PAID)
        }

        onNodeWithText("Paid").assertIsDisplayed()
    }

    @Test
    fun categoryBadge_showsCorrectText() = runComposeUiTest {
        setContent {
            CategoryBadge(category = "Cross-Site Scripting (XSS)")
        }

        onNodeWithText("Cross-Site Scripting (XSS)").assertIsDisplayed()
    }
}
