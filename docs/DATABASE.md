# Database

Project menggunakan SQLDelight untuk database lokal shared KMP.

## Schema

Schema berada di:

```text
composeApp/src/commonMain/sqldelight/id/my/sinanonym/mybawanggacha/
```

## Tabel utama

### `NoteEntity`

Menyimpan catatan lokal.

Field utama:

- `id`
- `title`
- `content`
- `category`
- `color`
- `is_pinned`
- `created_at`
- `updated_at`

### `LibraryEntryEntity`

Menyimpan anime/manga di library lokal.

Field utama:

- `id`
- `media_id`
- `media_type`
- `title`
- `image_url`
- `status`
- `progress`
- `total_count`
- `user_score`
- `notes`
- `created_at`
- `updated_at`

Constraint:

```text
UNIQUE(media_id, media_type)
```

### `AnimeDetailCache`

Menyimpan cache detail anime.

Field utama:

- `anime_id`
- `detail_json`
- `episodes_json`
- `cached_at`

### `AnimeEpisodeProgress`

Menyimpan progress episode anime.

Field utama:

- `anime_id`
- `episode_number`
- `watched`
- `marked`
- `updated_at`

### `MangaDetailCache`

Menyimpan cache detail manga.

Field utama:

- `manga_id`
- `detail_json`
- `cached_at`

### `MediaPageCache`

Menyimpan cache halaman list/search.

Field utama:

- `cache_key`
- `payload_json`
- `cached_at`

### `RelationPreviewCache`

Menyimpan cache preview relasi anime/manga.

Field utama:

- `cache_key`
- `preview_json`
- `cached_at`

### `AiChatMessageEntity`

Menyimpan chat AI lokal.

Field utama:

- `id`
- `session_key`
- `sender`
- `text`
- `created_at`

## Migration

Migration berada di:

```text
composeApp/src/commonMain/sqldelight/migrations/
```

Versi schema saat ini: `7`.

Ringkasan migration:

| Versi | Isi |
| --- | --- |
| 1 | Tabel awal notes, library, anime, manga, dan episode progress. |
| 2 | Anime detail cache. |
| 3 | Manga detail cache. |
| 4 | Media page cache. |
| 5 | Relation preview cache. |
| 6 | AI chat message. |
| 7 | Field `marked` untuk episode progress. |

## Platform driver

Database driver dibuat melalui expect/actual `DatabaseDriverFactory`.

- Android memakai driver SQLite Android.
- iOS memakai driver native sesuai actual implementation.

## Prinsip data lokal

- Entity SQLDelight tidak diekspos langsung ke UI.
- Repository mengubah entity menjadi domain model.
- Cache JSON disimpan untuk response detail/list yang lebih kompleks.
- Timestamp `cached_at` dipakai untuk kebijakan cache dan refresh.
