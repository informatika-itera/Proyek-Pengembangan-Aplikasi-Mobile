# Data Layer

Data layer mengimplementasikan repository contract dari domain.

## Struktur

```text
data/
├── local/
│   ├── datastore/
│   ├── entity/
│   └── source/
├── remote/
│   ├── gemini/
│   ├── github/
│   └── jikan/
└── repository/
```

## Local data

Local data memakai:

- SQLDelight untuk data relational/cache.
- DataStore untuk preference/settings sederhana.

Local source bertanggung jawab menjalankan query SQLDelight dan mengubah hasil query menjadi bentuk yang siap dipakai repository.

## Remote data

Remote data memakai Ktor Client.

Remote service utama:

- `JikanService`
- `GeminiService`
- `GitHubReleaseService`

DTO remote tidak dipakai langsung oleh UI. DTO dipetakan ke domain model melalui mapper.

## Repository implementation

Repository implementation berada di package `data/repository`.

Tugas repository implementation:

- menentukan apakah data diambil dari cache atau remote
- menjalankan request remote
- menyimpan cache lokal
- memetakan DTO/entity ke domain model
- menerapkan network mode/cache policy
- menjalankan operasi pada dispatcher yang tepat

## Error handling

Service remote membungkus error API ke exception/result. ViewModel menerima failure dan mengubahnya menjadi state error/message UI.

## DataStore

`UserPreferences` menyimpan:

- theme mode
- color scheme
- network mode
- AI token
- AI model
- AI personality
- token usage
- gacha preference
- gacha history
