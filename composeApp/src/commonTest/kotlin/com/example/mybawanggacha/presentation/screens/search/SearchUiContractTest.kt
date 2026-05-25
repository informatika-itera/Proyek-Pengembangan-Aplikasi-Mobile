package com.example.mybawanggacha.presentation.screens.search

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SearchUiContractTest {
    @Test
    fun searchTestTags_shouldBeStableAndNonEmpty() {
        val tags = listOf(
            SearchTestTags.queryField,
            SearchTestTags.searchButton,
            SearchTestTags.filterButton,
            SearchTestTags.mediaDropdown,
            SearchTestTags.resultCard
        )

        assertEquals(tags.distinct(), tags)
        assertTrue(tags.all { it.isNotBlank() })
    }

    @Test
    fun searchText_filterButton_shouldShowActiveCountOnlyWhenNeeded() {
        assertEquals("Filter", SearchText.filterButton(0))
        assertEquals("Filter (3)", SearchText.filterButton(3))
    }
}
