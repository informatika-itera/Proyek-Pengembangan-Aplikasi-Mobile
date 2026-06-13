# Kontribusi

Dokumen ini menjelaskan aturan kontribusi internal project.

## Prinsip umum

- Pertahankan pemisahan layer presentation, domain, dan data.
- Jangan membawa DTO remote langsung ke UI.
- Jangan membawa entity SQLDelight langsung ke UI.
- Gunakan repository contract dari domain.
- Gunakan Koin untuk dependency injection.
- Tambahkan migration SQLDelight bila schema berubah.
- Perbarui dokumentasi ketika fitur atau arsitektur berubah.

## Menambah fitur baru

Checklist:

1. Tambahkan domain model bila diperlukan.
2. Tambahkan repository contract atau use case bila ada logic bisnis baru.
3. Implementasikan repository di data layer.
4. Tambahkan source/DTO/mapper bila melibatkan remote atau database.
5. Tambahkan ViewModel dan UI state.
6. Tambahkan screen atau komponen UI.
7. Daftarkan dependency di Koin module.
8. Tambahkan route bila screen baru.
9. Tambahkan test sesuai area yang berubah.
10. Perbarui dokumentasi terkait.

## Menambah tabel database

Checklist:

1. Tambahkan schema `.sq` atau ubah schema yang ada.
2. Tambahkan migration `.sqm`.
3. Tambahkan local data source.
4. Tambahkan mapper entity-domain.
5. Hubungkan ke repository implementation.
6. Perbarui `DATABASE.md` dan `docs/code/DATABASE_CODE.md`.

## Menambah remote endpoint

Checklist:

1. Tambahkan method di service Ktor.
2. Tambahkan DTO bila response baru.
3. Tambahkan mapper DTO-domain.
4. Tambahkan repository method.
5. Tambahkan cache bila endpoint dipakai di UI utama.
6. Perbarui `API_DAN_CACHE.md`.

## Menambah settings baru

Checklist:

1. Tambahkan key di `UserPreferences`.
2. Tambahkan model enum/value object di domain settings bila perlu.
3. Tambahkan method di `SettingsRepository`.
4. Tambahkan UI di Settings screen.
5. Tambahkan test parsing/fallback.
6. Perbarui `SETTINGS_DAN_ABOUT.md`.
