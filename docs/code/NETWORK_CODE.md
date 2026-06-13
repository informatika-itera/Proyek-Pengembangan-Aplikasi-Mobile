# Network Code

## Ktor client

Ktor client dibuat melalui `HttpClientFactory` dan didaftarkan di `networkModule`.

Service remote memakai client yang sama agar konfigurasi logging, serialization, dan timeout konsisten.

## Jikan service

Lokasi:

```text
data/remote/jikan/api/JikanService.kt
```

Fungsi:

- search anime/manga
- top anime/manga
- season anime
- recommendation
- random anime/manga
- detail anime/manga
- episode anime
- genre/producers/magazines
- relation preview

Request Jikan melewati `JikanRateLimiter`.

## Gemini service

Lokasi:

```text
data/remote/gemini/api/GeminiService.kt
```

Fungsi:

- membuat request `generateContent`
- membaca token dari Settings/DataStore
- fallback ke `ApiConfig` bila tersedia
- membuat `GenerationConfig`
- membaca `usageMetadata`
- mencatat token usage
- mengembalikan text content atau error

## GitHub Release service

Lokasi:

```text
data/remote/github/api/GitHubReleaseService.kt
```

Endpoint:

```text
/repos/sinavarasina/Proyek-Pengembangan-Aplikasi-Mobile/releases/latest
```

Dipakai oleh Settings/About untuk mengecek release terbaru.

## DTO dan mapper

DTO berada di package remote masing-masing. Mapper mengubah DTO menjadi domain model.

Aturan:

- DTO tidak keluar dari data layer.
- Domain model tidak menyimpan detail serialization remote.
- Error remote diubah menjadi failure yang dapat ditampilkan ViewModel.
