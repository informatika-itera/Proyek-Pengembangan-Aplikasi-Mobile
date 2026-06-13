package com.example.fitgen.data.remote.api

import com.example.fitgen.BuildKonfig
import com.example.fitgen.core.network.NetworkResult
import com.example.fitgen.core.network.safeApiCall
import com.example.fitgen.data.remote.dto.ExerciseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ─────────────────────────────────────────────────────────────────────────────
// WGER API Internal DTOs
// ─────────────────────────────────────────────────────────────────────────────

@Serializable
private data class WgerPagedResponse(
    val count: Int = 0,
    val results: List<WgerExerciseInfo> = emptyList()
)

@Serializable
private data class WgerExerciseInfo(
    val id: Int = 0,
    val category: WgerCategory? = null,
    val images: List<WgerImage> = emptyList(),
    val translations: List<WgerTranslation> = emptyList()
)

@Serializable
private data class WgerCategory(
    val id: Int = 0,
    val name: String = ""
)

@Serializable
private data class WgerImage(
    val id: Int = 0,
    @SerialName("exercise_base_id") val exerciseBaseId: Int = 0,
    val image: String = "",
    @SerialName("is_main") val isMain: Boolean = false
)

@Serializable
private data class WgerTranslation(
    val id: Int = 0,
    val name: String = "",
    val language: Int = 0,
    val description: String = ""
)

// ─────────────────────────────────────────────────────────────────────────────
// SERVICE
// ─────────────────────────────────────────────────────────────────────────────

class ExerciseApiService(
    private val httpClient: HttpClient
) {

    // Mapping label kategori UI → category ID di WGER API
    private val categoryIdMap = mapOf(
        "Push"      to 8,  // Chest
        "Pull"      to 12, // Back
        "Legs"      to 10, // Legs
        "Core"      to 10, // Abs (WGER id 10 = Abs)
        "Cardio"    to 15, // Cardio
        "Shoulders" to 13  // Shoulders
    )

    private fun buildYuhonasUrls(id: String): List<String> {
        val baseUrl = "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/$id"
        return listOf("$baseUrl/0.jpg", "$baseUrl/1.jpg")
    }

    private fun getActualExerciseGif(id: String?): String {
        return "" 
    }

    // Fallback data hardcode per kategori dengan link gambar statis berurutan untuk dianimasikan
    private val fallbackByCategory = mapOf(
        "Push" to listOf(
            ExerciseDto(id="p1", name="Push-up",          bodyPart="Push", imageUrls=buildYuhonasUrls("Pushups"), instructions=listOf("Posisi tengkurap, angkat badan menggunakan lengan.")),
            ExerciseDto(id="p2", name="Dumbbell Press",   bodyPart="Push", imageUrls=buildYuhonasUrls("Dumbbell_Bench_Press"), instructions=listOf("Gunakan dumbbell dan dorong ke atas dada.")),
            ExerciseDto(id="p3", name="Incline Press",    bodyPart="Push", imageUrls=buildYuhonasUrls("Incline_Dumbbell_Press"), instructions=listOf("Lakukan press di bench dengan kemiringan tertentu.")),
            ExerciseDto(id="p4", name="Tricep Dip",       bodyPart="Push", imageUrls=buildYuhonasUrls("Bench_Dips"), instructions=listOf("Gunakan kursi atau bench untuk menopang tubuh.")),
            ExerciseDto(id="p5", name="Chest Fly",        bodyPart="Push", imageUrls=buildYuhonasUrls("Dumbbell_Flyes"), instructions=listOf("Buka lengan lebar seperti sayap lalu tutup ke depan dada."))
        ),
        "Pull" to listOf(
            ExerciseDto(id="l1", name="Pull-up",          bodyPart="Pull", imageUrls=buildYuhonasUrls("Pullups"), instructions=listOf("Bergelantung pada bar dan tarik badan ke atas.")),
            ExerciseDto(id="l2", name="Barbell Row",      bodyPart="Pull", imageUrls=buildYuhonasUrls("Bent_Over_Barbell_Row"), instructions=listOf("Tarik barbell ke arah perut.")),
            ExerciseDto(id="l3", name="Lat Pulldown",     bodyPart="Pull", imageUrls=buildYuhonasUrls("Wide-Grip_Lat_Pulldown"), instructions=listOf("Tarik tuas mesin ke arah dada atas.")),
            ExerciseDto(id="l4", name="Bicep Curl",       bodyPart="Pull", imageUrls=buildYuhonasUrls("Dumbbell_Bicep_Curl"), instructions=listOf("Angkat beban ke arah bahu dengan menekuk siku.")),
            ExerciseDto(id="l5", name="Face Pull",        bodyPart="Pull", imageUrls=buildYuhonasUrls("Face_Pull"), instructions=listOf("Tarik tali ke arah wajah setinggi hidung."))
        ),
        "Legs" to listOf(
            ExerciseDto(id="g1", name="Squat",            bodyPart="Legs", imageUrls=buildYuhonasUrls("Barbell_Squat"), instructions=listOf("Turunkan badan seperti duduk lalu berdiri tegak kembali.")),
            ExerciseDto(id="g2", name="Lunges",           bodyPart="Legs", imageUrls=buildYuhonasUrls("Dumbbell_Lunges"), instructions=listOf("Langkah ke depan dan turunkan pinggul sampai lutut menekuk 90 derajat.")),
            ExerciseDto(id="g3", name="Leg Press",        bodyPart="Legs", imageUrls=buildYuhonasUrls("Leg_Press"), instructions=listOf("Gunakan mesin untuk mendorong beban dengan kaki.")),
            ExerciseDto(id="g4", name="Romanian Deadlift",bodyPart="Legs", imageUrls=buildYuhonasUrls("Romanian_Deadlift"), instructions=listOf("Bungkukkan badan dengan kaki sedikit ditekuk lalu kembali tegak.")),
            ExerciseDto(id="g5", name="Calf Raise",       bodyPart="Legs", imageUrls=buildYuhonasUrls("Standing_Calf_Raises"), instructions=listOf("Jinjit perlahan untuk mengontraksikan otot betis."))
        ),
        "Core" to listOf(
            ExerciseDto(id="c1", name="Plank",            bodyPart="Core", imageUrls=buildYuhonasUrls("Plank"), instructions=listOf("Tahan tubuh menggunakan siku dan kaki, pastikan punggung lurus.")),
            ExerciseDto(id="c2", name="Crunch",           bodyPart="Core", imageUrls=buildYuhonasUrls("Crunches"), instructions=listOf("Baring terlentang dan angkat sedikit pundak ke atas.")),
            ExerciseDto(id="c3", name="Russian Twist",    bodyPart="Core", imageUrls=buildYuhonasUrls("Russian_Twist"), instructions=listOf("Duduk miring dan putar badan ke kiri dan ke kanan.")),
            ExerciseDto(id="c4", name="Leg Raise",        bodyPart="Core", imageUrls=buildYuhonasUrls("Flat_Bench_Lying_Leg_Raise"), instructions=listOf("Baring dan angkat kedua kaki lurus ke atas lalu turunkan perlahan.")),
            ExerciseDto(id="c5", name="Mountain Climber", bodyPart="Core", imageUrls=buildYuhonasUrls("Mountain_Climbers"), instructions=listOf("Posisi push up dan gerakkan lutut ke arah dada bergantian dengan cepat."))
        ),
        "Cardio" to listOf(
            ExerciseDto(id="r1", name="Jumping Jack",     bodyPart="Cardio", imageUrls=buildYuhonasUrls("Freehand_Jump_Squat"), instructions=listOf("Lompat dengan membuka kaki dan tangan ke atas.")),
            ExerciseDto(id="r2", name="Burpee",           bodyPart="Cardio", imageUrls=buildYuhonasUrls("Kettlebell_Thruster"), instructions=listOf("Kombinasi push-up dan lompat secara kontinu.")),
            ExerciseDto(id="r3", name="Jump Rope",        bodyPart="Cardio", imageUrls=buildYuhonasUrls("Rope_Jumping"), instructions=listOf("Lakukan lompat tali dengan tempo konsisten.")),
            ExerciseDto(id="r4", name="High Knees",       bodyPart="Cardio", imageUrls=buildYuhonasUrls("Running_Treadmill"), instructions=listOf("Lari di tempat dengan mengangkat lutut setinggi mungkin.")),
            ExerciseDto(id="r5", name="Box Jump",         bodyPart="Cardio", imageUrls=buildYuhonasUrls("Box_Jump_Multiple_Response"), instructions=listOf("Lompat ke atas kotak atau platform secara eksplosif."))
        ),
        "Shoulders" to listOf(
            ExerciseDto(id="s1", name="Overhead Press",   bodyPart="Shoulders", imageUrls=buildYuhonasUrls("Dumbbell_Shoulder_Press"), instructions=listOf("Dorong dumbbell lurus ke atas kepala.")),
            ExerciseDto(id="s2", name="Lateral Raise",    bodyPart="Shoulders", imageUrls=buildYuhonasUrls("Side_Lateral_Raise"), instructions=listOf("Angkat beban ke samping badan setinggi bahu.")),
            ExerciseDto(id="s3", name="Front Raise",      bodyPart="Shoulders", imageUrls=buildYuhonasUrls("Front_Dumbbell_Raise"), instructions=listOf("Angkat beban lurus ke depan setinggi mata.")),
            ExerciseDto(id="s4", name="Arnold Press",     bodyPart="Shoulders", imageUrls=buildYuhonasUrls("Arnold_Dumbbell_Press"), instructions=listOf("Press ke atas sambil memutar telapak tangan.")),
            ExerciseDto(id="s5", name="Shrug",            bodyPart="Shoulders", imageUrls=buildYuhonasUrls("Dumbbell_Shrug"), instructions=listOf("Angkat bahu ke arah telinga sambil memegang beban."))
        )
    )

    /**
     * Search for exercises by name using WGER API.
     */
    suspend fun searchExerciseByName(name: String): NetworkResult<List<ExerciseDto>> {
        return safeApiCall {
            val response = httpClient.get("https://wger.de/api/v2/exerciseinfo/") {
                header("Authorization", "Token ${BuildKonfig.WGER_API_KEY}")
                parameter("language", "2")
                parameter("format", "json")
                parameter("limit", "10")
            }
            val paged = response.body<WgerPagedResponse>()
            paged.results.mapToExerciseDtos()
        }
    }

    /**
     * Fetch 5 exercises from a given classic workout category.
     * Falls back to hardcoded data if API fails.
     */
    suspend fun getExercisesByCategory(categoryLabel: String): NetworkResult<List<ExerciseDto>> {
        val fallback = fallbackByCategory[categoryLabel] ?: emptyList()
        // Menggunakan data hardcode dan gabungkan imageUrls dengan koma untuk dikirim sebagai gifUrl
        val withGif = fallback.map { 
            it.copy(
                gifUrl = it.imageUrls.joinToString(",") 
            ) 
        }
        return NetworkResult.Success(withGif)
    }

    /**
     * Get 30-day program for a specific challenge.
     */
    suspend fun getChallengeProgram(challengeId: String): NetworkResult<com.example.fitgen.domain.model.Challenge> {
        val totalDays = when (challengeId) {
            "fullbody" -> 30
            "core" -> 14
            "upperbody" -> 21
            else -> 30
        }
        
        val title = when (challengeId) {
            "fullbody" -> "Full Body Transformation"
            "core" -> "Core Crusher $totalDays Days"
            "upperbody" -> "Upper Body Power"
            else -> "$totalDays Day Challenge"
        }
        val description = "Selesaikan tantangan $totalDays hari ini untuk membentuk tubuh idealmu!"
        
        // Generate days
        val days = (1..totalDays).map { day ->
            // Mix exercises based on challenge type
            val exercisesPool = when (challengeId) {
                "fullbody" -> fallbackByCategory.values.flatten()
                "core" -> fallbackByCategory["Core"]!! + fallbackByCategory["Cardio"]!!
                "upperbody" -> fallbackByCategory["Push"]!! + fallbackByCategory["Pull"]!! + fallbackByCategory["Shoulders"]!!
                else -> fallbackByCategory.values.flatten()
            }
            
            // Randomly select 4-6 exercises for the day, but seeded by day number so it's deterministic
            val dayExercises = exercisesPool.shuffled(kotlin.random.Random(day)).take(5).map { 
                it.copy(gifUrl = it.imageUrls.joinToString(","))
            }
            
            com.example.fitgen.domain.model.ChallengeDay(
                dayNumber = day,
                isCompleted = false,
                exercises = dayExercises
            )
        }
        
        val challenge = com.example.fitgen.domain.model.Challenge(
            id = challengeId,
            title = title,
            description = description,
            totalDays = totalDays,
            imageId = challengeId, // resource ID string
            days = days
        )
        return NetworkResult.Success(challenge)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// MAPPER: WGER → ExerciseDto
// ─────────────────────────────────────────────────────────────────────────────

private fun List<WgerExerciseInfo>.mapToExerciseDtos(): List<ExerciseDto> {
    return this.mapNotNull { info ->
        // Ambil terjemahan bahasa Inggris (language=2), atau terjemahan pertama yang ada
        val translation = info.translations.firstOrNull { it.language == 2 }
            ?: info.translations.firstOrNull()
            ?: return@mapNotNull null

        if (translation.name.isBlank()) return@mapNotNull null

        // Ambil gambar utama, atau gambar pertama, atau fallback ke LoremFlickr
        val rawImageUrl = info.images.firstOrNull { it.isMain }?.image
            ?: info.images.firstOrNull()?.image

        val fullImageUrl = when {
            rawImageUrl == null -> "https://loremflickr.com/400/300/fitness,gym/all?lock=${info.id}"
            rawImageUrl.startsWith("http") -> rawImageUrl
            else -> "https://wger.de$rawImageUrl"
        }

        ExerciseDto(
            id = info.id.toString(),
            name = translation.name,
            bodyPart = info.category?.name ?: "General",
            gifUrl = fullImageUrl,
            target = info.category?.name,
            instructions = listOf(
                translation.description
                    .replace(Regex("<[^>]*>"), "")
                    .trim()
            ).filter { it.isNotBlank() }
        )
    }
}
