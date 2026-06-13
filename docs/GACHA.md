# Gacha System

Gacha system memilih rekomendasi anime atau manga berdasarkan preferensi pengguna, lalu menyimpan preferensi terakhir dan histori hasil secara lokal.

## Tujuan desain

- Menghindari hasil yang terasa terlalu sering berulang.
- Mendukung anime, manga, atau kombinasi keduanya.
- Menghormati preferensi filter pengguna.
- Dapat mengecualikan item yang sudah ada di library.
- Menyimpan histori hasil untuk konteks dan anti-repetition.

## Komponen utama

| Komponen | Lokasi | Fungsi |
| --- | --- | --- |
| `GachaPreference` | `domain/gacha/model` | Preferensi media pool, genre, score, status, format, dan opsi exclude. |
| `GachaRepository` | `domain/gacha/repository` | Contract untuk preference dan history. |
| `RunGachaUseCase` | `domain/gacha/usecase` | Orkestrasi pemilihan kandidat dan hasil gacha. |
| `GachaDeckSession` | `domain/gacha/usecase` | Session deck berdasarkan fingerprint preferensi. |
| `GachaRepositoryImpl` | `data/repository/gacha` | Penyimpanan preference/history di DataStore. |
| `GachaViewModel` | `presentation/screen/gacha` | UI state, roll animation, skip animation, add to library. |

## Alur roll

```text
GachaScreen
-> GachaViewModel.runGacha()
-> saveLastPreference()
-> RunGachaUseCase(preference)
-> SearchRepository.searchMedia()
-> filter library/recent/history
-> deck session pick
-> saveHistoryEntry()
-> update UiState result
```

## Deck session

Deck session dibuat berdasarkan fingerprint stabil dari preferensi. Selama fingerprint sama, deck dipakai ulang agar hasil tidak hanya bergantung pada request API baru. Ketika preferensi berubah, session lama tidak dipakai.

Deck menyimpan kandidat yang sudah diambil dan melakukan prefetch ketika jumlah kandidat rendah.

Konstanta penting:

```text
SEARCH_LIMIT_PER_PAGE = 25
INITIAL_POOL_PAGE_COUNT = 3
PREFETCH_PAGE_COUNT = 1
DECK_PREFETCH_THRESHOLD = 8
RECENT_EXCLUSION_WINDOW = 12
```

## Anti-repetition

Item dapat dikeluarkan dari kandidat bila:

- sudah ada di library dan preferensi tidak mengizinkan known item
- termasuk hasil terbaru dalam history window
- sudah dipakai dalam deck session aktif

## History

History disimpan lokal melalui DataStore dalam bentuk JSON. Implementasi membatasi jumlah history agar tidak tumbuh tanpa batas.

## Animasi roll

ViewModel menyediakan state:

- `isRolling`
- `canSkipRoll`
- `pendingRollOutcome`
- `activeRollId`

Roll memiliki minimum duration agar animasi tidak terasa terlalu instan. User dapat skip animation tanpa membatalkan hasil roll.

## Add to library

Hasil gacha dapat ditambahkan ke library. Default status disesuaikan dengan media type, dan catatan dapat berisi konteks bahwa item ditambahkan dari Gacha.
