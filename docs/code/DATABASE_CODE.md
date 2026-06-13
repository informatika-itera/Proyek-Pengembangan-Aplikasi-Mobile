# Database Code

## Lokasi schema

```text
composeApp/src/commonMain/sqldelight/id/my/sinanonym/mybawanggacha/
```

## Lokasi migration

```text
composeApp/src/commonMain/sqldelight/migrations/
```

## Local source

Local source berada di:

```text
data/local/source/
```

Local source utama:

- `AnimeDetailCacheLocalDataSource`
- `MangaDetailCacheLocalDataSource`
- `MediaPageCacheLocalDataSource`
- `RelationPreviewCacheLocalDataSource`
- `AnimeProgressLocalDataSource`

## Database driver

`DatabaseDriverFactory` memakai expect/actual.

Common code hanya bergantung pada abstraction. Android dan iOS menyediakan driver masing-masing.

## Entity mapper

Mapper entity berada di:

```text
data/local/entity/
```

Mapper mengubah hasil query/entity menjadi domain model dan sebaliknya.

## Schema version

Schema version saat ini adalah `7` dan ikut ditampilkan di build info.

## Praktik perubahan schema

Saat mengubah schema:

1. Ubah file `.sq`.
2. Tambahkan migration `.sqm`.
3. Update local data source bila query berubah.
4. Update mapper bila field berubah.
5. Update test terkait.
6. Update dokumentasi database.
