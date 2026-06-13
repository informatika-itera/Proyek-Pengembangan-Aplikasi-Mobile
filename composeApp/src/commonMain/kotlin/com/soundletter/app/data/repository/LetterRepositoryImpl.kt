package com.soundletter.app.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.soundletter.app.data.local.SoundLetterDatabase
import com.soundletter.app.data.local.entity.toDomain
import com.soundletter.app.domain.model.Note
import com.soundletter.app.domain.model.NoteCategory
import com.soundletter.app.domain.model.NoteColor
import com.soundletter.app.domain.repository.LetterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus

class LetterRepositoryImpl(
    private val database: SoundLetterDatabase
) : LetterRepository {

    private val queries = database.noteQueries
    private val dummyGlobalLetters = listOf(
        Note(
            id = -201, recipient = "Rara", sender = "Midnight Wanderer",
            content = "Hujan di perantauan selalu bikin aku ingat masakan ibu. Kamu baik-baik ya di sana, jangan telat makan.",
            songTitle = "Home Bound", songArtist = "Indie Soul",
            songPreviewUrl = "https://mp3l.jamendo.com/?trackid=1886257&format=mp31",
            songAlbumArtUrl = "https://picsum.photos/seed/201/300/300",
            category = NoteCategory.PERSONAL, color = NoteColor.BLUE,
            createdAt = Clock.System.now().minus(24, DateTimeUnit.HOUR)
        ),
        Note(
            id = -202, recipient = "Dimas", sender = "Sobat Seperjuangan",
            content = "Selamat atas gelarnya! Perjuangan 4 tahun di lab akhirnya lunas. Dunia luar sudah menanti.",
            songTitle = "Victory Lap", songArtist = "The Achievers",
            songPreviewUrl = "https://mp3l.jamendo.com/?trackid=1245678&format=mp31",
            songAlbumArtUrl = "https://picsum.photos/seed/202/300/300",
            category = NoteCategory.GENERAL, color = NoteColor.ORANGE,
            createdAt = Clock.System.now().minus(2, DateTimeUnit.HOUR)
        ),
        Note(
            id = -203, recipient = "Dia", sender = "A.N",
            content = "Aku masih sering datang ke kedai kopi itu, duduk di kursi yang sama, memesan rasa yang sama, tapi tanpamu.",
            songTitle = "Empty Chairs", songArtist = "Broken String",
            songPreviewUrl = "https://mp3l.jamendo.com/?trackid=1567890&format=mp31",
            songAlbumArtUrl = "https://picsum.photos/seed/203/300/300",
            category = NoteCategory.PERSONAL, color = NoteColor.PURPLE,
            createdAt = Clock.System.now().minus(5, DateTimeUnit.HOUR)
        ),
        Note(
            id = -204, recipient = "Seseorang", sender = "Secret Admirer",
            content = "Cara kamu tersenyum saat membaca buku di perpus itu sangat menenangkan. Semoga harimu menyenangkan.",
            songTitle = "Quiet Library", songArtist = "Acoustic Dreams",
            songPreviewUrl = "https://mp3l.jamendo.com/?trackid=1122334&format=mp31",
            songAlbumArtUrl = "https://picsum.photos/seed/204/300/300",
            category = NoteCategory.IDEAS, color = NoteColor.PINK,
            createdAt = Clock.System.now().minus(10, DateTimeUnit.MINUTE)
        ),
        Note(
            id = -205, recipient = "Mantan", sender = "Ghost of Past",
            content = "Aku dengar kamu sudah bahagia sekarang. Syukurlah, setidaknya salah satu dari kita berhasil melakukannya.",
            songTitle = "Moving On", songArtist = "Final Chapter",
            songPreviewUrl = "https://mp3l.jamendo.com/?trackid=1998877&format=mp31",
            songAlbumArtUrl = "https://picsum.photos/seed/205/300/300",
            category = NoteCategory.PERSONAL, color = NoteColor.DEFAULT,
            createdAt = Clock.System.now().minus(72, DateTimeUnit.HOUR)
        ),
        Note(
            id = -206, recipient = "Ayah", sender = "Si Bungsu",
            content = "Yah, aku baru saja gajian pertama. Minggu depan aku pulang, kita makan sate favorit Ayah ya.",
            songTitle = "Father's Love", songArtist = "Roots & Wings",
            songPreviewUrl = "https://mp3l.jamendo.com/?trackid=1445566&format=mp31",
            songAlbumArtUrl = "https://picsum.photos/seed/206/300/300",
            category = NoteCategory.GENERAL, color = NoteColor.GREEN,
            createdAt = Clock.System.now().minus(8, DateTimeUnit.HOUR)
        ),
        Note(
            id = -207, recipient = "Team", sender = "Project Lead",
            content = "Kerja keras kalian luar biasa minggu ini. Istirahatlah yang cukup, kesehatan kalian lebih utama.",
            songTitle = "Weekend Vibes", songArtist = "Lofi Work",
            songPreviewUrl = "https://mp3l.jamendo.com/?trackid=1778899&format=mp31",
            songAlbumArtUrl = "https://picsum.photos/seed/207/300/300",
            category = NoteCategory.WORK, color = NoteColor.YELLOW,
            createdAt = Clock.System.now().minus(12, DateTimeUnit.HOUR)
        ),
        Note(
            id = -208, recipient = "Senja", sender = "Penikmat Kopi",
            content = "Matahari terbenam hari ini indah sekali, warnanya jingga seperti jaket yang sering kamu pakai dulu.",
            songTitle = "Golden Hour", songArtist = "Sunset Drive",
            songPreviewUrl = "https://mp3l.jamendo.com/?trackid=1223344&format=mp31",
            songAlbumArtUrl = "https://picsum.photos/seed/208/300/300",
            category = NoteCategory.GENERAL, color = NoteColor.ORANGE,
            createdAt = Clock.System.now().minus(1, DateTimeUnit.HOUR)
        ),
        Note(
            id = -209, recipient = "Dunia", sender = "The Optimist",
            content = "Mungkin hari ini berat, tapi besok adalah kesempatan baru. Jangan menyerah dulu ya.",
            songTitle = "New Dawn", songArtist = "Echoes of Hope",
            songPreviewUrl = "https://mp3l.jamendo.com/?trackid=1334455&format=mp31",
            songAlbumArtUrl = "https://picsum.photos/seed/209/300/300",
            category = NoteCategory.TODO, color = NoteColor.GREEN,
            createdAt = Clock.System.now().minus(4, DateTimeUnit.HOUR)
        ),
        Note(
            id = -210, recipient = "Maya", sender = "Old Friend",
            content = "Sudah berapa lama kita tidak mengobrol? Aku rindu tawa receh kita saat membahas hal-hal tidak penting.",
            songTitle = "Nostalgia", songArtist = "Rewind",
            songPreviewUrl = "https://mp3l.jamendo.com/?trackid=1445566&format=mp31",
            songAlbumArtUrl = "https://picsum.photos/seed/210/300/300",
            category = NoteCategory.PERSONAL, color = NoteColor.PURPLE,
            createdAt = Clock.System.now().minus(168, DateTimeUnit.HOUR)
        ),
        Note(
            id = -211, recipient = "Self", sender = "Future Me",
            content = "Hai diriku di masa lalu, terima kasih sudah bertahan melewati masa-masa tersulit itu. Kamu hebat.",
            songTitle = "Resilience", songArtist = "Inner Voice",
            songPreviewUrl = "https://mp3l.jamendo.com/?trackid=1556677&format=mp31",
            songAlbumArtUrl = "https://picsum.photos/seed/211/300/300",
            category = NoteCategory.IDEAS, color = NoteColor.BLUE,
            createdAt = Clock.System.now().minus(15, DateTimeUnit.HOUR)
        ),
        Note(
            id = -212, recipient = "Bintang", sender = "Night Owl",
            content = "Langit malam ini penuh bintang. Aku sengaja tidak tidur hanya untuk mengagumi keindahannya.",
            songTitle = "Starry Night", songArtist = "Midnight Jazz",
            songPreviewUrl = "https://mp3l.jamendo.com/?trackid=1667788&format=mp31",
            songAlbumArtUrl = "https://picsum.photos/seed/212/300/300",
            category = NoteCategory.GENERAL, color = NoteColor.DEFAULT,
            createdAt = Clock.System.now().minus(30, DateTimeUnit.MINUTE)
        ),
        Note(
            id = -213, recipient = "Andra", sender = "Silent Heart",
            content = "Maaf aku tidak bisa datang ke acaramu. Jarak memang seringkali menjadi penghalang yang menyebalkan.",
            songTitle = "Miles Apart", songArtist = "Distance",
            songPreviewUrl = "https://mp3l.jamendo.com/?trackid=1778899&format=mp31",
            songAlbumArtUrl = "https://picsum.photos/seed/213/300/300",
            category = NoteCategory.PERSONAL, color = NoteColor.RED,
            createdAt = Clock.System.now().minus(48, DateTimeUnit.HOUR)
        ),
        Note(
            id = -214, recipient = "Students", sender = "Elder Senior",
            content = "Kalian yang sedang ujian, semangat ya! Nilai memang penting, tapi kejujuran jauh lebih berharga.",
            songTitle = "Final Exam", songArtist = "Focus Mode",
            songPreviewUrl = "https://mp3l.jamendo.com/?trackid=1889900&format=mp31",
            songAlbumArtUrl = "https://picsum.photos/seed/214/300/300",
            category = NoteCategory.STUDY, color = NoteColor.YELLOW,
            createdAt = Clock.System.now().minus(20, DateTimeUnit.HOUR)
        ),
        Note(
            id = -215, recipient = "Santi", sender = "Little Bird",
            content = "Lagu ini dulu sering kita putar saat di bus sekolah. Masih ingat liriknya?",
            songTitle = "Yellow Bus", songArtist = "School Days",
            songPreviewUrl = "https://mp3l.jamendo.com/?trackid=1990011&format=mp31",
            songAlbumArtUrl = "https://picsum.photos/seed/215/300/300",
            category = NoteCategory.GENERAL, color = NoteColor.PINK,
            createdAt = Clock.System.now().minus(240, DateTimeUnit.HOUR)
        ),
        Note(
            id = -216, recipient = "Rizky", sender = "Unknown",
            content = "Jangan lupa istirahat, kamu sudah bekerja terlalu keras belakangan ini. Ambil napas dalam-dalam.",
            songTitle = "Deep Breath", songArtist = "Zen Master",
            songPreviewUrl = "https://mp3l.jamendo.com/?trackid=1112233&format=mp31",
            songAlbumArtUrl = "https://picsum.photos/seed/216/300/300",
            category = NoteCategory.WORK, color = NoteColor.GREEN,
            createdAt = Clock.System.now().minus(1, DateTimeUnit.HOUR)
        ),
        Note(
            id = -217, recipient = "Tiara", sender = "Cloudy Soul",
            content = "Aku titip rindu lewat angin yang berhembus ke arah rumahmu. Semoga sampai tepat waktu.",
            songTitle = "Wind Whispers", songArtist = "Breeze",
            songPreviewUrl = "https://mp3l.jamendo.com/?trackid=1223344&format=mp31",
            songAlbumArtUrl = "https://picsum.photos/seed/217/300/300",
            category = NoteCategory.PERSONAL, color = NoteColor.BLUE,
            createdAt = Clock.System.now().minus(4, DateTimeUnit.HOUR)
        ),
        Note(
            id = -218, recipient = "Developer", sender = "Bug Hunter",
            content = "Aplikasi ini membantu aku mengeluarkan emosi yang terpendam. Terima kasih ya.",
            songTitle = "Code & Soul", songArtist = "Binary Rhythm",
            songPreviewUrl = "https://mp3l.jamendo.com/?trackid=1334455&format=mp31",
            songAlbumArtUrl = "https://picsum.photos/seed/218/300/300",
            category = NoteCategory.IDEAS, color = NoteColor.DEFAULT,
            createdAt = Clock.System.now().minus(3, DateTimeUnit.HOUR)
        ),
        Note(
            id = -219, recipient = "Bunda", sender = "Rantau Son",
            content = "Selamat ulang tahun Bunda. Maaf tahun ini belum bisa pulang dan kasih pelukan langsung.",
            songTitle = "Mother's Smile", songArtist = "Heartfelt",
            songPreviewUrl = "https://mp3l.jamendo.com/?trackid=1445566&format=mp31",
            songAlbumArtUrl = "https://picsum.photos/seed/219/300/300",
            category = NoteCategory.PERSONAL, color = NoteColor.RED,
            createdAt = Clock.System.now().minus(6, DateTimeUnit.HOUR)
        ),
        Note(
            id = -220, recipient = "Stranger", sender = "Anonymous",
            content = "Kamu yang sedang membaca ini, kamu berharga. Jangan biarkan siapapun bilang sebaliknya.",
            songTitle = "You Matter", songArtist = "Kindness",
            songPreviewUrl = "https://mp3l.jamendo.com/?trackid=1556677&format=mp31",
            songAlbumArtUrl = "https://picsum.photos/seed/220/300/300",
            category = NoteCategory.GENERAL, color = NoteColor.YELLOW,
            createdAt = Clock.System.now().minus(50, DateTimeUnit.MINUTE)
        ),
        Note(
            id = -221, recipient = "Ardi", sender = "Hamba Allah",
            content = "Sabar ya Bro, kehilangan memang berat. Tapi aku yakin lu bisa ngelewatin ini semua.",
            songTitle = "Stronger", songArtist = "Uplift",
            songPreviewUrl = "https://mp3l.jamendo.com/?trackid=1667788&format=mp31",
            songAlbumArtUrl = "https://picsum.photos/seed/221/300/300",
            category = NoteCategory.PERSONAL, color = NoteColor.ORANGE,
            createdAt = Clock.System.now().minus(9, DateTimeUnit.HOUR)
        ),
        Note(
            id = -222, recipient = "Siska", sender = "Coffee Addict",
            content = "Besok pagi di tempat biasa ya? Ada cerita seru yang pengen aku bagi sama kamu.",
            songTitle = "Morning Talk", songArtist = "Cafe Latte",
            songPreviewUrl = "https://mp3l.jamendo.com/?trackid=1778899&format=mp31",
            songAlbumArtUrl = "https://picsum.photos/seed/222/300/300",
            category = NoteCategory.TODO, color = NoteColor.GREEN,
            createdAt = Clock.System.now().minus(14, DateTimeUnit.HOUR)
        ),
        Note(
            id = -223, recipient = "Langit", sender = "Dreamer",
            content = "Aku ingin terbang bebas tanpa beban, seperti awan yang ditiup angin tanpa tujuan.",
            songTitle = "Flight", songArtist = "Atmosphere",
            songPreviewUrl = "https://mp3l.jamendo.com/?trackid=1889900&format=mp31",
            songAlbumArtUrl = "https://picsum.photos/seed/223/300/300",
            category = NoteCategory.IDEAS, color = NoteColor.PURPLE,
            createdAt = Clock.System.now().minus(11, DateTimeUnit.HOUR)
        ),
        Note(
            id = -224, recipient = "Reza", sender = "Gamer",
            content = "Malam ini push rank lagi ga? Jangan cupu kaya semalem ya!",
            songTitle = "Level Up", songArtist = "Pixel Beats",
            songPreviewUrl = "https://mp3l.jamendo.com/?trackid=1991122&format=mp31",
            songAlbumArtUrl = "https://picsum.photos/seed/224/300/300",
            category = NoteCategory.GENERAL, color = NoteColor.DEFAULT,
            createdAt = Clock.System.now().minus(30, DateTimeUnit.MINUTE)
        ),
        Note(
            id = -225, recipient = "Semua", sender = "Midnight Thinker",
            content = "Selamat tidur dunia. Semoga mimpi buruk tidak mampir malam ini.",
            songTitle = "Lullaby", songArtist = "Soft Pillow",
            songPreviewUrl = "https://mp3l.jamendo.com/?trackid=1112244&format=mp31",
            songAlbumArtUrl = "https://picsum.photos/seed/225/300/300",
            category = NoteCategory.GENERAL, color = NoteColor.PINK,
            createdAt = Clock.System.now().minus(5, DateTimeUnit.MINUTE)
        )
    )

    override fun getGlobalLetters(): Flow<List<Note>> = flowOf(dummyGlobalLetters)

    override fun getLetters(): Flow<List<Note>> {
        return queries.getAllNotes()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun searchLetters(query: String): Flow<List<Note>> {
        return getLetters().map { localNotes ->
            val filteredLocal = localNotes.filter {
                it.recipient.contains(query, ignoreCase = true) ||
                it.content.contains(query, ignoreCase = true) ||
                it.songTitle?.contains(query, ignoreCase = true) == true
            }
            val filteredDummy = dummyGlobalLetters.filter {
                it.recipient.contains(query, ignoreCase = true) ||
                it.sender.contains(query, ignoreCase = true) ||
                it.content.contains(query, ignoreCase = true) ||
                it.songTitle?.contains(query, ignoreCase = true) == true
            }
            (filteredLocal + filteredDummy).distinctBy { it.id }
        }
    }

    override suspend fun sendLetter(letter: Note): Boolean {
        queries.insertNote(
            recipient = letter.recipient,
            sender = letter.sender,
            content = letter.content,
            song_title = letter.songTitle,
            song_artist = letter.songArtist,
            song_preview_url = letter.songPreviewUrl,
            song_album_art_url = letter.songAlbumArtUrl,
            category = letter.category.name,
            color = letter.color.name,
            is_pinned = if (letter.isPinned) 1L else 0L,
            created_at = letter.createdAt.toEpochMilliseconds(),
            updated_at = letter.updatedAt.toEpochMilliseconds()
        )
        return true
    }

    override suspend fun getLetterById(id: Long): Note? {
        if (id < 0) return dummyGlobalLetters.find { it.id == id }
        return queries.getNoteById(id).executeAsOneOrNull()?.toDomain()
    }

    override suspend fun deleteLetter(id: Long) = queries.deleteNoteById(id)

    override suspend fun clearHistory() = queries.deleteAllNotes()
}
