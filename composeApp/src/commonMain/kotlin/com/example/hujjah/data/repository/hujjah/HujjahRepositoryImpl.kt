package com.example.hujjah.data.repository.hujjah

import com.example.hujjah.core.network.ApiConfig
import com.example.hujjah.data.local.NoteDatabase
import com.example.hujjah.data.sample.SampleIslamicReferences
import com.example.hujjah.domain.model.islamic.*
import com.example.hujjah.domain.repository.hujjah.HujjahRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class HujjahRepositoryImpl(
    private val httpClient: HttpClient,
    private val database: NoteDatabase
) : HujjahRepository {

    private val queries = database.hujjahQueries
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    override fun getTopics(): Flow<List<TopicOption>> {
        return kotlinx.coroutines.flow.flowOf(SampleIslamicReferences.topics)
    }

    override fun getReferencesByTopic(topicId: String): Flow<List<IslamicReference>> {
        return kotlinx.coroutines.flow.flowOf(
            SampleIslamicReferences.references.filter { reference ->
                reference.topicId == topicId
            }
        )
    }

    override fun getReferenceById(referenceId: String): Flow<IslamicReference?> {
        return kotlinx.coroutines.flow.flowOf(
            SampleIslamicReferences.references.find { reference ->
                reference.id == referenceId
            }
        )
    }

    // ==================== QURAN (OFFLINE-FIRST SSOT) ====================

    override fun getSurahs(forceRefresh: Boolean): Flow<List<SurahItem>> = flow {
        // 1. Emit cached local data first
        val localSurahs = queries.getAllSurahs().executeAsList().map {
            SurahItem(it.number.toInt(), it.name, it.translation, it.numberOfVerses.toInt(), it.revelation, it.asma)
        }
        emit(localSurahs)

        // 2. Fetch remote if local is incomplete (< 114 surahs) or forceRefresh is true
        if (forceRefresh || localSurahs.size < 114) {
            try {
                val response: GadingQuranResponse = httpClient.get("${ApiConfig.QURAN_BASE_URL}/surah").body()
                val remoteSurahs = response.data.map {
                    SurahItem(
                        number = it.number,
                        name = it.name.transliteration?.id ?: it.name.short,
                        translation = it.name.translation.id,
                        numberOfVerses = it.numberOfVerses,
                        revelation = it.revelation.id,
                        asma = it.name.short
                    )
                }

                // Save to local database
                queries.transaction {
                    queries.clearSurahs()
                    remoteSurahs.forEach { surah ->
                        queries.insertSurah(
                            number = surah.number.toLong(),
                            name = surah.name,
                            translation = surah.translation,
                            numberOfVerses = surah.numberOfVerses.toLong(),
                            revelation = surah.revelation,
                            asma = surah.asma
                        )
                    }
                }

                // Emit updated list from local database
                val updatedSurahs = queries.getAllSurahs().executeAsList().map {
                    SurahItem(it.number.toInt(), it.name, it.translation, it.numberOfVerses.toInt(), it.revelation, it.asma)
                }
                emit(updatedSurahs)
            } catch (e: Exception) {
                // If network fails and local cache is also empty, emit fallback mock data
                if (localSurahs.isEmpty()) {
                    val fallbackItems = listOf(
                        SurahItem(1, "Al-Fatihah", "Pembukaan", 7, "Mekah", "الفاتحة"),
                        SurahItem(2, "Al-Baqarah", "Sapi Betina", 286, "Madinah", "البقرة"),
                        SurahItem(3, "Ali 'Imran", "Keluarga 'Imran", 200, "Madinah", "آل عمران"),
                        SurahItem(18, "Al-Kahf", "Gua", 110, "Mekah", "الكهف")
                    )
                    emit(fallbackItems)
                }
            }
        }
    }.flowOn(Dispatchers.Default)

    override fun getSurahDetail(surahNumber: Int, surahName: String, forceRefresh: Boolean): Flow<List<VerseItem>> = flow {
        // 1. Emit cached local data first
        val localVerses = queries.getVersesBySurah(surahNumber.toLong()).executeAsList().map {
            VerseItem(it.number.toInt(), it.arabic, it.translation)
        }
        emit(localVerses)

        val expectedVerses = queries.getAllSurahs().executeAsList().find { it.number == surahNumber.toLong() }?.numberOfVerses?.toInt() ?: 0

        // 2. Fetch remote if local is empty, incomplete, or forceRefresh is true
        if (forceRefresh || localVerses.isEmpty() || localVerses.size < expectedVerses) {
            try {
                val response: GadingSurahDetailResponse = httpClient.get("${ApiConfig.QURAN_BASE_URL}/surah/$surahNumber").body()
                val remoteVerses = response.data.verses.map {
                    VerseItem(
                        number = it.number.inSurah,
                        arabic = it.text.arab,
                        translation = it.translation.id
                    )
                }

                // Save to local database
                queries.transaction {
                    queries.clearVersesBySurah(surahNumber.toLong())
                    remoteVerses.forEach { verse ->
                        queries.insertVerse(
                            surahNumber = surahNumber.toLong(),
                            number = verse.number.toLong(),
                            arabic = verse.arabic,
                            translation = verse.translation
                        )
                    }
                }

                // Emit updated list from local database
                val updatedVerses = queries.getVersesBySurah(surahNumber.toLong()).executeAsList().map {
                    VerseItem(it.number.toInt(), it.arabic, it.translation)
                }
                emit(updatedVerses)
            } catch (e: Exception) {
                // Network failed, if local is also empty, emit fallback
                if (localVerses.isEmpty()) {
                    val fallbackVerses = if (surahNumber == 1) {
                        listOf(
                            VerseItem(1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "Dengan nama Allah Yang Maha Pengasih, Maha Penyayang."),
                            VerseItem(2, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "Segala puji bagi Allah, Tuhan seluruh alam,"),
                            VerseItem(3, "الرَّحْمَٰنِ الرَّحِيمِ", "Yang Maha Pengasih, Maha Penyayang,"),
                            VerseItem(4, "مَالِكِ يَوْمِ الدِّينِ", "Pemilik hari pembalasan."),
                            VerseItem(5, "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ", "Hanya kepada Engkaulah kami menyembah dan hanya kepada Engkaulah kami mohon pertolongan."),
                            VerseItem(6, "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ", "Tunjukilah kami jalan yang lurus,"),
                            VerseItem(7, "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ", "(yaitu) jalan orang-orang yang telah Engkau beri nikmat kepadanya; bukan (jalan) mereka yang dimurkai, dan bukan (pula jalan) mereka yang sesat.")
                        )
                    } else {
                        listOf(
                            VerseItem(1, "الۤمّۤ", "Alif Lam Mim"),
                            VerseItem(2, "ذٰلِكَ الْكِتٰbُ لَا رَيْبَ ۛ فِيْهِ ۛ هُدًى لِّلْمُتَّقِيْنَۙ", "Kitab (Al-Qur'an) ini tidak ada keraguan padanya; petunjuk bagi mereka yang bertakwa,"),
                            VerseItem(3, "الَّذِيْنَ يُؤْمِنُوْنَ بِالْغَيْبِ وَيُقِيْمُوْنَ الصَّلٰوةَ وَمِمَّا رَزَقْنٰهُمْ يُنْفِقُوْنَۙ", "(yaitu) mereka yang beriman kepada yang gaib, melaksanakan shalat, dan menginfakkan sebagian rezeki yang Kami berikan kepada mereka,")
                        )
                    }
                    emit(fallbackVerses)
                }
            }
        }
    }.flowOn(Dispatchers.Default)

    // ==================== HADITH (OFFLINE-FIRST SSOT) ====================

    override fun getHadithBooks(forceRefresh: Boolean): Flow<List<HadithBookItem>> = flow {
        // 1. Emit cached local data first
        val localBooks = queries.getAllHadithBooks().executeAsList().map {
            HadithBookItem(it.id, it.name, it.totalHadith.toInt())
        }
        emit(localBooks)

        // 2. Fetch remote if local is incomplete (< 9 books) or forceRefresh is true
        if (forceRefresh || localBooks.size < 9) {
            try {
                val response: HadithBooksResponse = httpClient.get("${ApiConfig.HADITH_BASE_URL}/books").body()
                val remoteBooks = response.data.map {
                    HadithBookItem(it.id, it.name, it.available)
                }

                // Save to local database
                queries.transaction {
                    queries.clearHadithBooks()
                    remoteBooks.forEach { book ->
                        queries.insertHadithBook(
                            id = book.id,
                            name = book.name,
                            totalHadith = book.totalHadith.toLong()
                        )
                    }
                }

                // Emit updated list
                val updatedBooks = queries.getAllHadithBooks().executeAsList().map {
                    HadithBookItem(it.id, it.name, it.totalHadith.toInt())
                }
                emit(updatedBooks)
            } catch (e: Exception) {
                // Network failed, if local is empty, emit default 9 perawi
                if (localBooks.isEmpty()) {
                    val staticBooks = listOf(
                        HadithBookItem("bukhari", "Shahih Bukhari", 6638),
                        HadithBookItem("muslim", "Shahih Muslim", 4930),
                        HadithBookItem("tirmidzi", "Sunan Tirmidzi", 3625),
                        HadithBookItem("nasai", "Sunan Nasai", 5364),
                        HadithBookItem("abu-daud", "Sunan Abu Daud", 4419),
                        HadithBookItem("ibnu-majah", "Sunan Ibnu Majah", 4285),
                        HadithBookItem("ahmad", "Musnad Ahmad", 4305),
                        HadithBookItem("darimi", "Sunan Darimi", 2949),
                        HadithBookItem("malik", "Muwatta Malik", 1587)
                    )
                    emit(staticBooks)
                }
            }
        }
    }.flowOn(Dispatchers.Default)

    override fun getHadithRange(bookId: String, start: Int, end: Int, forceRefresh: Boolean): Flow<List<HadithItem>> = flow {
        // 1. Emit cached local data first
        val localHadiths = queries.getHadithsByRange(bookId, start.toLong(), end.toLong()).executeAsList().map {
            HadithItem(it.number.toInt(), it.arab, it.translation)
        }
        
        // Count how many we actually have locally in this range
        val expectedCount = end - start + 1
        emit(localHadiths)

        // 2. Fetch remote if local is incomplete or forceRefresh is true
        if (forceRefresh || localHadiths.size < expectedCount) {
            try {
                val response: GadingHadithResponse = httpClient.get("${ApiConfig.HADITH_BASE_URL}/books/$bookId?range=$start-$end").body()
                val remoteHadiths = response.data.hadiths.map {
                    HadithItem(it.number, it.arab, it.id)
                }

                // Save to local database
                queries.transaction {
                    remoteHadiths.forEach { hadith ->
                        queries.insertHadith(
                            bookId = bookId,
                            number = hadith.number.toLong(),
                            arab = hadith.arab,
                            translation = hadith.translation
                        )
                    }
                }

                // Emit updated list from local database
                val updatedHadiths = queries.getHadithsByRange(bookId, start.toLong(), end.toLong()).executeAsList().map {
                    HadithItem(it.number.toInt(), it.arab, it.translation)
                }
                emit(updatedHadiths)
            } catch (e: Exception) {
                // Network failed, if local is empty, emit default sample items
                if (localHadiths.isEmpty()) {
                    val fallbackHadiths = listOf(
                        HadithItem(1, "إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ", "Sesungguhnya amal perbuatan itu disertai niat."),
                        HadithItem(2, "لاَ يُؤْمِنُ أَحَدُكُمْ حَتَّى يُحِبَّ لأَخِيهِ مَا يُحِبُwلِنَفْسِهِ", "Tidak beriman salah seorang di antara kalian sampai ia mencintai saudaranya sebagaimana ia mencintai dirinya sendiri."),
                        HadithItem(3, "الْمُسْلِمُ مَنْ سَلِمَ الْمُسْلِمُونَ مِنْ لِسَانِهِ وَيَدِهِ", "Seorang muslim adalah orang yang lidah dan tangannya tidak menyakiti muslim lain."),
                        HadithItem(4, "مَنْ كَانَ يُؤْمِنُ بِاللَّهِ وَالْيَوْمِ الآخِرِ فَلْيَقُلْ خَيْرًا أَوْ لِيَصْمُتْ", "Barangsiapa beriman kepada Allah dan hari akhir, hendaklah berkata baik atau diam."),
                        HadithItem(5, "الدِّينُ النَّصِيحَةُ", "Agama itu adalah nasihat.")
                    )
                    emit(fallbackHadiths)
                }
            }
        }
    }.flowOn(Dispatchers.Default)

    // ==================== LENS CHAT CACHING (SESSION HISTORY) ====================

    override fun getChatHistory(): Flow<List<ChatMessage>> {
        return queries.getAllChats()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { list ->
                list.map { entity ->
                    ChatMessage(
                        id = entity.id,
                        sender = if (entity.sender == "USER") Sender.USER else Sender.AI,
                        text = entity.text,
                        timestamp = entity.timestamp,
                        references = entity.referencesJson?.let {
                            try {
                                json.decodeFromString<List<IslamicReference>>(it)
                            } catch (e: Exception) {
                                emptyList()
                            }
                        } ?: emptyList(),
                        solutions = entity.solutionsJson?.let {
                            try {
                                json.decodeFromString<List<String>>(it)
                            } catch (e: Exception) {
                                emptyList()
                            }
                        } ?: emptyList()
                    )
                }
            }
    }

    override suspend fun saveChatMessage(message: ChatMessage) {
        withContext(Dispatchers.Default) {
            val refsJson = json.encodeToString(message.references)
            val solsJson = json.encodeToString(message.solutions)
            
            queries.insertChat(
                id = message.id,
                sender = message.sender.name,
                text = message.text,
                timestamp = message.timestamp,
                referencesJson = refsJson,
                solutionsJson = solsJson
            )
        }
    }

    override suspend fun deleteChatMessage(messageId: String) {
        withContext(Dispatchers.Default) {
            queries.deleteChatById(messageId)
        }
    }

    override suspend fun clearChatHistory() {
        withContext(Dispatchers.Default) {
            queries.clearAllChats()
        }
    }
}

// ==================== DTO REPRESENTATIONS ====================

@Serializable
private data class GadingQuranResponse(
    val code: Int,
    val status: String,
    val data: List<GadingSurahItem>
)

@Serializable
private data class GadingSurahItem(
    val number: Int,
    val name: GadingSurahName,
    val numberOfVerses: Int,
    val revelation: GadingRevelation
)

@Serializable
private data class GadingSurahName(
    val short: String,
    val transliteration: GadingTransliteration? = null,
    val translation: GadingTranslation
)

@Serializable
private data class GadingTransliteration(
    val id: String
)

@Serializable
private data class GadingTranslation(
    val id: String
)

@Serializable
private data class GadingRevelation(
    val id: String
)

@Serializable
private data class GadingSurahDetailResponse(
    val code: Int,
    val status: String,
    val data: GadingSurahDetailData
)

@Serializable
private data class GadingSurahDetailData(
    val number: Int,
    val name: GadingSurahName,
    val verses: List<GadingVerseItem>
)

@Serializable
private data class GadingVerseItem(
    val number: GadingVerseNumber,
    val text: GadingVerseText,
    val translation: GadingTranslation
)

@Serializable
private data class GadingVerseNumber(
    val inSurah: Int
)

@Serializable
private data class GadingVerseText(
    val arab: String
)

@Serializable
private data class HadithBooksResponse(
    val code: Int,
    val message: String? = null,
    val data: List<HadithBookItemDto>
)

@Serializable
private data class HadithBookItemDto(
    val name: String,
    val id: String,
    val available: Int
)

@Serializable
private data class GadingHadithResponse(
    val code: Int,
    val message: String? = null,
    val data: GadingHadithData
)

@Serializable
private data class GadingHadithData(
    val name: String,
    val id: String,
    val available: Int,
    val hadiths: List<GadingHadithItem>
)

@Serializable
private data class GadingHadithItem(
    val number: Int,
    val arab: String,
    val id: String
)
