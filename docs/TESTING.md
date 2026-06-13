# Testing

Dokumentasi ini mencatat test yang relevan dengan codebase, bukan checklist manual sprint lama.

## Test yang tersedia

Test shared berada di:

```text
composeApp/src/commonTest/
```

Area yang sudah memiliki test mencakup parsing settings enum seperti color scheme dan AI personality fallback.

CI menjalankan compile unit test sources melalui:

```bash
./gradlew :composeApp:assembleUnitTest -PbuildProfile=ci -PbuildTarget=tests
```

## Test prioritas berikutnya

| Area | Rekomendasi test |
| --- | --- |
| Gacha | Fingerprint preference, deck filtering, recent exclusion, known library exclusion, fallback saat pool kosong. |
| Search mapper | Mapping DTO Jikan ke domain model untuk anime/manga. |
| Repository cache | Cache hit, stale-while-revalidate, force refresh, offline-only behavior. |
| Library repository | Upsert unik berdasarkan `media_id` dan `media_type`, status/progress/score mapping. |
| AI settings | Model parsing, token fallback, usage metadata aggregation. |
| Navigation | Route serialization untuk argument detail, AI context, dan library editor. |
| Build info | Version ABI ketika commit tersedia dan ketika metadata Git kosong. |

## Smoke test manual ringkas

Manual smoke test tetap boleh dilakukan sebelum release, tetapi tidak perlu menjadi file sprint terpisah.

Flow utama:

1. Home terbuka dan menampilkan data/cache.
2. Search anime dan manga berjalan.
3. Detail anime/manga dapat dibuka.
4. Item dapat ditambahkan dan diedit di library.
5. Notes dapat dibuat, diedit, dan dihapus.
6. Gacha dapat roll, skip animation, reroll, dan add to library.
7. AI Assistant menampilkan error jelas saat token kosong dan dapat chat saat token valid.
8. Settings dapat mengubah tema, color scheme, network mode, model, dan personality.
9. About menampilkan build info.
