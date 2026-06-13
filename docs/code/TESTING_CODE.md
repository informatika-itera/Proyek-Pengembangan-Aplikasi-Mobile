# Testing Code

## Lokasi test

```text
composeApp/src/commonTest/
```

## Test yang sudah relevan

Test parsing enum settings memastikan string dari DataStore dapat dipetakan ke enum dengan fallback aman.

Area contoh:

- `AppColorScheme.fromString()`
- `AiPersonality.fromString()`

## CI test command

CI menjalankan:

```bash
./gradlew :composeApp:assembleUnitTest -PbuildProfile=ci -PbuildTarget=tests
```

## Target test berikutnya

### Gacha

- preference fingerprint stabil
- deck session tidak reuse saat fingerprint berubah
- recent exclusion window
- known library exclusion
- fallback saat kandidat kurang

### Cache

- cache hit
- cache miss
- stale content saat refresh gagal
- force refresh bypass cache
- offline-only behavior

### Repository mapper

- anime detail DTO ke domain
- manga detail DTO ke domain
- search item DTO ke domain
- library entity ke domain

### Settings

- model fallback
- network mode fallback
- token usage aggregation

### Navigation

- route encode/decode untuk detail
- route AI context
- library editor argument
