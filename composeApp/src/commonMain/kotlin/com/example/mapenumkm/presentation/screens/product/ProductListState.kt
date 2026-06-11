package com.example.mapenumkm.presentation.screens.product

import com.example.mapenumkm.domain.model.Note
import com.example.mapenumkm.domain.model.NoteCategory

data class ProductListState(
    val products: List<Note> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: NoteCategory? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
