package id.pusakakata.di

import id.pusakakata.data.repository.WordRepositoryImpl
import id.pusakakata.domain.repository.WordRepository
import id.pusakakata.ui.screens.home.HomeViewModel
import id.pusakakata.ui.screens.addedit.AddEditViewModel
import id.pusakakata.ui.screens.detail.DetailViewModel
import id.pusakakata.ui.screens.gacha.GachaViewModel
import id.pusakakata.ui.screens.flashcard.FlashcardViewModel
import id.pusakakata.domain.usecase.GachaSystem
import id.pusakakata.domain.model.LegendaryCard
import id.pusakakata.domain.model.Rarity
import id.pusakakata.data.local.PusakaDatabase
import id.pusakakata.core.util.DatabaseDriverFactory
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind

val databaseModule = module {
    single {
        val driverFactory: DatabaseDriverFactory = get()
        PusakaDatabase(driverFactory.createDriver())
    }
}

val repositoryModule = module {
    singleOf(::WordRepositoryImpl) bind WordRepository::class
}

val useCaseModule = module {
    single { 
        GachaSystem(
            availableCards = listOf(
                LegendaryCard("1", "Keris Mpu Gandring", "Keris legendaris yang haus darah.", Rarity.MYTHIC, "", "Singasari"),
                LegendaryCard("2", "Kujang", "Senjata tradisional khas Jawa Barat.", Rarity.RARE, "", "Jawa Barat"),
                LegendaryCard("3", "Rencong", "Simbol keberanian rakyat Aceh.", Rarity.RARE, "", "Aceh"),
                LegendaryCard("4", "Mandau", "Senjata tajam suku Dayak.", Rarity.EPIC, "", "Kalimantan"),
                LegendaryCard("5", "Badik", "Senjata tradisional Bugis-Makassar.", Rarity.COMMON, "", "Sulawesi")
            )
        )
    }
}

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::GachaViewModel)
    viewModelOf(::FlashcardViewModel)
    factory { (wordId: String?) -> AddEditViewModel(get(), wordId) }
    factory { (wordId: String) -> DetailViewModel(get(), wordId) }
}

val allPusakaKataModules = listOf(
    databaseModule,
    repositoryModule,
    useCaseModule,
    viewModelModule
)
