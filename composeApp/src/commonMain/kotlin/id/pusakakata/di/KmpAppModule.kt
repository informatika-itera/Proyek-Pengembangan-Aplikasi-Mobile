package id.pusakakata.di

import id.pusakakata.data.repository.ItemRepositoryImpl
import id.pusakakata.domain.repository.ItemRepository
import id.pusakakata.ui.screens.home.HomeViewModel
import id.pusakakata.ui.screens.addedit.AddEditViewModel
import id.pusakakata.ui.screens.detail.DetailViewModel
import id.pusakakata.ui.screens.gacha.GachaViewModel
import id.pusakakata.ui.screens.flashcard.FlashcardViewModel
import id.pusakakata.ui.screens.quiz.QuizViewModel
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
                LegendaryCard("1", "Gajah Mada", "Patih legendaris Majapahit dengan Sumpah Palapa.", Rarity.MYTHIC, "", "Majapahit"),
                LegendaryCard("2", "Malin Kundang", "Anak durhaka yang dikutuk menjadi batu.", Rarity.COMMON, "", "Sumatera Barat"),
                LegendaryCard("3", "Sangkuriang", "Pembuat perahu legendaris Tangkuban Perahu.", Rarity.RARE, "", "Jawa Barat"),
                LegendaryCard("4", "Nyi Roro Kidul", "Ratu penguasa Laut Selatan Jawa.", Rarity.EPIC, "", "Laut Selatan"),
                LegendaryCard("5", "Lutung Kasarung", "Pangeran yang dikutuk menjadi kera.", Rarity.RARE, "", "Sunda"),
                LegendaryCard("6", "Cindelaras", "Pemuda sakti dengan ayam jago ajaib.", Rarity.COMMON, "", "Jawa Timur"),
                LegendaryCard("7", "Roro Jonggrang", "Putri peminta seribu candi dalam semalam.", Rarity.EPIC, "", "Prambanan")
            )
        )
    }
}

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
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
