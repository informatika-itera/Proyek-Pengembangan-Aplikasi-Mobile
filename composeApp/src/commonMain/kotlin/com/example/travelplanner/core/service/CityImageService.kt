package com.example.travelplanner.core.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Singleton service (Koin `single`) that provides photo URLs for cities.
 *
 * Strategy (priority order):
 *  1. Cache hit → return instantly, guarantees ALL screens show the SAME image.
 *  2. Wikipedia `pageimages` API for hardcoded landmark article → verified 640px thumbnail.
 *  3. Wikipedia search for unknown city → `pageimages` API.
 *  4. loremflickr fallback → deterministic, always loads, city-keyword-matched.
 */
class CityImageService(private val httpClient: HttpClient) {

    private val json = Json { ignoreUnknownKeys = true }

    // ── Shared in-memory cache ────────────────────────────────────────────────
    // Mutex ensures thread-safety. Koin `single` → all ViewModels share this cache
    // → same city ALWAYS returns the same URL across Home, Perjalanan, and Detail.
    private val cache = HashMap<String, String>()
    private val mutex = Mutex()

    /** Returns a city photo URL. Cached after first call. Never throws, never returns null. */
    /** Returns a city photo URL. Cached after first call. Never throws, never returns null. */
    suspend fun getImageUrl(cityName: String): String {
        val normalized = cityName.lowercase().trim()

        // 1. Curated static check first (instant, high-quality, stable)
        val staticImg = getStaticImage(normalized)
        if (staticImg != null) {
            mutex.withLock { cache[normalized] = staticImg }
            return staticImg
        }

        // 2. Fast path: return cached URL
        mutex.withLock { cache[normalized] }?.let { return it }

        // 3. Slow path: fetch best available image
        val url = fetchBestImage(cityName)

        mutex.withLock { cache[normalized] = url }
        return url
    }

    /** Returns cached Wikipedia photo URL immediately if available, otherwise deterministic loremflickr. */
    fun getImmediateUrl(cityName: String): String {
        val normalized = cityName.lowercase().trim()
        val staticImg = getStaticImage(normalized)
        if (staticImg != null) return staticImg
        
        return cache[normalized] ?: loremflickr(cityName)
    }

    @Suppress("CyclomaticComplexMethod")
    private fun getStaticImage(city: String): String? {
        val q = city.lowercase().trim()
        return when {
            // Bali & Nusa Tenggara
            q.contains("bali") && !q.contains("balikpapan")       -> "https://images.unsplash.com/photo-1537996194471-e657df975ab4?q=80&w=800&auto=format&fit=crop"
            q.contains("ubud")                                     -> "https://images.unsplash.com/photo-1552832230-c0197dd311b5?q=80&w=800&auto=format&fit=crop"
            q.contains("uluwatu")                                  -> "https://images.unsplash.com/photo-1542856391-010fb87dcfed?q=80&w=800&auto=format&fit=crop"
            q.contains("kintamani")                                -> "https://images.unsplash.com/photo-1505993597083-3bd19f7c1f27?q=80&w=800&auto=format&fit=crop"
            q.contains("lombok") || q.contains("gili")             -> "https://images.unsplash.com/photo-1588598126747-d5d1c312cb69?q=80&w=800&auto=format&fit=crop"
            q.contains("labuan bajo") || q.contains("komodo")     -> "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?q=80&w=800&auto=format&fit=crop"
            q.contains("flores") || q.contains("kelimutu")         -> "https://images.unsplash.com/photo-1505993597083-3bd19f7c1f27?q=80&w=800&auto=format&fit=crop"
            q.contains("sumba")                                    -> "https://images.unsplash.com/photo-1516690561799-46d8f74f9abf?q=80&w=800&auto=format&fit=crop"
            
            // Jawa
            q.contains("jakarta") || q.contains("tangerang") || q.contains("bekasi") || q.contains("serpong") -> "https://images.unsplash.com/photo-1583037189850-1921ae7c6c22?q=80&w=800&auto=format&fit=crop"
            q.contains("yogya") || q.contains("jogja") || q.contains("borobudur") || q.contains("prambanan") -> "https://images.unsplash.com/photo-1626266842869-d4c62bf6a246?q=80&w=800&auto=format&fit=crop"
            q.contains("bandung") || q.contains("lembang") || q.contains("ciwidey") -> "https://images.unsplash.com/photo-1549468057-5b7fa1a41d7a?q=80&w=800&auto=format&fit=crop"
            q.contains("bromo") || q.contains("malang")            -> "https://images.unsplash.com/photo-1596701062351-8c2c14d1fdd0?q=80&w=800&auto=format&fit=crop"
            q.contains("surabaya")                                 -> "https://images.unsplash.com/photo-1582298538104-fc76911790c3?q=80&w=800&auto=format&fit=crop"
            q.contains("semarang") || q.contains("lawang sewu")    -> "https://images.unsplash.com/photo-1563245372-f21724e3856d?q=80&w=800&auto=format&fit=crop"
            q.contains("dieng")                                    -> "https://images.unsplash.com/photo-1508193638397-1c4234db14d8?q=80&w=800&auto=format&fit=crop"
            q.contains("bogor")                                    -> "https://images.unsplash.com/photo-1582298538104-fc76911790c3?q=80&w=800&auto=format&fit=crop"
            q.contains("solo") || q.contains("surakarta")         -> "https://images.unsplash.com/photo-1626266842869-d4c62bf6a246?q=80&w=800&auto=format&fit=crop"

            // Sumatera
            q.contains("toba") || q.contains("samosir")           -> "https://images.unsplash.com/photo-1627848604928-86d1bfd0b674?q=80&w=800&auto=format&fit=crop"
            q.contains("medan")                                    -> "https://images.unsplash.com/photo-1616198943315-095bbabf090a?q=80&w=800&auto=format&fit=crop"
            q.contains("padang") || q.contains("bukittinggi")      -> "https://images.unsplash.com/photo-1536098561742-ca998e48cbcc?q=80&w=800&auto=format&fit=crop"
            q.contains("palembang")                                -> "https://images.unsplash.com/photo-1596464716127-f2a82984de30?q=80&w=800&auto=format&fit=crop"
            q.contains("belitung") || q.contains("bangka")         -> "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?q=80&w=800&auto=format&fit=crop"
            q.contains("aceh") || q.contains("banda aceh")        -> "https://images.unsplash.com/photo-1596464716127-f2a82984de30?q=80&w=800&auto=format&fit=crop"
            q.contains("lampung")                                  -> "https://images.unsplash.com/photo-1549468057-5b7fa1a41d7a?q=80&w=800&auto=format&fit=crop"

            // Kalimantan & Sulawesi & Papua
            q.contains("balikpapan") || q.contains("samarinda")   -> "https://images.unsplash.com/photo-1582298538104-fc76911790c3?q=80&w=800&auto=format&fit=crop"
            q.contains("makassar")                                 -> "https://images.unsplash.com/photo-1597074866923-dc0589150358?q=80&w=800&auto=format&fit=crop"
            q.contains("manado") || q.contains("bunaken")          -> "https://images.unsplash.com/photo-1544551763-46a013bb70d5?q=80&w=800&auto=format&fit=crop"
            q.contains("raja ampat")                               -> "https://images.unsplash.com/photo-1516690561799-46d8f74f9abf?q=80&w=800&auto=format&fit=crop"
            q.contains("toraja")                                   -> "https://images.unsplash.com/photo-1605538032432-a9f0c8d9baac?q=80&w=800&auto=format&fit=crop"

            // International
            q.contains("singapore") || q.contains("singapura")    -> "https://images.unsplash.com/photo-1525625293386-3f8f99389edd?q=80&w=800&auto=format&fit=crop"
            q.contains("kuala lumpur") || q.contains("kl")        -> "https://images.unsplash.com/photo-1595438601894-6b9415714392?q=80&w=800&auto=format&fit=crop"
            q.contains("bangkok")                                  -> "https://images.unsplash.com/photo-1508009603885-50cf7c579365?q=80&w=800&auto=format&fit=crop"
            q.contains("tokyo")                                    -> "https://images.unsplash.com/photo-1503899036084-c55cdd92da26?q=80&w=800&auto=format&fit=crop"
            q.contains("kyoto")                                    -> "https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?q=80&w=800&auto=format&fit=crop"
            q.contains("osaka")                                    -> "https://images.unsplash.com/photo-1590253205779-7a08b5329381?q=80&w=800&auto=format&fit=crop"
            q.contains("seoul")                                    -> "https://images.unsplash.com/photo-1538481199705-c710c4e965fc?q=80&w=800&auto=format&fit=crop"
            q.contains("paris")                                    -> "https://images.unsplash.com/photo-1502602898657-3e91760cbb34?q=80&w=800&auto=format&fit=crop"
            q.contains("london")                                   -> "https://images.unsplash.com/photo-1513635269975-59663e0ac1ad?q=80&w=800&auto=format&fit=crop"
            q.contains("rome") || q.contains("roma")              -> "https://images.unsplash.com/photo-1552832230-c0197dd311b5?q=80&w=800&auto=format&fit=crop"
            q.contains("dubai")                                    -> "https://images.unsplash.com/photo-1512453979798-5ea266f8880c?q=80&w=800&auto=format&fit=crop"
            q.contains("sydney")                                   -> "https://images.unsplash.com/photo-1506973035872-a4ec16b8e8d9?q=80&w=800&auto=format&fit=crop"
            q.contains("new york")                                 -> "https://images.unsplash.com/photo-1496442226666-8d4d0e62e6e9?q=80&w=800&auto=format&fit=crop"
            else                                                   -> null
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  loremflickr — public so ViewModels can set an immediate placeholder
    // ─────────────────────────────────────────────────────────────────────────

    /** Deterministic, always-available image URL for [city]. No network call needed. */
    fun loremflickr(city: String): String {
        val q = city.lowercase().trim()
        val (kw, seed) = cityKeywords(q)
        return "https://loremflickr.com/800/500/$kw/all?lock=$seed"
    }

    // ─────────────────────────────────────────────────────────────────────────

    private suspend fun fetchBestImage(city: String): String {
        // Step 1: Hardcoded landmark article → pageimages API → 640px thumbnail
        val article = hardcodedArticle(city)
        if (article != null) {
            val img = wikiPageImage(article)
            if (img != null) return img
        }

        // Step 2: Wikipedia search for unknown cities
        val searchImg = searchWikiImage(city)
        if (searchImg != null) return searchImg

        // Step 3: loremflickr — deterministic, city-keyword-matched
        return loremflickr(city)
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Wikipedia pageimages API — gives EXACT verified thumbnail at requested size
    //  Unlike REST API, no URL manipulation needed; thumbnail URL always valid.
    // ─────────────────────────────────────────────────────────────────────────

    private suspend fun wikiPageImage(article: String): String? {
        return try {
            val encoded = article.replace(" ", "_")
            val resp: String = httpClient.get(
                "https://en.wikipedia.org/w/api.php?" +
                "action=query&titles=$encoded&prop=pageimages&pithumbsize=640" +
                "&format=json&pilimit=1&redirects=1"
            ) {
                header("User-Agent", "TravelPlannerApp/1.0 (educational project)")
            }.body()

            // Step-by-step parsing to avoid Kotlin ASI issue with bracket access
            val root     = json.parseToJsonElement(resp).jsonObject
            val query    = root.get("query")?.jsonObject ?: return null
            val pages    = query.get("pages")?.jsonObject ?: return null
            val page     = pages.values.firstOrNull()?.jsonObject ?: return null
            val thumb    = page.get("thumbnail")?.jsonObject ?: return null
            val source   = thumb.get("source")?.jsonPrimitive?.content ?: return null

            if (source.contains("upload.wikimedia.org") && !isAdminImage(source)) source else null

        } catch (_: Exception) { null }
    }

    /** Search Wikipedia for a city, then get its lead image via pageimages API. */
    private suspend fun searchWikiImage(cityName: String): String? {
        return try {
            val query = cityName.trim().replace(" ", "+")
            val searchResp: String = httpClient.get(
                "https://en.wikipedia.org/w/api.php?" +
                "action=query&list=search&srsearch=${query}+city+landmark" +
                "&format=json&srlimit=5&srnamespace=0"
            ) {
                header("User-Agent", "TravelPlannerApp/1.0 (educational project)")
            }.body()

            val root       = json.parseToJsonElement(searchResp).jsonObject
            val queryObj   = root.get("query")?.jsonObject ?: return null
            val searchArr  = queryObj.get("search")?.jsonArray ?: return null

            val titles = searchArr
                .mapNotNull { elem -> elem.jsonObject.get("title")?.jsonPrimitive?.content }
                .take(3)

            if (titles.isEmpty()) return null

            // Try each result until we find one with an image
            for (title in titles) {
                val img = wikiPageImage(title)
                if (img != null && !isAdminImage(img)) return img
            }
            null
        } catch (_: Exception) { null }
    }

    private fun isAdminImage(url: String): Boolean {
        val lower = url.lowercase()
        return listOf(
            "locator_map", "location_map", "blank_map", "relief_map", "topographic_map",
            "coat_of_arms", "flag_of_", "_stub", "noimage", "commons-logo", "wikidata",
            "administrative_division", "location_indonesia", "location_java"
        ).any { lower.contains(it) }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  City → Wikipedia article map (Indonesia-focused + international)
    // ─────────────────────────────────────────────────────────────────────────

    @Suppress("CyclomaticComplexMethod")
    private fun hardcodedArticle(city: String): String? {
        val q = city.lowercase().trim()
        return when {
            // Bali
            q.contains("uluwatu")                                  -> "Pura_Luhur_Uluwatu"
            q.contains("ubud")                                     -> "Tegallalang_Rice_Terrace"
            q.contains("kintamani")                                -> "Mount_Batur"
            q.contains("seminyak") || q.contains("kuta")          -> "Kuta,_Bali"
            q.contains("sanur")                                    -> "Sanur,_Bali"
            q.contains("nusa dua")                                 -> "Nusa_Dua"
            q.contains("bali") && !q.contains("balikpapan")       -> "Pura_Ulun_Danu_Beratan"
            // Jawa Tengah
            q.contains("borobudur")                                -> "Borobudur"
            q.contains("prambanan")                                -> "Prambanan"
            q.contains("dieng")                                    -> "Dieng_Plateau"
            q.contains("yogya") || q.contains("jogja")            -> "Borobudur"
            q.contains("semarang")                                 -> "Lawang_Sewu"
            q.contains("solo") || q.contains("surakarta")         -> "Surakarta_Hadiningrat_Palace"
            q.contains("karimunjawa")                              -> "Karimunjawa"
            // Jawa Timur
            q.contains("bromo")                                    -> "Mount_Bromo"
            q.contains("ijen")                                     -> "Ijen"
            q.contains("batu") && q.contains("malang").not()      -> "Batu,_East_Java"
            q.contains("malang")                                   -> "Mount_Bromo"
            q.contains("surabaya")                                 -> "Surabaya"
            q.contains("madura")                                   -> "Madura_Island"
            // Jawa Barat
            q.contains("bandung")                                  -> "Tangkuban_Perahu"
            q.contains("lembang")                                  -> "Lembang,_West_Java"
            q.contains("pangandaran")                              -> "Pangandaran,_West_Java"
            q.contains("bogor")                                    -> "Bogor_Botanical_Gardens"
            q.contains("sukabumi") || q.contains("pelabuhan ratu") -> "Pelabuhan_Ratu"
            q.contains("garut")                                    -> "Papandayan"
            q.contains("ciwidey")                                  -> "Kawah_Putih"
            q.contains("anyer") || q.contains("carita")           -> "Krakatoa"
            // Jakarta + Banten
            q.contains("jakarta")                                  -> "Monas"
            q.contains("tangerang") || q.contains("serpong")      -> "Jakarta"
            q.contains("bekasi")                                   -> "Jakarta"
            // Sumatera
            q.contains("toba") || q.contains("samosir")           -> "Lake_Toba"
            q.contains("medan")                                    -> "Maimun_Palace"
            q.contains("berastagi")                                -> "Berastagi"
            q.contains("bukit lawang") || q.contains("gunung leuser") -> "Gunung_Leuser_National_Park"
            q.contains("aceh") || q.contains("banda aceh")        -> "Baiturrahman_Grand_Mosque"
            q.contains("sabang")                                   -> "Sabang,_Indonesia"
            q.contains("padang")                                   -> "Minangkabau_culture"
            q.contains("bukittinggi")                              -> "Jam_Gadang"
            q.contains("harau")                                    -> "Harau_Valley"
            q.contains("palembang")                                -> "Ampera_Bridge"
            q.contains("lampung")                                  -> "Way_Kambas_National_Park"
            q.contains("belitung")                                 -> "Belitung"
            q.contains("bangka")                                   -> "Bangka_Island"
            q.contains("pekanbaru") || q.contains("riau")         -> "Riau_Islands"
            q.contains("batam")                                    -> "Batam"
            q.contains("bintan")                                   -> "Bintan_Island"
            q.contains("jambi")                                    -> "Muara_Jambi_Temples"
            q.contains("bengkulu")                                 -> "Bengkulu"
            // Kalimantan
            q.contains("balikpapan")                               -> "Borneo"
            q.contains("samarinda")                                -> "Borneo"
            q.contains("nusantara") || q.contains("ikn")          -> "East_Kalimantan"
            q.contains("pontianak")                                -> "Borneo"
            q.contains("banjarmasin")                              -> "Borneo"
            q.contains("palangkaraya") || q.contains("palangka")  -> "Borneo"
            q.contains("kutai") || q.contains("tanjung puting")   -> "Tanjung_Puting_National_Park"
            // Sulawesi
            q.contains("makassar") || q.contains("ujung pandang") -> "Fort_Rotterdam"
            q.contains("toraja")                                   -> "Tongkonan"
            q.contains("manado")                                   -> "Bunaken"
            q.contains("bunaken")                                  -> "Bunaken"
            q.contains("wakatobi")                                 -> "Wakatobi_National_Park"
            q.contains("kendari")                                  -> "Southeast_Sulawesi"
            q.contains("palu")                                     -> "Central_Sulawesi"
            // Nusa Tenggara
            q.contains("lombok")                                   -> "Mount_Rinjani"
            q.contains("gili")                                     -> "Gili_Islands"
            q.contains("sumbawa")                                  -> "Sumbawa"
            q.contains("mataram")                                  -> "Mataram,_West_Nusa_Tenggara"
            q.contains("labuan bajo") || q.contains("labuhan bajo") -> "Komodo_National_Park"
            q.contains("komodo")                                   -> "Komodo_dragon"
            q.contains("flores")                                   -> "Kelimutu"
            q.contains("sumba")                                    -> "Sumba"
            q.contains("kupang")                                   -> "Kupang"
            // Maluku + Papua
            q.contains("raja ampat")                               -> "Raja_Ampat_Islands"
            q.contains("manokwari")                                -> "Manokwari"
            q.contains("sorong")                                   -> "Sorong"
            q.contains("jayapura")                                 -> "Jayapura"
            q.contains("wamena") || q.contains("baliem")          -> "Baliem_Valley"
            q.contains("ambon")                                    -> "Ambon_Island"
            q.contains("ternate")                                  -> "Ternate"
            q.contains("banda")                                    -> "Banda_Islands"
            // Asia Tenggara
            q.contains("singapore") || q.contains("singapura")    -> "Marina_Bay_Sands"
            q.contains("kuala lumpur") || q.contains("kl")        -> "Petronas_Towers"
            q.contains("penang")                                   -> "George_Town,_Penang"
            q.contains("langkawi")                                 -> "Langkawi"
            q.contains("bangkok")                                  -> "Grand_Palace,_Bangkok"
            q.contains("phuket")                                   -> "Phuket_Province"
            q.contains("krabi")                                    -> "Krabi_Province"
            q.contains("chiang mai")                               -> "Chiang_Mai"
            q.contains("hanoi")                                    -> "Hoan_Kiem_Lake"
            q.contains("ho chi minh") || q.contains("saigon")     -> "Ho_Chi_Minh_City"
            q.contains("ha long") || q.contains("halong")         -> "Halong_Bay"
            q.contains("hoi an")                                   -> "Hội_An"
            q.contains("manila")                                   -> "Intramuros"
            q.contains("boracay")                                  -> "Boracay"
            q.contains("palawan")                                  -> "Palawan"
            q.contains("phnom penh")                               -> "Phnom_Penh"
            q.contains("siem reap") || q.contains("angkor")       -> "Angkor_Wat"
            q.contains("yangon")                                   -> "Yangon"
            q.contains("bagan")                                    -> "Bagan"
            // Asia Timur
            q.contains("tokyo")                                    -> "Senso-ji"
            q.contains("kyoto")                                    -> "Fushimi_Inari-taisha"
            q.contains("osaka")                                    -> "Osaka_Castle"
            q.contains("hiroshima")                                -> "Hiroshima_Peace_Memorial"
            q.contains("mount fuji") || q.contains("fuji")        -> "Mount_Fuji"
            q.contains("seoul")                                    -> "Gyeongbokgung"
            q.contains("jeju")                                     -> "Jeju_Island"
            q.contains("beijing") || q.contains("peking")         -> "Forbidden_City"
            q.contains("shanghai")                                 -> "The_Bund"
            q.contains("hong kong")                                -> "Victoria_Harbour"
            q.contains("taipei")                                   -> "Taipei_101"
            // Eropa
            q.contains("paris")                                    -> "Eiffel_Tower"
            q.contains("london")                                   -> "Tower_Bridge"
            q.contains("rome") || q.contains("roma")              -> "Colosseum"
            q.contains("barcelona")                                -> "Sagrada_Família"
            q.contains("amsterdam")                                -> "Amsterdam_canal"
            q.contains("vienna") || q.contains("wina")            -> "Schönbrunn_Palace"
            q.contains("prague") || q.contains("praha")           -> "Prague"
            q.contains("istanbul")                                 -> "Hagia_Sophia"
            q.contains("athens") || q.contains("athena")          -> "Parthenon"
            q.contains("santorini")                                -> "Santorini"
            q.contains("venice") || q.contains("venesia")         -> "Grand_Canal,_Venice"
            // Timur Tengah
            q.contains("dubai")                                    -> "Burj_Khalifa"
            q.contains("abu dhabi")                                -> "Sheikh_Zayed_Grand_Mosque"
            // Amerika
            q.contains("new york")                                 -> "Statue_of_Liberty"
            q.contains("los angeles") || q.contains("la,")        -> "Hollywood_Sign"
            q.contains("miami")                                    -> "Miami_Beach"
            q.contains("las vegas")                                -> "Las_Vegas_Strip"
            q.contains("san francisco")                            -> "Golden_Gate_Bridge"
            q.contains("cancun")                                   -> "Cancún"
            // Australia
            q.contains("sydney")                                   -> "Sydney_Opera_House"
            q.contains("melbourne")                                -> "Melbourne"
            q.contains("brisbane")                                 -> "Brisbane"
            q.contains("gold coast")                               -> "Gold_Coast,_Queensland"
            else                                                   -> null
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  loremflickr city → keyword mapping
    // ─────────────────────────────────────────────────────────────────────────

    @Suppress("CyclomaticComplexMethod")
    private fun cityKeywords(q: String): Pair<String, Int> = when {
        q.contains("uluwatu")                                -> "uluwatu,temple,bali,cliff" to 11
        q.contains("ubud")                                   -> "ubud,bali,rice,terrace" to 8
        q.contains("bali") && !q.contains("balikpapan")     -> "bali,temple,rice,terrace" to 5
        q.contains("yogya") || q.contains("jogja")          -> "borobudur,yogyakarta,java" to 21
        q.contains("borobudur")                              -> "borobudur,temple,java" to 24
        q.contains("prambanan")                              -> "prambanan,temple,java" to 27
        q.contains("jakarta")                                -> "jakarta,monas,indonesia,city" to 30
        q.contains("bandung")                                -> "bandung,flower,java,city" to 33
        q.contains("lembang")                                -> "lembang,highland,java" to 36
        q.contains("bromo")                                  -> "mount,bromo,volcano,java,sunrise" to 42
        q.contains("malang")                                 -> "malang,east,java,city" to 45
        q.contains("surabaya")                               -> "surabaya,java,hero,monument" to 51
        q.contains("solo") || q.contains("surakarta")       -> "solo,java,batik,keraton" to 54
        q.contains("semarang")                               -> "semarang,lawang,sewu,colonial" to 57
        q.contains("dieng")                                  -> "dieng,plateau,java,misty" to 60
        q.contains("bogor")                                  -> "bogor,botanical,garden,java" to 63
        q.contains("palembang")                              -> "palembang,ampera,bridge,sumatra" to 69
        q.contains("padang")                                 -> "padang,minangkabau,sumatra" to 72
        q.contains("bukittinggi")                            -> "bukittinggi,clock,tower,sumatra" to 75
        q.contains("medan")                                  -> "medan,maimun,palace,sumatra" to 78
        q.contains("toba") || q.contains("samosir")         -> "lake,toba,sumatra,island" to 81
        q.contains("aceh") || q.contains("banda aceh")      -> "aceh,baiturrahman,mosque" to 87
        q.contains("lampung")                                -> "lampung,way,kambas,elephant" to 90
        q.contains("belitung")                               -> "belitung,beach,granite,sea" to 93
        q.contains("balikpapan")                             -> "balikpapan,borneo,city" to 99
        q.contains("banjarmasin")                            -> "banjarmasin,floating,market,river" to 105
        q.contains("makassar")                               -> "makassar,fort,rotterdam,sulawesi" to 108
        q.contains("toraja")                                 -> "toraja,tongkonan,sulawesi" to 111
        q.contains("manado") || q.contains("bunaken")       -> "bunaken,coral,reef,sulawesi" to 114
        q.contains("wakatobi")                               -> "wakatobi,sea,coral,indonesia" to 117
        q.contains("raja ampat")                             -> "raja,ampat,island,sea,papua" to 120
        q.contains("lombok")                                 -> "lombok,rinjani,mountain,indonesia" to 132
        q.contains("gili")                                   -> "gili,island,beach,lombok" to 135
        q.contains("labuan bajo") || q.contains("komodo")   -> "komodo,dragon,labuan,bajo" to 141
        q.contains("flores")                                 -> "flores,kelimutu,lake,indonesia" to 147
        q.contains("singapore") || q.contains("singapura")  -> "singapore,marina,bay,sands" to 150
        q.contains("kuala lumpur")                           -> "kuala,lumpur,petronas,tower" to 153
        q.contains("bangkok")                                -> "bangkok,temple,thailand,grand,palace" to 156
        q.contains("tokyo")                                  -> "tokyo,japan,shibuya,city" to 159
        q.contains("kyoto")                                  -> "kyoto,japan,temple,fushimi" to 162
        q.contains("osaka")                                  -> "osaka,castle,japan" to 165
        q.contains("paris")                                  -> "paris,eiffel,tower,france" to 168
        q.contains("london")                                 -> "london,big,ben,bridge,england" to 171
        q.contains("rome") || q.contains("roma")            -> "rome,colosseum,italy" to 174
        q.contains("dubai")                                  -> "dubai,burj,khalifa,uae" to 177
        q.contains("new york")                               -> "new,york,times,square,usa" to 180
        q.contains("sydney")                                 -> "sydney,opera,house,australia" to 183
        else -> {
            val kw = q.trim().replace(" ", ",").take(40)
            val seed = (q.hashCode().and(0x7FFFFFFF) % 500) + 1
            "$kw,travel,city,tourism" to seed
        }
    }
}
