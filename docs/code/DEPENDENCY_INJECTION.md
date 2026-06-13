# Dependency Injection

Project memakai Koin.

## Common initializer

Common initializer menerima platform modules dan menggabungkannya dengan shared modules.

```text
initKoin(platformModules, config)
```

## Shared modules

| Module | Isi |
| --- | --- |
| `dispatchersModule` | `AppDispatchers` dan dispatcher platform. |
| `networkModule` | `HttpClient`, Jikan, Gemini, GitHub Release service, remote data source. |
| `databaseModule` | SQLDelight database dan query object. |
| `preferencesModule` | DataStore dan `UserPreferences`. |
| `repositoryModule` | Local source, cache policy, dan repository implementation. |
| `useCaseModule` | Note use case dan `RunGachaUseCase`. |
| `viewModelModule` | ViewModel untuk screen aplikasi. |

## Platform modules

Platform module menyediakan dependency yang tidak bisa dibuat di common source set.

Contoh:

- `DatabaseDriverFactory`
- `DataStoreFactory`
- platform-specific config

## Network dependencies

`networkModule` menyediakan satu `HttpClient` shared untuk remote API. Service yang dibuat dari client ini:

- `JikanService`
- `GeminiService`
- `GitHubReleaseService`

## Repository dependencies

Repository implementation menggabungkan source lokal dan remote sesuai kebutuhan.

Contoh pattern:

```text
RepositoryImpl(
  remoteDataSource,
  localDataSource,
  cachePolicy,
  dispatchers
)
```

## ViewModel dependencies

ViewModel tidak membuat repository sendiri. Semua dependency masuk dari Koin.

Contoh area:

- `HomeViewModel`
- `SearchViewModel`
- `AnimeDetailViewModel`
- `MangaDetailViewModel`
- `MyLibraryViewModel`
- `GachaViewModel`
- `AIAssistantViewModel`
- `SettingsViewModel`
