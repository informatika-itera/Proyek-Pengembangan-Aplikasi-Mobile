package com.example.hujjah.presentation.screens.lens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hujjah.data.sample.SampleIslamicReferences
import com.example.hujjah.domain.model.islamic.ChatMessage
import com.example.hujjah.domain.model.islamic.IslamicReference
import com.example.hujjah.domain.model.islamic.Sender
import com.example.hujjah.domain.model.islamic.SourceType
import com.example.hujjah.domain.repository.AIRepository
import com.example.hujjah.domain.repository.hujjah.BookmarkRepository
import com.example.hujjah.domain.repository.hujjah.HujjahRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

data class HujjahLensUiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@Serializable
private data class GeminiIslamicResponse(
    val counselorResponse: String,
    val solutions: List<String> = emptyList(),
    val references: List<GeminiReferenceItem> = emptyList()
)

@Serializable
private data class GeminiReferenceItem(
    val sourceType: String, // QURAN or HADITH
    val title: String,
    val sourceName: String,
    val arabicText: String,
    val translation: String,
    val explanation: String
)

private data class LocalFallbackData(
    val counselorResponse: String,
    val solutions: List<String>,
    val topicTitle: String
)

class HujjahLensViewModel(
    private val hujjahRepository: HujjahRepository,
    private val aiRepository: AIRepository,
    private val bookmarkRepository: BookmarkRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HujjahLensUiState())
    val uiState: StateFlow<HujjahLensUiState> = _uiState.asStateFlow()

    private val jsonParser = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val localFallbackMap = mapOf(
        "anger" to LocalFallbackData(
            counselorResponse = "Assalamualaikum. Saya memahami bahwa rasa kesal dan amarah sedang berkecamuk di dalam dirimu saat ini. Amarah adalah ujian emosi yang manusiawi, namun Islam mengajarkan kita untuk mengendalikannya demi menjaga kedamaian hati. Tariklah napas dalam-dalam, tenangkan pikiranmu, dan marilah kita renungkan nasihat mulia di bawah ini.",
            solutions = listOf(
                "Meredam amarah dengan mengambil wudhu atau merubah posisi tubuh (duduk jika sedang berdiri). (Dasar: HR. Bukhari)",
                "Memilih memaafkan kesalahan orang lain sebagai bentuk akhlak mulia yang dicintai Allah. (Dasar: QS. Ali 'Imran: 134)",
                "Menjaga lisan agar tidak mengeluarkan ucapan yang menyakiti ketika sedang emosi."
            ),
            topicTitle = "Mengendalikan Amarah"
        ),
        "calm" to LocalFallbackData(
            counselorResponse = "Assalamualaikum. Merasa sedih, galau, atau kecewa adalah bagian dari perjalanan hidup. Ketahuilah bahwa kamu tidak sendirian. Allah selalu dekat dengan hamba-Nya dan menanti keluh kesahmu. Semoga dalil-dalil berikut dapat menjadi penyejuk hati yang sedang gundah.",
            solutions = listOf(
                "Memperbanyak zikir dan mengingat Allah dalam setiap hela napas untuk menenangkan hati. (Dasar: QS. Ar-Ra'd: 28)",
                "Meyakini dengan sepenuh jiwa bahwa di balik setiap kesulitan pasti ada kemudahan yang dipersiapkan Allah. (Dasar: QS. Al-Insyirah: 5-6)",
                "Meluangkan waktu untuk berwudhu dan membaca Al-Qur'an secara tartil."
            ),
            topicTitle = "Ketenangan Hati"
        ),
        "sabr" to LocalFallbackData(
            counselorResponse = "Assalamualaikum. Ketika ujian terasa berat dan raga mulai lelah, ingatlah bahwa Allah tidak membebani seseorang melainkan sesuai kesanggupannya. Kesabaranmu dalam menghadapi ujian ini bernilai pahala yang amat besar di sisi-Nya. Semoga pengingat ini menguatkan langkahmu.",
            solutions = listOf(
                "Menjadikan sabar dan shalat khusyuk sebagai penolong utama dalam menghadapi cobaan. (Dasar: QS. Al-Baqarah: 153)",
                "Menghibur diri dengan keyakinan bahwa pahala kesabaran akan disempurnakan Allah tanpa batas. (Dasar: QS. Az-Zumar: 10)",
                "Menghindari keluh kesah yang berlebihan dan tetap berprasangka baik atas takdir Allah."
            ),
            topicTitle = "Sabar"
        ),
        "taubah" to LocalFallbackData(
            counselorResponse = "Assalamualaikum. Pintu ampunan Allah selalu terbuka lebar bagi setiap hamba yang menyadari kekhilafannya. Jangan biarkan rasa bersalah membuatmu menjauh dari rahmat Allah. Bertaubatlah dengan tulus, karena Allah adalah Maha Pengampun lagi Maha Penyayang.",
            solutions = listOf(
                "Mengakui kesalahan dengan penuh penyesalan dan memohon ampunan tulus (istighfar). (Dasar: QS. Az-Zumar: 53)",
                "Melakukan taubatan nasuha (taubat semurni-murninya) dan bertekad kuat tidak mengulangi dosa tersebut. (Dasar: QS. At-Tahrim: 8)",
                "Mengganti perbuatan buruk dengan amal-amal kebajikan di kehidupan sehari-hari."
            ),
            topicTitle = "Taubat"
        ),
        "syukur" to LocalFallbackData(
            counselorResponse = "Assalamualaikum. Alhamdulillah, senantiasa bersyukur atas segala nikmat-Nya adalah kunci kebahagiaan sejati. Ketika hati diliputi rasa cukup dan syukur, Allah menjanjikan keberkahan yang berlipat ganda. Mari kita pupuk rasa syukur ini.",
            solutions = listOf(
                "Mengucapkan syukur dengan lisan (Alhamdulillah) dan menyadari setiap nikmat sekecil apapun. (Dasar: QS. Ibrahim: 7)",
                "Menggunakan nikmat yang diberikan Allah untuk berbuat kebaikan dan menolong sesama hamba-Nya.",
                "Menghindari sifat iri dengki dengan melihat ke bawah dalam urusan keduniaan."
            ),
            topicTitle = "Syukur"
        ),
        "shalat" to LocalFallbackData(
            counselorResponse = "Assalamualaikum. Shalat adalah tiang agama dan sarana utama komunikasi kita dengan Sang Pencipta. Jika rasa malas atau kelalaian mulai melanda, marilah kita perbaiki hubungan shalat kita agar hidup menjadi lebih tertata dan berkah.",
            solutions = listOf(
                "Memohon pertolongan Allah dengan melatih kedisiplinan shalat tepat waktu dan sabar. (Dasar: QS. Al-Baqarah: 45)",
                "Menghindari sifat lalai dan menunda-nunda shalat karena itu adalah kerugian yang besar. (Dasar: QS. Al-Ma'un: 4-5)",
                "Memperbaiki kekhusyukan shalat dengan memahami makna bacaan shalat."
            ),
            topicTitle = "Shalat"
        ),
        "tawakkal_cemas" to LocalFallbackData(
            counselorResponse = "Assalamualaikum. Rasa cemas dan khawatir akan masa depan seringkali menghinggapi hati kita. Di sinilah letak pentingnya tawakkal—menyerahkan segala keputusan kepada Allah setelah kita berusaha maksimal. Percayalah, rencana Allah adalah yang terbaik.",
            solutions = listOf(
                "Menyerahkan segala urusan sepenuhnya kepada Allah (tawakkal) agar hati terasa lapang dan dicukupkan. (Dasar: QS. Ath-Thalaq: 3)",
                "Melatih diri untuk fokus pada ikhtiar hari ini dan melepaskan ketakutan berlebihan akan hari esok.",
                "Memperbanyak doa mohon ketetapan hati dan kelapangan dada."
            ),
            topicTitle = "Tawakkal / Cemas"
        ),
        "ilmu" to LocalFallbackData(
            counselorResponse = "Assalamualaikum. Menuntut ilmu adalah ibadah mulia yang membuka jalan menuju keridaan Allah. Jika rasa malas belajar menghampiri, ingatlah kembali betapa tingginya derajat yang dijanjikan Allah bagi para pencari ilmu. Semoga motivasi ini membakar semangatmu.",
            solutions = listOf(
                "Mengingat janji Allah yang akan meninggikan derajat orang-orang yang berilmu. (Dasar: QS. Al-Mujadilah: 11)",
                "Menyusun jadwal belajar yang teratur dan berdoa memohon perlindungan dari sifat malas.",
                "Niatkan menuntut ilmu untuk diamalkan dan memberikan manfaat bagi umat manusia."
            ),
            topicTitle = "Ilmu"
        ),
        "parents" to LocalFallbackData(
            counselorResponse = "Assalamualaikum. Hubungan dengan orang tua adalah salah satu ladang pahala terbesar bagi kita. Islam menempatkan bakti kepada ibu dan ayah sebagai kewajiban yang sangat utama. Jagalah sikap dan tutur kata kita di hadapan mereka dengan kelembutan.",
            solutions = listOf(
                "Selalu berbakti dan memperlakukan orang tua dengan penuh hormat dan kasih sayang. (Dasar: QS. Al-Isra: 23)",
                "Menjaga lisan agar tidak mengucapkan kata kasar (bahkan sekadar berkata 'ah') kepada mereka. (Dasar: QS. Al-Isra: 23)",
                "Mendoakan kebaikan dan keselamatan bagi kedua orang tua di setiap selesai shalat."
            ),
            topicTitle = "Berbakti Orang Tua"
        ),
        "rezeki" to LocalFallbackData(
            counselorResponse = "Assalamualaikum. Mengenai kekhawatiran rezeki, percayalah bahwa Allah telah menjamin porsi rezeki setiap makhluk-Nya. Tugas kita hanyalah menjemput rezeki tersebut dengan ikhtiar yang halal, disertai dengan ketakwaan yang kokoh.",
            solutions = listOf(
                "Meningkatkan ketakwaan kepada Allah sebagai pembuka pintu rezeki dari arah yang tak disangka. (Dasar: QS. Ath-Thalaq: 2-3)",
                "Meyakini bahwa setiap makhluk bergerak di bumi telah dijamin rezekinya oleh Allah. (Dasar: QS. Hud: 6)",
                "Berusaha (ikhtiar) secara jujur, halal, dan menjauhi transaksi yang diharamkan."
            ),
            topicTitle = "Rezeki"
        )
    )

    init {
        loadChatHistory()
    }

    private fun loadChatHistory() {
        viewModelScope.launch {
            hujjahRepository.getChatHistory().collect { history ->
                if (history.isEmpty()) {
                    val welcomeMsg = ChatMessage(
                        id = "welcome",
                        sender = Sender.AI,
                        text = "Assalamualaikum, saya adalah Hujjah Lens. Ceritakan apa yang sedang mengganjal di hatimu saat ini, atau gunakan filter emosi di bawah. Saya akan mencarikan dalil yang menenangkan jiwamu.",
                        timestamp = Clock.System.now().toEpochMilliseconds()
                    )
                    hujjahRepository.saveChatMessage(welcomeMsg)
                } else {
                    _uiState.value = _uiState.value.copy(
                        messages = history
                    )
                }
            }
        }
    }

    fun onInputTextChanged(value: String) {
        _uiState.value = _uiState.value.copy(inputText = value)
    }

    fun sendUserMessage(text: String) {
        if (text.isBlank()) return

        val userMessage = ChatMessage(
            id = "msg-${Clock.System.now().toEpochMilliseconds()}",
            sender = Sender.USER,
            text = text,
            timestamp = Clock.System.now().toEpochMilliseconds()
        )

        _uiState.value = _uiState.value.copy(
            inputText = "",
            isLoading = true,
            errorMessage = null
        )

        viewModelScope.launch {
            hujjahRepository.saveChatMessage(userMessage)

            val categoryId = identifyCategoryFromInput(text)
            val authenticRefs = SampleIslamicReferences.references.filter { it.topicId == categoryId }
            val formattedRefs = authenticRefs.joinToString("\n") { ref ->
                "- sourceType: ${ref.sourceType.name}\n  sourceName: ${ref.sourceName}\n  arabicText: ${ref.arabicText}\n  translation: ${ref.translation}"
            }

            val systemPrompt = """
                Kamu adalah Hujjah Lens, Konselor Spiritual Islam berbasis AI yang berempati, bijaksana, dan menenangkan. 
                Tugasmu adalah menganalisis keluhan emosional pengguna (seperti kecemasan, kesedihan, kemarahan, keraguan, rasa bersyukur, dll.), memberikan respon konseling yang menenangkan dan relevan, lalu menyusun "Solusi Berdalil" berdasarkan kutipan ayat Al-Qur'an dan Hadis yang disediakan.

                KATEGORI TERDETEKSI: $categoryId
                
                DALIL AUTENTIK YANG WAJIB DIGUNAKAN (Gunakan HANYA dalil di bawah ini, dilarang mengarang/halusinasi dalil lain):
                $formattedRefs

                FORMAT JAWABAN:
                Kamu wajib menjawab dalam bentuk JSON murni dengan format persis seperti di bawah ini, tanpa tambahan teks pembuka markdown atau tanda kutip di luar JSON:
                {
                  "counselorResponse": "Tanggapan konseling yang hangat, berempati, dan menenangkan pengguna...",
                  "solutions": [
                    "Saran praktis pertama yang berlandaskan dalil...",
                    "Saran praktis kedua yang berlandaskan dalil..."
                  ],
                  "references": [
                    {
                      "sourceType": "QURAN", 
                      "title": "Judul Konteks Ayat",
                      "sourceName": "QS. NamaSurah: NomorAyat",
                      "arabicText": "Salin persis Teks Arab yang disediakan",
                      "translation": "Salin persis Terjemahan yang disediakan",
                      "explanation": "Penjelasan singkat relevansi ayat ini dengan kondisi pengguna."
                    }
                  ]
                }
                
                Aturan penting:
                1. Jika keluhan pengguna bernada positif atau rasa bersyukur, respon dengan apresiasi spiritual (Tashakur) dan dalil tentang bersyukur.
                2. Teks Arab wajib ditulis dengan harakat lengkap sesuai yang disediakan.
                3. Pastikan format JSON valid agar aplikasi tidak crash saat memparsing.
                4. Untuk setiap reference, salin `sourceName`, `arabicText`, dan `translation` persis sama dengan yang disediakan di atas.
            """.trimIndent()

            val result = aiRepository.chat(
                message = "$systemPrompt\n\nKondisi/Keluhan Pengguna saat ini: \"$text\""
            )

            result.fold(
                onSuccess = { responseText ->
                    try {
                        val cleanJson = extractJson(responseText)
                        val parsed = jsonParser.decodeFromString<GeminiIslamicResponse>(cleanJson)

                        // Authenticity merger
                        val mappedReferences = parsed.references.map { item ->
                            val localRef = authenticRefs.find { 
                                it.sourceName.replace(" ", "").equals(item.sourceName.replace(" ", ""), ignoreCase = true) 
                            } ?: authenticRefs.firstOrNull()
                            
                            val finalArabic = localRef?.arabicText ?: item.arabicText
                            val finalTranslation = localRef?.translation ?: item.translation
                            val finalSourceName = localRef?.sourceName ?: item.sourceName
                            val finalSourceType = localRef?.sourceType ?: (if (item.sourceType.uppercase() == "QURAN") SourceType.QURAN else SourceType.HADITH)

                            IslamicReference(
                                id = "ref-${Clock.System.now().toEpochMilliseconds()}-${finalSourceName.hashCode()}",
                                sourceType = finalSourceType,
                                title = item.title.ifBlank { localRef?.title ?: "Dalil Hujjah" },
                                sourceName = finalSourceName,
                                arabicText = finalArabic,
                                translation = finalTranslation,
                                explanation = item.explanation.ifBlank { localRef?.explanation ?: "" },
                                topicId = categoryId,
                                topicTitle = localFallbackMap[categoryId]?.topicTitle ?: "Hujjah Lens"
                            )
                        }

                        val aiMessage = ChatMessage(
                            id = "msg-${Clock.System.now().toEpochMilliseconds()}-ai",
                            sender = Sender.AI,
                            text = parsed.counselorResponse,
                            timestamp = Clock.System.now().toEpochMilliseconds(),
                            references = mappedReferences,
                            solutions = parsed.solutions
                        )

                        hujjahRepository.saveChatMessage(aiMessage)
                        _uiState.value = _uiState.value.copy(isLoading = false)
                    } catch (e: Exception) {
                        // If JSON parsing fails, trigger local fallback
                        triggerLocalFallback(text, categoryId)
                    }
                },
                onFailure = { error ->
                    // If network/rate limit failure, trigger local fallback
                    triggerLocalFallback(text, categoryId)
                }
            )
        }
    }

    private fun identifyCategoryFromInput(text: String): String {
        val lowerText = text.lowercase()
        return when {
            lowerText.contains("marah") || lowerText.contains("emosi") || lowerText.contains("kesal") || 
            lowerText.contains("amuk") || lowerText.contains("jengkel") || lowerText.contains("tengkar") || 
            lowerText.contains("benci") || lowerText.contains("murka") || lowerText.contains("temper") -> "anger"
            
            lowerText.contains("sedih") || lowerText.contains("galau") || lowerText.contains("kecewa") || 
            lowerText.contains("hampa") || lowerText.contains("menangis") || lowerText.contains("sakit hati") || 
            lowerText.contains("resah") || lowerText.contains("gelisah") || lowerText.contains("luka") -> "calm"
            
            lowerText.contains("diuji") || lowerText.contains("musibah") || lowerText.contains("capek") || 
            lowerText.contains("lelah") || lowerText.contains("berat") || lowerText.contains("sabar") || 
            lowerText.contains("ujian") || lowerText.contains("sakit") || lowerText.contains("derita") || 
            lowerText.contains("putus asa") || lowerText.contains("mengeluh") -> "sabr"
            
            lowerText.contains("dosa") || lowerText.contains("menyesal") || lowerText.contains("taubat") || 
            lowerText.contains("ampun") || lowerText.contains("istighfar") || lowerText.contains("maksiat") || 
            lowerText.contains("hina") || lowerText.contains("khilaf") || lowerText.contains("buruk") -> "taubah"
            
            lowerText.contains("syukur") || lowerText.contains("terima kasih") || lowerText.contains("alhamdulillah") || 
            lowerText.contains("nikmat") || lowerText.contains("iri") || lowerText.contains("dengki") || 
            lowerText.contains("hasad") || lowerText.contains("beruntung") -> "syukur"
            
            lowerText.contains("shalat") || lowerText.contains("solat") || lowerText.contains("malas shalat") || 
            lowerText.contains("lalai shalat") || lowerText.contains("ibadah") || lowerText.contains("sajadah") || 
            lowerText.contains("wudhu") || lowerText.contains("masjid") -> "shalat"
            
            lowerText.contains("takut") || lowerText.contains("cemas") || lowerText.contains("overthinking") || 
            lowerText.contains("khawatir") || lowerText.contains("masa depan") || lowerText.contains("bingung") || 
            lowerText.contains("ragu") -> "tawakkal_cemas"
            
            lowerText.contains("belajar") || lowerText.contains("ilmu") || lowerText.contains("malas belajar") || 
            lowerText.contains("sekolah") || lowerText.contains("kuliah") || lowerText.contains("ujian") || 
            lowerText.contains("buku") || lowerText.contains("pengetahuan") || lowerText.contains("pandai") || 
            lowerText.contains("pintar") -> "ilmu"
            
            lowerText.contains("orang tua") || lowerText.contains("ibu") || lowerText.contains("ayah") || 
            lowerText.contains("bapak") || lowerText.contains("konflik") || lowerText.contains("durhaka") || 
            lowerText.contains("mama") || lowerText.contains("papa") -> "parents"
            
            lowerText.contains("rezeki") || lowerText.contains("miskin") || lowerText.contains("uang") || 
            lowerText.contains("kerja") || lowerText.contains("nafkah") || lowerText.contains("duit") || 
            lowerText.contains("hutang") || lowerText.contains("ekonomi") || lowerText.contains("sulit uang") -> "rezeki"
            
            else -> "calm"
        }
    }

    private fun triggerLocalFallback(text: String, categoryId: String) {
        val fallbackData = localFallbackMap[categoryId] ?: localFallbackMap["calm"]!!
        val authenticRefs = SampleIslamicReferences.references.filter { it.topicId == categoryId }
        
        val mappedReferences = authenticRefs.map { ref ->
            IslamicReference(
                id = "ref-${Clock.System.now().toEpochMilliseconds()}-${ref.sourceName.hashCode()}",
                sourceType = ref.sourceType,
                title = ref.title,
                sourceName = ref.sourceName,
                arabicText = ref.arabicText,
                translation = ref.translation,
                explanation = ref.explanation,
                topicId = categoryId,
                topicTitle = fallbackData.topicTitle
            )
        }

        val aiMessage = ChatMessage(
            id = "msg-${Clock.System.now().toEpochMilliseconds()}-ai",
            sender = Sender.AI,
            text = fallbackData.counselorResponse,
            timestamp = Clock.System.now().toEpochMilliseconds(),
            references = mappedReferences,
            solutions = fallbackData.solutions
        )

        viewModelScope.launch {
            hujjahRepository.saveChatMessage(aiMessage)
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    private fun extractJson(text: String): String {
        val start = text.indexOf('{')
        val end = text.lastIndexOf('}')
        return if (start != -1 && end != -1 && end > start) {
            text.substring(start, end + 1)
        } else {
            text
        }
    }

    fun saveBookmark(reference: IslamicReference) {
        viewModelScope.launch {
            bookmarkRepository.saveBookmark(reference)
        }
    }

    fun deleteMessage(messageId: String) {
        viewModelScope.launch {
            hujjahRepository.deleteChatMessage(messageId)
        }
    }
}
