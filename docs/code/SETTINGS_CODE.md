# Settings Code

## UserPreferences

Lokasi:

```text
data/local/datastore/UserPreferences.kt
```

Menyimpan preference aplikasi melalui DataStore.

Data yang disimpan:

- theme mode
- color scheme
- network mode
- AI API token
- AI model
- AI personality
- AI token usage
- gacha last preference
- gacha history

## SettingsRepository

Domain contract ada di:

```text
domain/settings/repository/SettingsRepository.kt
```

Implementasi berada di data repository settings.

## Settings model

Model penting:

- `ThemeMode`
- `AppColorScheme`
- `NetworkMode`
- `AiApiModel`
- `AiPersonality`
- `AiTokenUsageDelta`
- `GitHubRelease`

Setiap enum memiliki parsing fallback agar preference lama/rusak tidak membuat aplikasi crash.

## About/build info

Build info disediakan oleh `AppBuildInfoProvider` dan generated Gradle metadata.

Field yang ditampilkan meliputi version, commit, build target, CI info, database schema, dan version ABI.

## GitHub release check

Settings/About memakai `GitHubReleaseService` untuk membaca release terbaru dari GitHub API.

## Theme integration

Theme mode dan color scheme dari Settings diterapkan ke Compose Material 3 theme.
