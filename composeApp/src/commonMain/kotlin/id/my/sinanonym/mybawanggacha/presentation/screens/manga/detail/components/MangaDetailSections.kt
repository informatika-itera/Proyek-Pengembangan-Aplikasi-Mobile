package id.my.sinanonym.mybawanggacha.presentation.screens.manga.detail.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import id.my.sinanonym.mybawanggacha.domain.manga.model.MangaDetail
import id.my.sinanonym.mybawanggacha.domain.manga.model.MangaRelationEntry
import id.my.sinanonym.mybawanggacha.presentation.components.AnimatedSectionContent
import id.my.sinanonym.mybawanggacha.presentation.components.MBGSideRailItem

internal enum class MangaDetailSection(
    val key: String,
    val label: String,
    val icon: ImageVector
) {
    Overview("overview", "Overview", Icons.Default.MenuBook),
    Synopsis("synopsis", "Sinopsis", Icons.Default.Article),
    Info("info", "Info", Icons.Default.Info),
    Relations("relations", "Relations", Icons.Default.Link)
}

internal fun mangaDetailRailItems(): List<MBGSideRailItem> = MangaDetailSection.entries.map { section ->
    MBGSideRailItem(
        key = section.key,
        label = section.label,
        icon = section.icon
    )
}

@Composable
internal fun MangaDetailContent(
    manga: MangaDetail,
    selectedSection: MangaDetailSection,
    onRelationEntryClick: (MangaRelationEntry) -> Unit,
    onPosterClick: () -> Unit = {},
    onTitleCopied: () -> Unit = {}
) {
    AnimatedSectionContent(
        targetState = selectedSection,
        indexOf = { it.ordinal },
        modifier = Modifier.fillMaxSize(),
        label = "MangaDetailSectionTransition"
    ) { section ->
        when (section) {
            MangaDetailSection.Overview -> MangaOverviewSection(
                manga = manga,
                onPosterClick = onPosterClick,
                onTitleCopied = onTitleCopied
            )
            MangaDetailSection.Synopsis -> MangaSynopsisSection(manga)
            MangaDetailSection.Info -> MangaInfoSection(manga)
            MangaDetailSection.Relations -> MangaRelationsSection(
                manga = manga,
                onEntryClick = onRelationEntryClick
            )
        }
    }
}
