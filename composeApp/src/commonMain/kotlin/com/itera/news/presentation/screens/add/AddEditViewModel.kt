package com.itera.news.presentation.screens.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itera.news.domain.model.Article
import com.itera.news.domain.repository.NewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.random.Random

class AddEditViewModel(
    private val repository: NewsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddEditUiState>(AddEditUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()

    private val _source = MutableStateFlow("")
    val source = _source.asStateFlow()

    private val _category = MutableStateFlow("Netral")
    val category = _category.asStateFlow()

    private var currentUrl: String? = null
    private var isEditing = false

    fun initArticle(url: String?) {
        if (url == null || url.isEmpty()) {
            isEditing = false
            return
        }
        isEditing = true
        currentUrl = url
        viewModelScope.launch {
            _uiState.value = AddEditUiState.Loading
            repository.getArticleByUrl(url)
                .catch { e -> _uiState.value = AddEditUiState.Error(e.message ?: "Failed to load") }
                .collect { article ->
                    if (article != null) {
                        _title.value = article.title
                        _description.value = article.description
                        _source.value = article.sourceName
                        _category.value = article.category
                        _uiState.value = AddEditUiState.Idle
                    } else {
                        _uiState.value = AddEditUiState.Error("Article not found")
                    }
                }
        }
    }

    fun onTitleChange(newTitle: String) { _title.value = newTitle }
    fun onDescriptionChange(newDesc: String) { _description.value = newDesc }
    fun onSourceChange(newSource: String) { _source.value = newSource }
    fun onCategoryChange(newCat: String) { _category.value = newCat }

    fun saveArticle() {
        if (_title.value.isBlank() || _description.value.isBlank()) {
            _uiState.value = AddEditUiState.Error("Title and description cannot be empty")
            return
        }

        viewModelScope.launch {
            _uiState.value = AddEditUiState.Loading
            try {
                val articleUrl = currentUrl ?: "custom-${Clock.System.now().toEpochMilliseconds()}-${Random.nextInt()}"
                
                val article = Article(
                    title = _title.value,
                    description = _description.value,
                    url = articleUrl,
                    imageUrl = "https://via.placeholder.com/150", // default image for custom
                    publishedAt = Clock.System.now().toString(),
                    sourceName = _source.value.ifBlank { "Custom" },
                    category = _category.value
                )

                if (isEditing) {
                    repository.updateArticle(article)
                } else {
                    repository.saveArticle(article)
                }
                
                _uiState.value = AddEditUiState.Success
            } catch (e: Exception) {
                _uiState.value = AddEditUiState.Error(e.message ?: "Failed to save")
            }
        }
    }
}
