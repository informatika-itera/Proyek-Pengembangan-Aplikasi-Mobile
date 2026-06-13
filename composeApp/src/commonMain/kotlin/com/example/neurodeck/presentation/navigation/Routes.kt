package com.example.neurodeck.presentation.navigation

/**
 * Type-safe route definitions untuk NavHost.
 *
 * Convention: setiap Screen punya `route` (template path) dan optional
 * `createRoute(args)` untuk build URL dengan argument value.
 *
 * Sprint 2 P3a — Routes diperluas untuk mendukung 5 Tab Bottom Navigation
 * (Home / Decks / AIChat / Stats / Profile) + sub-screens yang spec prompt minta.
 *
 * Catatan: argument deckId/cardId dipakai sebagai Long (sesuai database ID type).
 * createRoute() terima Long, jadi caller cukup kasih ID-nya tanpa convert manual.
 */
sealed class Screen(val route: String) {

    // ════════════════════════════════════════════════════════════════════════
    // MAIN ROUTES — 5 Tab Bottom Navigation
    // ════════════════════════════════════════════════════════════════════════

    /** 🏠 Tab #1 — Dashboard ringkas + Continue Learning + Quick actions. */
    data object Home : Screen("home")

    /**
     * 📚 Tab #2 — CRUD deck + entry ke Study session (CORE FEATURE).
     * Menggantikan `DeckLibrary` lama yang sekarang jadi konten tab Decks.
     * Route string-nya tetap "deck_library" supaya kode existing yang
     * navigate ke Screen.DeckLibrary.route tidak break.
     */
    data object Decks : Screen("deck_library")

    /** 💬 Tab #3 — Tutor AI conversation (akan dibuat di P3f). */
    data object AIChat : Screen("ai_chat")

    /** 📊 Tab #4 — Analytics belajar mendalam (akan dibuat di P4). */
    data object Stats : Screen("stats")

    /** 👤 Tab #5 — User info + Settings + Data Management (akan dibuat di P3e). */
    data object Profile : Screen("profile")

    // ════════════════════════════════════════════════════════════════════════
    // LEGACY ALIAS — Backward compat
    // ════════════════════════════════════════════════════════════════════════

    /**
     * @deprecated Pakai [Screen.Decks] saja — route string-nya identik.
     * Dibiarkan supaya kode existing (DeckLibraryScreen, ViewModel) tidak
     * perlu di-rename massal. Akan dihapus saat Sprint 4 polish.
     */
    @Deprecated("Gunakan Screen.Decks", ReplaceWith("Screen.Decks"))
    data object DeckLibrary : Screen("deck_library")

    /**
     * @deprecated Pakai [Screen.Stats]. Route string-nya berbeda — kalau ada
     * kode yang navigate ke `Screen.Statistics.route` ("statistics"), update
     * ke `Screen.Stats.route` ("stats").
     */
    @Deprecated("Gunakan Screen.Stats", ReplaceWith("Screen.Stats"))
    data object Statistics : Screen("statistics")

    // ════════════════════════════════════════════════════════════════════════
    // SUB-SCREEN ROUTES — Push navigation dari main tabs
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Settings screen (existing route, dipakai dari Drawer atau Profile tab).
     * Bukan tab utama tapi diperlukan untuk navigasi dari beberapa entry point.
     */
    data object Settings : Screen("settings")

    /**
     * About screen (info aplikasi, team, GitHub link).
     * Diakses dari Drawer atau Profile tab.
     */
    data object About : Screen("about")

    /**
     * Form edit profile (nama, username, bio, avatar).
     * Diakses dari Profile tab via tombol Edit.
     */
    data object EditProfile : Screen("edit_profile")

    /**
     * Layar Notifikasi — daftar deck yang siap dipelajari (ada kartu due).
     * Diakses dari ikon lonceng di TopBar (semua main tab).
     */
    data object Notifications : Screen("notifications")

    /**
     * Form create deck (langkah pertama: nama + deskripsi + pilih cara generate).
     * Diakses dari Decks tab via FAB.
     */
    data object CreateDeck : Screen("create_deck")

    /**
     * Layar AI generate flashcards dari teks materi.
     * Argument: deckId (Long?) — nullable karena bisa dipanggil dari:
     *   - CreateDeckScreen (deckId belum exist, pass 0L)
     *   - CardListScreen (deck sudah exist, pass real deckId)
     *
     * Route mengembalikan "0" untuk deckId null supaya tipe Long tetap konsisten
     * di NavType.LongType (tidak pakai NavType.LongArrayType atau nullable trick).
     */
    data object ImportGenerate : Screen("import_generate/{deckId}") {
        fun createRoute(deckId: Long = 0L): String = "import_generate/$deckId"
    }

    /**
     * List kartu dalam 1 deck. Argument: deckId.
     * Diakses dari DeckLibrary saat user tap card deck.
     */
    data object CardList : Screen("card_list/{deckId}") {
        fun createRoute(deckId: Long): String = "card_list/$deckId"
    }

    /**
     * Form untuk tambah kartu baru ke deck. Argument: deckId.
     * Diakses dari CardListScreen via FAB.
     */
    data object AddCard : Screen("add_card/{deckId}") {
        fun createRoute(deckId: Long): String = "add_card/$deckId"
    }

    /**
     * Study Session untuk deck tertentu. Argument: deckId.
     * Diakses dari CardListScreen via tombol "Mulai Belajar".
     */
    data object StudySession : Screen("study_session/{deckId}") {
        fun createRoute(deckId: Long): String = "study_session/$deckId"
    }

    /**
     * Form edit kartu existing. Argument: cardId.
     * Diakses dari CardListScreen via icon Edit di CardItem.
     */
    data object EditCard : Screen("edit_card/{cardId}") {
        fun createRoute(cardId: Long): String = "edit_card/$cardId"
    }
}

/**
 * Argument keys — pakai konstanta supaya tidak typo di multiple tempat.
 */
object NavArgs {
    const val DECK_ID = "deckId"
    const val CARD_ID = "cardId"
}

/**
 * Semua route yang merupakan TAB UTAMA (muncul di Bottom Navigation).
 *
 * Dipakai oleh `AppNavHost` untuk menentukan apakah TopBar + BottomBar
 * harus di-show (true kalau current route ada di set ini) atau di-hide
 * (false untuk sub-screens seperti StudySession, AddCard, dll.).
 *
 * Set bukan List — lookup O(1) untuk `currentRoute in mainRoutes`.
 */
val mainRoutes: Set<String> = setOf(
    Screen.Home.route,
    Screen.Decks.route,
    Screen.AIChat.route,
    Screen.Stats.route,
    Screen.Profile.route,
)