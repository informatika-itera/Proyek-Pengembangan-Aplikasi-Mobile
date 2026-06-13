# Arsitektur

My Bawang Gacha menggunakan Clean Architecture dengan MVVM pada layer presentation.

## Arah dependency

```text
Presentation -> Domain -> Data
```

Aturan utama:

- Presentation boleh memakai domain model, repository contract, dan use case.
- Domain tidak bergantung pada framework UI, SQLDelight, Ktor, DataStore, atau DTO remote.
- Data mengimplementasikan contract dari domain dan boleh bergantung pada remote API, local database, cache, mapper, dan DataStore.
- Dependency injection disediakan oleh Koin.

## Layer

| Layer | Package utama | Tanggung jawab |
| --- | --- | --- |
| Presentation | `presentation` | Screen, ViewModel, navigation, UI state, theme, component, interaction. |
| Domain | `domain` | Model bisnis, repository interface, use case, enum/value object. |
| Data | `data` | Repository implementation, remote service, local data source, DTO, mapper, cache, DataStore. |
| Core | `core` | DI, network client, coroutine dispatchers, build info, platform abstraction. |

## MVVM

ViewModel menjadi penghubung antara UI dan domain/data flow. Screen membaca `StateFlow` atau state object dari ViewModel, lalu mengirim event user melalui function ViewModel.

Contoh flow umum:

```text
Screen -> ViewModel -> UseCase/Repository -> Local/Remote Source -> Mapper -> Domain Model -> UiState -> Screen
```

## Platform abstraction

Project memakai `expect/actual` untuk bagian yang berbeda antar platform, seperti:

- database driver
- DataStore factory
- API config fallback
- build info provider
- platform name/runtime info

## Shared UI

Sebagian besar UI berada di `composeApp/src/commonMain`. Android dan iOS hanya menyediakan entry point platform, dependency platform, dan actual implementation yang dibutuhkan.

## Cache strategy

Repository data menggabungkan remote API, SQLDelight cache, dan network policy. Untuk beberapa flow, UI bisa tetap menampilkan konten cache ketika refresh gagal.
