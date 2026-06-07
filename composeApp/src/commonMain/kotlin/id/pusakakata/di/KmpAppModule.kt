package id.pusakakata.di

import id.pusakakata.data.repository.ItemRepositoryImpl
import id.pusakakata.domain.repository.ItemRepository
import id.pusakakata.ui.screens.home.HomeViewModel
import id.pusakakata.ui.screens.addedit.AddEditViewModel
import id.pusakakata.ui.screens.detail.DetailViewModel
import id.pusakakata.ui.screens.gacha.GachaViewModel
import id.pusakakata.ui.screens.flashcard.FlashcardViewModel
import id.pusakakata.ui.screens.quiz.QuizViewModel
import id.pusakakata.ui.screens.favorite.FavoriteViewModel
import id.pusakakata.ui.screens.profile.ProfileViewModel
import id.pusakakata.ui.screens.collection.CollectionViewModel
import id.pusakakata.domain.usecase.GachaSystem
import id.pusakakata.domain.model.LegendaryCard
import id.pusakakata.domain.model.Rarity
import id.pusakakata.data.local.PusakaDatabase
import id.pusakakata.data.remote.ApiService
import id.pusakakata.data.remote.GeminiService
import id.pusakakata.core.util.DatabaseDriverFactory
import id.pusakakata.core.network.ApiConfig
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val networkModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    coerceInputValues = true
                })
            }
            install(Logging) {
                level = LogLevel.ALL
                logger = Logger.DEFAULT
            }
        }
    }
    single { ApiService(get()) }
    single { GeminiService(ApiConfig.GEMINI_API_KEY) }
}

val databaseModule = module {
    single {
        val driverFactory: DatabaseDriverFactory = get()
        PusakaDatabase(driverFactory.createDriver())
    }
}

val repositoryModule = module {
    single<ItemRepository> { ItemRepositoryImpl(get(), get(), get()) }
}

val useCaseModule = module {
    single { 
        GachaSystem(
            availableCards = listOf(
                LegendaryCard(
                    "1", "Gajah Mada", "Patih legendaris Majapahit dengan Sumpah Palapa.", Rarity.MYTHIC, "", "Majapahit",
                    "Gajah Mada adalah seorang panglima perang dan tokoh yang sangat berpengaruh pada zaman kerajaan Majapahit. Menurut berbagai sumber mitologi dan sejarah, ia memulai kariernya sebagai prajurit Bhayangkara. Kehebatannya terbukti saat ia mengucapkan Sumpah Palapa, sebuah janji bahwa ia tidak akan memakan buah palapa (menikmati kesenangan duniawi) sebelum berhasil menyatukan seluruh Nusantara di bawah panji Majapahit. Dedikasinya membawa Majapahit ke puncak kejayaan."
                ),
                LegendaryCard(
                    "2", "Malin Kundang", "Anak durhaka yang dikutuk menjadi batu.", Rarity.COMMON, "", "Sumatera Barat",
                    "Kisah Malin Kundang menceritakan tentang seorang pemuda miskin yang merantau ke kota besar dan menjadi kaya raya. Namun, setelah sukses, ia merasa malu akan asal-usulnya dan ibunya yang tua renta. Saat ibunya datang menemui Malin, ia menolaknya dengan kasar di depan istri dan anak buahnya. Kecewa dan sakit hati, sang ibu berdoa kepada Tuhan, dan seketika badai besar datang menghancurkan kapalnya, mengubah Malin menjadi sebongkah batu yang bersujud di Pantai Air Manis."
                ),
                LegendaryCard(
                    "3", "Sangkuriang", "Pembuat perahu legendaris Tangkuban Perahu.", Rarity.RARE, "", "Jawa Barat",
                    "Sangkuriang adalah putra dari Dayang Sumbi. Setelah lama berkelana, ia kembali ke rumah dan jatuh cinta pada seorang wanita cantik tanpa menyadari bahwa itu adalah ibu kandungnya sendiri yang tetap awet muda karena memakan jantung anjing jelmaan dewa. Dayang Sumbi menyadarinya dan memberi syarat mustahil: membangun danau dan perahu dalam satu malam. Hampir berhasil, Sangkuriang marah saat digagalkan oleh kecerdikan Dayang Sumbi dan menendang perahu tersebut hingga terbalik menjadi Gunung Tangkuban Perahu."
                ),
                LegendaryCard(
                    "4", "Nyi Roro Kidul", "Ratu penguasa Laut Selatan Jawa.", Rarity.EPIC, "", "Laut Selatan",
                    "Dikenal sebagai Kanjeng Ratu Kidul, ia adalah sosok penguasa samudera di pantai selatan Jawa. Mitos menyebutkan ia awalnya adalah seorang putri cantik dari kerajaan di Jawa yang mengasingkan diri ke laut karena kutukan atau pengkhianatan. Ia sering digambarkan mengenakan kebaya hijau zamrud yang anggun. Hingga kini, masyarakat pesisir selatan sangat menghormatinya dan memiliki berbagai pantangan, seperti larangan mengenakan pakaian hijau di tepi pantai."
                ),
                LegendaryCard(
                    "5", "Lutung Kasarung", "Pangeran yang dikutuk menjadi kera.", Rarity.RARE, "", "Sunda",
                    "Pangeran Guruminda turun ke bumi dalam wujud kera bernama Lutung Kasarung untuk mencari cinta sejati. Ia bertemu dengan Putri Purbasari yang dibuang ke hutan oleh kakaknya yang jahat, Purbararang. Berkat bantuan kesaktian Lutung Kasarung, Purbasari berhasil kembali ke istana dan membuktikan kebenarannya. Saat ditantang menunjukkan calon suami, Lutung berubah kembali menjadi pangeran yang tampan, membuktikan bahwa ketulusan hati lebih berharga dari wujud fisik."
                ),
                LegendaryCard(
                    "6", "Cindelaras", "Pemuda sakti dengan ayam jago ajaib.", Rarity.COMMON, "", "Jawa Timur",
                    "Cindelaras adalah putra permaisuri yang dibuang ke hutan akibat fitnah selir raja. Ia tumbuh besar dengan ditemani seekor ayam jago sakti yang tak terkalahkan dalam sabung ayam. Ayam tersebut memiliki nyanyian yang mengungkap asal-usul Cindelaras sebagai putra raja Jenggala. Melalui pertandingan adu ayam yang dramatis melawan ayam ayahnya sendiri, kebenaran akhirnya terungkap dan Cindelaras kembali ke istana bersama ibunya."
                ),
                LegendaryCard(
                    "7", "Roro Jonggrang", "Putri peminta seribu candi dalam semalam.", Rarity.EPIC, "", "Prambanan",
                    "Bandung Bondowoso yang sakti ingin menikahi Roro Jonggrang, namun Jonggrang enggan karena Bondowoso telah membunuh ayahnya. Ia meminta syarat seribu candi dibangun dalam satu malam. Bondowoso hampir berhasil dengan bantuan jin, namun Jonggrang memerintahkan warga menumbuk padi dan membakar jerami agar langit terlihat terang seolah fajar tiba. Merasa dicurangi pada candi ke-999, Bondowoso murka dan mengutuk Roro Jonggrang menjadi arca ke-1000 yang kini berada di Candi Prambanan."
                )
            )
        )
    }
}

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::FavoriteViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::CollectionViewModel)
    viewModelOf(::GachaViewModel)
    viewModelOf(::FlashcardViewModel)
    viewModelOf(::QuizViewModel)
    factory { (wordId: String?) -> AddEditViewModel(get(), wordId) }
    factory { (wordId: String) -> DetailViewModel(get(), wordId) }
}

val allPusakaKataModules = listOf(
    networkModule,
    databaseModule,
    repositoryModule,
    useCaseModule,
    viewModelModule
)
