package id.my.sinanonym.mybawanggacha.presentation.screens.search

import id.my.sinanonym.mybawanggacha.domain.search.model.MediaSearchFilters
import id.my.sinanonym.mybawanggacha.domain.search.model.MediaSearchItem
import id.my.sinanonym.mybawanggacha.domain.search.model.SearchFilterOption
import id.my.sinanonym.mybawanggacha.domain.search.model.SearchMediaType

internal fun buildActiveFilterLabels(
    filters: MediaSearchFilters,
    filterMetadata: SearchFilterMetadataUiState = SearchFilterMetadataUiState()
): List<String> {
    return buildList {
        filters.query.takeIf { it.isNotBlank() }?.let { add("${SearchText.queryLabel}: $it") }
        filters.type?.takeIf { it.isNotBlank() }?.let { add("${SearchText.typeLabel}: $it") }
        filters.status?.takeIf { it.isNotBlank() }?.let { add("${SearchText.statusLabel}: $it") }
        filters.rating?.takeIf { it.isNotBlank() && filters.mediaType == SearchMediaType.Anime }?.let {
            add("${SearchText.ratingLabel}: $it")
        }
        filters.limit.takeIf { it.isNotBlank() && it != "12" }?.let { add("${SearchText.limitLabel}: $it") }
        filters.score.takeIf { it.isNotBlank() }?.let { add("${SearchText.exactScoreLabel}: $it") }
        filters.minScore.takeIf { it.isNotBlank() }?.let { add("Min: $it") }
        filters.maxScore.takeIf { it.isNotBlank() }?.let { add("Max: $it") }
        filters.genres.takeIf { it.isNotBlank() }?.let {
            add("${SearchText.includedGenreLabel}: ${it.toOptionLabels(filterMetadata.genres)}")
        }
        filters.genresExclude.takeIf { it.isNotBlank() }?.let {
            add("${SearchText.excludedGenreLabel}: ${it.toOptionLabels(filterMetadata.genres)}")
        }
        filters.orderBy?.takeIf { it.isNotBlank() }?.let { add("Order: $it") }
        filters.sort?.takeIf { it.isNotBlank() }?.let { add("${SearchText.sortLabel}: $it") }
        filters.letter.takeIf { it.isNotBlank() }?.let { add("${SearchText.letterLabel}: $it") }
        filters.startDate.takeIf { it.isNotBlank() }?.let { add("Start: $it") }
        filters.endDate.takeIf { it.isNotBlank() }?.let { add("End: $it") }
        if (filters.unapproved) add(SearchText.unapprovedActiveLabel)
        if (filters.mediaType == SearchMediaType.Anime) {
            filters.producers.takeIf { it.isNotBlank() }?.let {
                add("${SearchText.producerIdsLabel}: ${it.toOptionLabels(filterMetadata.related)}")
            }
        } else {
            filters.magazines.takeIf { it.isNotBlank() }?.let {
                add("${SearchText.magazineIdsLabel}: ${it.toOptionLabels(filterMetadata.related)}")
            }
        }
    }
}

internal fun buildSearchSubtitle(item: MediaSearchItem): String {
    val parts = buildList {
        item.type?.takeIf { it.isNotBlank() }?.let { add(it) }
        item.status?.takeIf { it.isNotBlank() }?.let { add(it) }
        item.score?.let { score -> add("Score $score") }
        item.rank?.let { rank -> add("Rank #$rank") }
        item.episodes?.let { episodes -> add("$episodes eps") }
        item.chapters?.let { chapters -> add("$chapters ch") }
        item.volumes?.let { volumes -> add("$volumes vol") }
    }
    return parts.joinToString(" • ").ifBlank { SearchText.missingMetadata }
}

private fun String.toOptionLabels(options: List<SearchFilterOption>): String {
    val optionMap = options.associateBy { option -> option.id.toString() }

    return split(",")
        .map { value -> value.trim() }
        .filter { value -> value.isNotBlank() }
        .joinToString(", ") { value ->
            optionMap[value]?.name ?: "#$value"
        }
}
