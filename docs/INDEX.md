# Documentation Index

Dokumentasi ini menjelaskan project My Bawang Gacha berdasarkan codebase terbaru. Dokumen lama yang bersifat template, tutorial modifikasi, atau checklist sprint manual tidak dipakai sebagai dokumentasi utama.

## Project

| Dokumen | Isi |
| --- | --- |
| [CARA_MENJALANKAN.md](CARA_MENJALANKAN.md) | Kebutuhan sistem, build Android, build iOS, dan konfigurasi runtime. |
| [ARSITEKTUR.md](ARSITEKTUR.md) | Arsitektur Clean Architecture + MVVM dan arah dependency. |
| [STRUKTUR_KODE.md](STRUKTUR_KODE.md) | Struktur folder dan tanggung jawab package. |
| [FITUR.md](FITUR.md) | Dokumentasi fitur aplikasi dari sisi produk. |
| [GACHA.md](GACHA.md) | Sistem gacha, preference, deck session, history, dan anti-repetition. |
| [AI_ASSISTANT.md](AI_ASSISTANT.md) | Integrasi Gemini, model, personality, token usage, dan chat history. |
| [DATABASE.md](DATABASE.md) | SQLDelight schema dan penyimpanan lokal. |
| [API_DAN_CACHE.md](API_DAN_CACHE.md) | Integrasi Jikan, GitHub Release API, rate limiter, cache, dan offline policy. |
| [SETTINGS_DAN_ABOUT.md](SETTINGS_DAN_ABOUT.md) | Settings, theme, color scheme, network mode, dan About screen. |
| [CI_RELEASE.md](CI_RELEASE.md) | GitHub Actions CI, dry build, release, signing, dan artifact. |
| [TESTING.md](TESTING.md) | Test yang ada dan rekomendasi test berikutnya. |
| [TROUBLESHOOTING.md](TROUBLESHOOTING.md) | Masalah build/runtime yang relevan dengan codebase saat ini. |
| [GIT_WORKFLOW.md](GIT_WORKFLOW.md) | Workflow Git yang ringkas untuk pengembangan project. |
| [KONTRIBUSI.md](KONTRIBUSI.md) | Panduan kontribusi internal project. |
| [REMOVED_OR_REPLACED.md](REMOVED_OR_REPLACED.md) | Dokumen lama yang diganti atau dihapus dari dokumentasi utama. |

## Dokumentasi kode

| Dokumen | Isi |
| --- | --- |
| [code/INDEX.md](code/INDEX.md) | Indeks dokumentasi kode. |
| [code/CODEBASE_OVERVIEW.md](code/CODEBASE_OVERVIEW.md) | Ringkasan modul, package, dan alur aplikasi. |
| [code/DEPENDENCY_INJECTION.md](code/DEPENDENCY_INJECTION.md) | Koin modules dan dependency graph. |
| [code/NAVIGATION.md](code/NAVIGATION.md) | Route, NavHost, argument, dan debounce navigation. |
| [code/DOMAIN_LAYER.md](code/DOMAIN_LAYER.md) | Domain model, repository contract, dan use case. |
| [code/DATA_LAYER.md](code/DATA_LAYER.md) | Repository implementation, source, mapper, DTO, dan persistence. |
| [code/PRESENTATION_LAYER.md](code/PRESENTATION_LAYER.md) | Screen, ViewModel, UI state, dan komponen UI. |
| [code/DATABASE_CODE.md](code/DATABASE_CODE.md) | Schema SQLDelight, migrations, dan local data source. |
| [code/NETWORK_CODE.md](code/NETWORK_CODE.md) | Ktor client, Jikan, Gemini, GitHub Release API. |
| [code/CACHE_CODE.md](code/CACHE_CODE.md) | Detail cache, page cache, relation preview cache, dan cache policy. |
| [code/GACHA_CODE.md](code/GACHA_CODE.md) | Implementasi gacha domain dan presentation. |
| [code/AI_CODE.md](code/AI_CODE.md) | Implementasi AI repository, Gemini service, prompt, dan chat storage. |
| [code/SETTINGS_CODE.md](code/SETTINGS_CODE.md) | UserPreferences, SettingsRepository, AppBuildInfo, dan release check. |
| [code/TESTING_CODE.md](code/TESTING_CODE.md) | Unit test yang ada dan area test prioritas. |
