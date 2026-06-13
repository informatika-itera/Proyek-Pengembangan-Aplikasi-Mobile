# Gacha Code

## File penting

```text
domain/gacha/model/
domain/gacha/repository/GachaRepository.kt
domain/gacha/usecase/RunGachaUseCase.kt
data/repository/gacha/GachaRepositoryImpl.kt
presentation/screen/gacha/
```

## Repository contract

`GachaRepository` menyediakan:

- observe last preference
- observe history
- get last preference
- save last preference
- save history entry
- clear history

## RunGachaUseCase

Dependency:

- `SearchRepository`
- `LibraryRepository`
- `GachaRepository`

Tanggung jawab:

1. Normalisasi target media type.
2. Membuat fingerprint dari preference.
3. Membaca library key yang sudah dikenal.
4. Membaca recent result dari history.
5. Membuat atau memakai ulang `GachaDeckSession`.
6. Mengambil kandidat dari search repository.
7. Memfilter kandidat.
8. Memilih hasil acak.
9. Mengembalikan `GachaRunResult`.

## GachaDeckSession

Session menyimpan deck kandidat untuk fingerprint preference tertentu. Ini membuat reroll lebih natural karena tidak selalu meminta pool kecil yang sama dari API.

## GachaViewModel

State penting:

- `preference`
- `history`
- `isLoading`
- `isRolling`
- `canSkipRoll`
- `errorMessage`
- `infoMessage`
- `result`

Event penting:

- update preference
- refresh genres
- run gacha
- skip roll animation
- add result to library
- clear history

## Animation handling

ViewModel memisahkan proses roll dan durasi minimum animasi. Hasil dapat sudah tersedia, tetapi UI menunggu minimum duration kecuali user menekan skip.
