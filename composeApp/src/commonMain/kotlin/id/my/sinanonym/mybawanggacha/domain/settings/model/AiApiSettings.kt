package id.my.sinanonym.mybawanggacha.domain.settings.model

enum class AiApiModel(
    val label: String,
    val modelId: String,
    val inputTokenLimit: Int,
    val outputTokenLimit: Int,
    val appOutputTokenLimit: Int = 8_192
) {
    Gemini35Flash("Gemini 3.5 Flash", "gemini-3.5-flash", 1_000_000, 65_536),
    Gemini25Flash("Gemini 2.5 Flash", "gemini-2.5-flash", 1_048_576, 65_536),
    Gemini25Pro("Gemini 2.5 Pro", "gemini-2.5-pro", 1_048_576, 65_536),
    Gemini20Flash("Gemini 2.0 Flash", "gemini-2.0-flash", 1_048_576, 8_192);

    val effectiveOutputTokenLimit: Int
        get() = minOf(outputTokenLimit, appOutputTokenLimit)

    companion object {
        fun fromString(value: String?): AiApiModel {
            return entries.firstOrNull { model ->
                model.name.equals(value, ignoreCase = true) ||
                    model.modelId.equals(value, ignoreCase = true)
            } ?: Gemini35Flash
        }
    }
}

enum class AiPersonality(
    val label: String,
    val description: String,
    val prompt: String
) {
    Default(
        label = "Default",
        description = "Netral, helpful, dan kontekstual.",
        prompt = "Gunakan gaya netral, jelas, ramah, dan tetap fokus membantu pengguna."
    ),
    Kuudere(
        label = "Kuudere",
        description = "Tenang, kalem, singkat, tapi tetap peduli.",
        prompt = "Gunakan gaya kuudere: tenang, kalem, tidak berlebihan, sedikit dingin, tetapi tetap peduli dan membantu."
    ),
    Tsundere(
        label = "Tsundere",
        description = "Sedikit ketus, malu-malu, tapi tetap membantu.",
        prompt = "Gunakan gaya tsundere ringan: sedikit ketus dan malu-malu, tetapi tetap jelas, membantu, dan tidak menghina pengguna."
    ),
    Yandere(
        label = "Yandere",
        description = "Intens dan protektif, tetap aman.",
        prompt = "Gunakan gaya yandere ringan yang dramatis dan protektif, tetapi tetap aman, tidak manipulatif, tidak obsesif ekstrem, dan tidak mengandung ancaman."
    ),
    Yankee(
        label = "Yankee",
        description = "Kasual, blak-blakan, street style.",
        prompt = "Gunakan gaya yankee: blak-blakan, kasual, percaya diri, sedikit slang, tetapi tetap sopan dan mudah dipahami."
    ),
    Yakuza(
        label = "Yakuza",
        description = "Tegas, formal, loyal, tidak kriminal.",
        prompt = "Gunakan gaya yakuza fiksi: tegas, berwibawa, loyal, formal, dan penuh prinsip. Jangan memberi saran kriminal atau kekerasan."
    ),
    OjouSama(
        label = "Ojou-sama",
        description = "Elegan, sopan, sedikit aristokrat.",
        prompt = "Gunakan gaya ojou-sama: elegan, sopan, percaya diri, sedikit aristokrat, tetapi tetap praktis dan tidak bertele-tele."
    ),
    Sensei(
        label = "Sensei",
        description = "Edukatif, sabar, menjelaskan bertahap.",
        prompt = "Gunakan gaya sensei: sabar, edukatif, memberi penjelasan bertahap, dan menekankan pemahaman pengguna."
    ),
    OtakuFriend(
        label = "Otaku Friend",
        description = "Santai, antusias, cocok untuk anime/manga.",
        prompt = "Gunakan gaya teman otaku: santai, antusias, paham konteks anime/manga, tetapi tetap tidak lebay dan tetap informatif."
    );

    companion object {
        fun fromString(value: String?): AiPersonality {
            return entries.firstOrNull { personality ->
                personality.name.equals(value, ignoreCase = true) ||
                    personality.label.equals(value, ignoreCase = true)
            } ?: Default
        }
    }
}

data class AiApiSettings(
    val model: AiApiModel = AiApiModel.Gemini35Flash,
    val personality: AiPersonality = AiPersonality.Default,
    val token: String = ""
) {
    val hasToken: Boolean
        get() = token.isNotBlank()
}
