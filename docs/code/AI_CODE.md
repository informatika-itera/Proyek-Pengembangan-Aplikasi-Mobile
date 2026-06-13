# AI Code

## File penting

```text
data/remote/gemini/api/GeminiService.kt
data/remote/gemini/dto/GeminiDto.kt
data/repository/ai/AIRepositoryImpl.kt
domain/ai/repository/AIRepository.kt
presentation/screen/ai/
```

## GeminiService

`GeminiService` membuat request ke endpoint:

```text
https://generativelanguage.googleapis.com/v1beta/models/<modelId>:generateContent
```

Token API dibaca dari:

1. `userPreferences.aiApiToken`
2. fallback `ApiConfig.geminiApiKey`

Jika token kosong, service melempar error bahwa token belum diatur di Settings.

## AIRepositoryImpl

Fitur utama:

- summarize
- improve writing
- generate ideas
- chat assistant

Repository menyusun prompt dan system prompt, lalu memanggil `GeminiService`.

## System prompts

System prompt dibuat agar assistant memahami konteks aplikasi seperti anime, manga, library, gacha, dan notes. Personality dari Settings dapat mengubah gaya jawaban.

## Chat persistence

Chat history disimpan di SQLDelight melalui `AiChatMessageEntity`.

Session key membedakan konteks chat.

## Token usage

Usage metadata dari Gemini dipetakan ke `AiTokenUsageDelta` lalu dicatat melalui `AiTokenUsageRepository`.

## UI integration

`AIAssistantViewModel` mengatur:

- input text
- message list
- loading state
- error state
- session context
- media card result jika respons AI mengandung referensi media
