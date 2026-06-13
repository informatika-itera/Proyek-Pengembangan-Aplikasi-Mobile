# API dan Cache

Project memakai Ktor Client untuk remote API dan SQLDelight untuk cache lokal.

## Remote API

| API | Fungsi |
| --- | --- |
| Jikan API | Sumber anime, manga, search, season, top, genre, episode, dan recommendation. |
| Gemini API | AI assistant, summarize, improve writing, dan generate ideas. |
| GitHub Releases API | Mengecek release terbaru project. |

## Jikan API

Base URL:

```text
https://api.jikan.moe/v4/
```

Endpoint yang dipakai antara lain:

- `recommendations/anime`
- `recommendations/manga`
- `random/anime`
- `random/manga`
- `watch/episodes`
- `genres/anime`
- `genres/manga`
- `producers`
- `magazines`
- `anime`
- `manga`
- `anime/{id}`
- `anime/{id}/full`
- `anime/{id}/episodes`
- `manga/{id}/full`
- `seasons/now`
- `seasons/upcoming`
- `seasons/{year}/{season}`
- `top/anime`
- `top/manga`

## Rate limiting

Jikan request melewati `JikanRateLimiter` agar request tidak terlalu rapat.

Konfigurasi utama:

```text
minimum request interval = 360 ms
approx per minute limit = 60
```

## Cache lokal

| Cache | Tabel | Isi |
| --- | --- | --- |
| Anime detail | `AnimeDetailCache` | Detail anime dan episode JSON. |
| Manga detail | `MangaDetailCache` | Detail manga JSON. |
| Media page | `MediaPageCache` | Payload list/search dengan cache key. |
| Relation preview | `RelationPreviewCache` | Preview relasi anime/manga. |

## Cache policy

Repository memakai cache untuk menjaga UI tetap responsif. Pada beberapa flow, data cache ditampilkan dulu, lalu remote refresh berjalan ketika network mode mengizinkan.

Network mode tersedia di Settings:

| Mode | Perilaku |
| --- | --- |
| Auto | Remote API boleh dipakai dan cache dapat diperbarui. |
| Offline Only | Remote API tidak dipakai; data yang tampil berasal dari cache/lokal. |

## GitHub Release API

GitHub release check memakai endpoint:

```text
https://api.github.com/repos/sinavarasina/Proyek-Pengembangan-Aplikasi-Mobile/releases/latest
```

Response dipetakan ke domain `GitHubRelease` dengan field:

- `tagName`
- `name`
- `htmlUrl`
