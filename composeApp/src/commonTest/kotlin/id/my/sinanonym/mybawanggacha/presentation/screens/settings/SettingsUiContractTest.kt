package id.my.sinanonym.mybawanggacha.presentation.screens.settings

import id.my.sinanonym.mybawanggacha.domain.settings.model.AiApiModel
import id.my.sinanonym.mybawanggacha.domain.settings.model.AppColorScheme
import id.my.sinanonym.mybawanggacha.domain.settings.model.NetworkMode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import id.my.sinanonym.mybawanggacha.domain.settings.model.AiPersonality

class SettingsUiContractTest {
    @Test
    fun networkMode_fromString_shouldFallbackToAuto() {
        assertEquals(NetworkMode.Auto, NetworkMode.fromString(null))
        assertEquals(NetworkMode.Auto, NetworkMode.fromString("broken"))
        assertEquals(NetworkMode.OfflineOnly, NetworkMode.fromString("offlineonly"))
    }

    @Test
    fun networkMode_allowsNetwork_shouldMatchMode() {
        assertTrue(NetworkMode.Auto.allowsNetwork)
        assertFalse(NetworkMode.OfflineOnly.allowsNetwork)
    }

    @Test
    fun aiApiModel_fromString_shouldFallbackToGemini35Flash() {
        assertEquals(AiApiModel.Gemini35Flash, AiApiModel.fromString(null))
        assertEquals(AiApiModel.Gemini35Flash, AiApiModel.fromString("broken"))
        assertEquals(AiApiModel.Gemini25Flash, AiApiModel.fromString("gemini-2.5-flash"))
        assertEquals(AiApiModel.Gemini25Pro, AiApiModel.fromString("gemini25pro"))
    }

    @Test
    fun appColorScheme_fromString_shouldFallbackToCodeGeass() {
        assertEquals(AppColorScheme.CodeGeass, AppColorScheme.fromString(null))
        assertEquals(AppColorScheme.CodeGeass, AppColorScheme.fromString("broken"))
        assertEquals(AppColorScheme.PakHabib, AppColorScheme.fromString("pakhabib"))
        assertEquals(AppColorScheme.Gruvbox, AppColorScheme.fromString("gruvbox"))
        assertEquals(AppColorScheme.Catppuccin, AppColorScheme.fromString("catppuccin"))
        assertEquals(AppColorScheme.HatsuneMiku, AppColorScheme.fromString("hatsunemiku"))
    }
    @Test
    fun aiPersonality_fromString_shouldFallbackToDefault() {
        assertEquals(AiPersonality.Default, AiPersonality.fromString(null))
        assertEquals(AiPersonality.Default, AiPersonality.fromString("broken"))
        assertEquals(AiPersonality.Kuudere, AiPersonality.fromString("kuudere"))
        assertEquals(AiPersonality.Tsundere, AiPersonality.fromString("Tsundere"))
        assertEquals(AiPersonality.OtakuFriend, AiPersonality.fromString("Otaku Friend"))
    }

}
