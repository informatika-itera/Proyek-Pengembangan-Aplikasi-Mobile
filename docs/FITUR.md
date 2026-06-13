# Fitur

## Home dan Discover

Home menampilkan entry point untuk eksplorasi anime dan manga. Konten berasal dari Jikan API dan cache lokal bila tersedia.

Konten utama:

- anime populer
- manga populer
- season now
- upcoming season
- recommendation
- watch episode
- navigasi ke detail, list, search, library, gacha, AI, dan settings

## Anime List dan Manga List

List screen menampilkan media berdasarkan kategori dan filter. Data mendukung pagination dan refresh.

## Search

Search mencari anime dan manga dari Jikan API. Query dibuat debounce agar request tidak dikirim pada setiap perubahan karakter secara agresif.

Filter yang didukung oleh domain search antara lain:

- media type
- genre
- score minimum
- status
- format/type

## Detail anime/manga

Detail screen menampilkan:

- poster
- judul
- sinopsis
- score
- status
- type/format
- episode/chapter/volume bila tersedia
- relasi media
- aksi tambah/edit library
- episode progress untuk anime

## My Library

Library menyimpan item lokal berdasarkan `media_id` dan `media_type`.

Field utama:

- status
- progress
- total count
- user score
- notes
- created at
- updated at

Status yang dipakai:

- plan
- watching/reading
- completed
- on hold
- dropped

## Notes

Notes adalah catatan lokal yang disimpan di SQLDelight. Notes dapat dipakai untuk catatan personal dan dapat dikirim ke AI assistant untuk summarize, improve writing, atau generate ideas.

## Gacha

Gacha menghasilkan rekomendasi anime/manga berdasarkan preferensi. Implementasi memakai deck session agar reroll tidak sering mengulang item yang sama.

Detail teknis ada di [GACHA.md](GACHA.md).

## AI Assistant

AI Assistant memakai Gemini API. Pengguna mengatur token, model, dan personality dari Settings. Histori chat disimpan lokal berdasarkan session key.

Detail teknis ada di [AI_ASSISTANT.md](AI_ASSISTANT.md).

## Settings

Settings menyediakan:

- theme mode
- color scheme
- network mode
- AI token
- AI model
- AI personality
- AI token usage
- Jikan usage indicator
- cache cleanup
- About/build info
- release check
