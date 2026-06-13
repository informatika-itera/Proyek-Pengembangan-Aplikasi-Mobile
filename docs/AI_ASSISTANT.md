# AI Assistant

AI Assistant memakai Gemini API untuk chat dan bantuan penulisan/catatan.

## Konfigurasi token

Token AI dikonfigurasi dari Settings aplikasi. Runtime membaca token dari DataStore terlebih dahulu. Jika kosong, runtime mencoba fallback platform `ApiConfig`. Jika token tetap kosong, request AI gagal dengan pesan bahwa token belum diatur di Settings.

`local.properties` tidak menjadi jalur konfigurasi utama token AI pada dokumentasi project ini.

## Model AI

Model disimpan sebagai preference dan dipetakan ke enum domain.

Model yang tersedia di codebase:

| Model aplikasi | Model ID |
| --- | --- |
| Gemini 3.5 Flash | `gemini-3.5-flash` |
| Gemini 2.5 Flash | `gemini-2.5-flash` |
| Gemini 2.5 Pro | `gemini-2.5-pro` |
| Gemini 2.0 Flash | `gemini-2.0-flash` |

Setiap model memiliki output token limit efektif yang dipakai saat membuat `GenerationConfig`.

## Personality

Personality mengubah gaya system prompt.

Personality yang tersedia:

- Default
- Kuudere
- Tsundere
- Yandere
- Yankee
- Yakuza
- OjouSama
- Sensei
- Otaku Friend

## Komponen utama

| Komponen | Fungsi |
| --- | --- |
| `GeminiService` | Mengirim request ke Gemini API dan membaca usage metadata. |
| `AIRepository` | Contract fitur AI. |
| `AIRepositoryImpl` | Implementasi summarize, improve writing, generate ideas, dan chat. |
| `AiChatRepository` | Penyimpanan chat history lokal. |
| `AiTokenUsageRepository` | Penyimpanan penggunaan token. |
| `AIAssistantViewModel` | State dan event untuk AI Assistant screen. |

## Request flow

```text
AIAssistantScreen
-> AIAssistantViewModel
-> AIRepositoryImpl
-> GeminiService
-> Gemini API
-> usage metadata
-> AiTokenUsageRepository
-> UiState response
```

## Chat history

Chat disimpan di SQLDelight melalui `AiChatMessageEntity`. Pesan dipisahkan berdasarkan `session_key`, sehingga chat dapat terkait dengan konteks tertentu seperti note, media, atau assistant umum.

## Token usage

Jika respons Gemini memiliki `usageMetadata`, aplikasi menyimpan:

- prompt tokens
- candidates tokens
- thoughts tokens
- cached content tokens
- total tokens

Data ini dipakai di Settings untuk menampilkan penggunaan token AI.

## Error yang umum

| Error | Penyebab |
| --- | --- |
| AI API token belum diatur di Settings | Token kosong di DataStore dan fallback platform kosong. |
| Respons kosong dari AI | Gemini tidak mengembalikan text content. |
| HTTP error / API error | Token tidak valid, model tidak tersedia, quota habis, atau koneksi gagal. |
