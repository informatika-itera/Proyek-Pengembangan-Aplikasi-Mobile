# Settings dan About

Settings menyimpan konfigurasi aplikasi melalui DataStore.

## Theme mode

| Mode | Perilaku |
| --- | --- |
| System | Mengikuti tema sistem. |
| Light | Memaksa tema terang. |
| Dark | Memaksa tema gelap. |

## Color scheme

Color scheme yang tersedia:

- Code Geass
- Pak Habib
- Gruvbox
- Catppuccin
- Hatsune Miku

## Network mode

| Mode | Perilaku |
| --- | --- |
| Auto | Aplikasi boleh mengambil data remote dan memperbarui cache. |
| Offline Only | Aplikasi hanya memakai data lokal/cache. |

## AI settings

Settings AI menyimpan:

- API token
- model
- personality
- token usage

Token API disimpan lokal di DataStore. Dokumentasi tidak menganjurkan menyimpan token Gemini di `local.properties` sebagai cara utama.

## Cache management

Settings menyediakan aksi untuk membersihkan cache aplikasi. Cache yang dapat dibersihkan mencakup cache media page, relation preview, anime detail, dan manga detail sesuai implementasi repository/local source.

## About screen

About screen menampilkan informasi aplikasi dan build.

Informasi utama:

- app name
- version name
- version code
- build profile
- build target
- runtime platform
- device
- application ID
- repository
- branch
- commit
- commit state
- build date
- CI flag
- CI run ID
- database schema
- version ABI

## Release check

Aplikasi dapat mengecek release terbaru dari GitHub Releases API. Data release ditampilkan dari domain `GitHubRelease`.
