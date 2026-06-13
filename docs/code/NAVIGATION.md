# Navigation

Navigation berada di package:

```text
presentation/navigation
```

## Komponen utama

| File/komponen | Fungsi |
| --- | --- |
| `Routes` | Definisi route dan argument. |
| `AppNavHost` | Graph navigation utama. |
| Navigation helpers | Debounce navigation dan helper argument. |

## Route utama

Route yang didukung:

- Home
- Search
- MyLibrary
- Gacha
- AnimeList
- MangaList
- LibraryEntryEditor
- Settings
- AddNote
- NoteDetail
- AIAssistant
- AnimeDetail
- MangaDetail

## Argument route

Detail screen memakai ID media:

```text
AnimeDetail(malId)
MangaDetail(malId)
```

AI Assistant dapat menerima konteks:

- note
- media
- general assistant

Library editor menerima data media untuk add/edit entry.

## Debounce navigation

NavHost memiliki debounce pendek untuk menghindari double navigation akibat tap berulang cepat. Ini menjaga back stack tidak terisi route duplikat secara tidak sengaja.

## Prinsip navigation

- Route disimpan terpusat.
- Screen tidak membuat route string manual bila helper tersedia.
- Argument wajib di-encode/decode secara aman.
- Back navigation ditangani dari NavController atau callback screen.
