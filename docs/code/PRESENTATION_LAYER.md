# Presentation Layer

Presentation layer berisi UI Compose, ViewModel, navigation, theme, dan reusable component.

## Struktur

```text
presentation/
├── component
├── navigation
├── screen
└── theme
```

## Screen

Screen utama:

- Home
- Search
- AnimeList
- MangaList
- AnimeDetail
- MangaDetail
- MyLibrary
- LibraryEntryEditor
- Gacha
- Notes
- AIAssistant
- Settings

## ViewModel

ViewModel menyimpan UI state dan mengekspos event handler untuk screen.

Prinsip:

- ViewModel menerima repository/use case dari Koin.
- ViewModel tidak membuat service remote atau database sendiri.
- ViewModel mengubah domain result menjadi UI state.
- Screen tidak melakukan business logic berat.

## UI state

Pattern umum UI state:

- loading
- content
- error message
- info message
- refreshing
- empty state

## Theme

Theme mendukung:

- system/light/dark
- beberapa color scheme
- Material Design 3

Color scheme dipilih dari Settings dan diterapkan ke Compose UI.

## Component

Reusable component dipakai untuk card media, loading, error, empty state, top bar, dialog, editor form, filter chip, dan item list.
