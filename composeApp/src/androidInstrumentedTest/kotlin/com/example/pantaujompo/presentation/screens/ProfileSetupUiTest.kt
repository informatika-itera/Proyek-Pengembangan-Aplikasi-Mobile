package com.example.pantaujompo.presentation.screens

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import com.example.pantaujompo.presentation.screens.profile.ProfileSetupScreen
import org.junit.Test
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith

// 🖥️ PROFILE SETUP UI TEST
// Tes ini adalah UI Test (Pengujian Antarmuka) menggunakan bot otomatis.
// Tujuan utamanya:
// 1. Memastikan semua teks wajib seperti "Laki-laki", "Perempuan", dan "Selamat Datang" muncul di layar.
// 2. Menyimulasikan klik tombol jenis kelamin dan memastikan UI tidak *crash*.
@RunWith(AndroidJUnit4::class)
class ProfileSetupUiTest {

    // 1. Memastikan elemen teks awal muncul dengan benar
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun testProfileSetupScreen_initialState() = runComposeUiTest {
        setContent {
            ProfileSetupScreen(onSaveClick = { _, _, _, _, _ -> })
        }
        mainClock.advanceTimeBy(1000L)
        onNodeWithText("Laki-laki").assertExists()
        onNodeWithText("Perempuan").assertExists()
        onNodeWithText("Selamat Datang di\nPantau Jompo.").assertExists()
    }

    // 2. Menyimulasikan klik pada pilihan jenis kelamin "Perempuan"
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun testProfileSetupScreen_genderSelection() = runComposeUiTest {
        setContent {
            ProfileSetupScreen(onSaveClick = { _, _, _, _, _ -> })
        }
        mainClock.advanceTimeBy(1000L)
        onNodeWithText("Perempuan").performClick()
        // Here we just test that the click doesn't crash and works smoothly
        onNodeWithText("Perempuan").assertExists()
    }

    // 3. Memastikan tombol "Mulai Sekarang" ada di layar
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun testProfileSetupScreen_inputHandling() = runComposeUiTest {
        var savedName = ""
        setContent {
            ProfileSetupScreen(onSaveClick = { name, _, _, _, _ -> 
                savedName = name
            })
        }
        mainClock.advanceTimeBy(1000L)
        
        onNodeWithText("Mulai Sekarang").assertExists()
    }
}
