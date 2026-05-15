package com.dailybliss.app.presentation.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailybliss.app.domain.model.ContentBlock
import com.dailybliss.app.domain.model.Moment
import com.dailybliss.app.domain.model.MomentContent
import com.dailybliss.app.domain.usecase.DeleteMomentUseCase
import com.dailybliss.app.domain.usecase.GetMomentByIdUseCase
import com.dailybliss.app.domain.usecase.SaveMomentUseCase
import com.dailybliss.app.presentation.util.FileStorage
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json

class MomentDetailViewModel(
    private val getMomentByIdUseCase: GetMomentByIdUseCase,
    private val saveMomentUseCase: SaveMomentUseCase,
    private val deleteMomentUseCase: DeleteMomentUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<MomentDetailUiState>(MomentDetailUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<MomentDetailEvent>()
    val events = _events.asSharedFlow()

    private var currentMomentId: Long? = null
    private val json = Json { ignoreUnknownKeys = true }

    fun loadMoment(id: Long) {
        if (currentMomentId == id) return
        currentMomentId = id
        
        viewModelScope.launch {
            getMomentByIdUseCase(id).collect { moment ->
                if (moment != null) {
                    val parsedContent = try {
                        if (moment.content.startsWith("{\"blocks\":")) {
                            json.decodeFromString<MomentContent>(moment.content)
                        } else {
                            MomentContent(listOf(ContentBlock.Text(moment.content)))
                        }
                    } catch (e: Exception) {
                        MomentContent(listOf(ContentBlock.Text(moment.content)))
                    }

                    _uiState.update { currentState ->
                        val currentRequestedFocusIndex = if (currentState is MomentDetailUiState.Success) currentState.requestedFocusIndex else null
                        MomentDetailUiState.Success(
                            moment = moment,
                            title = moment.title,
                            contentBlocks = if (parsedContent.blocks.isEmpty()) listOf(ContentBlock.Text("")) else parsedContent.blocks,
                            requestedFocusIndex = currentRequestedFocusIndex
                        )
                    }
                } else {
                    _uiState.value = MomentDetailUiState.NotFound
                }
            }
        }
    }

    fun onTitleChange(newTitle: String) {
        val currentState = _uiState.value
        if (currentState is MomentDetailUiState.Success) {
            _uiState.value = currentState.copy(title = newTitle)
            saveChanges()
        }
    }

    fun onBlockChange(index: Int, block: ContentBlock) {
        val currentState = _uiState.value
        if (currentState is MomentDetailUiState.Success) {
            val newBlocks = currentState.contentBlocks.toMutableList()
            if (index in newBlocks.indices) {
                newBlocks[index] = block
                _uiState.value = currentState.copy(contentBlocks = newBlocks)
                saveChanges()
            }
        }
    }

    fun addTextBlock(afterIndex: Int) {
        val currentState = _uiState.value
        if (currentState is MomentDetailUiState.Success) {
            val newBlocks = currentState.contentBlocks.toMutableList()
            val newIndex = afterIndex + 1
            newBlocks.add(newIndex, ContentBlock.Text(""))
            _uiState.value = currentState.copy(
                contentBlocks = newBlocks,
                requestedFocusIndex = newIndex
            )
            saveChanges()
        }
    }

    fun addImageBlock(bytes: ByteArray, afterIndex: Int) {
        viewModelScope.launch {
            val localPath = FileStorage.saveImage(bytes)
            if (localPath != null) {
                val currentState = _uiState.value
                if (currentState is MomentDetailUiState.Success) {
                    val newBlocks = currentState.contentBlocks.toMutableList()
                    val newIndex = afterIndex + 1
                    newBlocks.add(newIndex, ContentBlock.Image(localPath))
                    _uiState.value = currentState.copy(
                        contentBlocks = newBlocks,
                        requestedFocusIndex = null
                    )
                    saveChanges()
                }
            }
        }
    }

    fun clearFocusRequest() {
        val currentState = _uiState.value
        if (currentState is MomentDetailUiState.Success) {
            _uiState.value = currentState.copy(requestedFocusIndex = null)
        }
    }

    fun removeBlock(index: Int) {
        val currentState = _uiState.value
        if (currentState is MomentDetailUiState.Success) {
            if (currentState.contentBlocks.size > 1) {
                val newBlocks = currentState.contentBlocks.toMutableList()
                newBlocks.removeAt(index)
                val focusBackIndex = if (index > 0) index - 1 else 0
                _uiState.value = currentState.copy(
                    contentBlocks = newBlocks,
                    requestedFocusIndex = focusBackIndex
                )
                saveChanges()
            }
        }
    }

    private fun saveChanges() {
        val state = _uiState.value
        if (state is MomentDetailUiState.Success) {
            viewModelScope.launch {
                val serializedContent = json.encodeToString(MomentContent.serializer(), MomentContent(state.contentBlocks))
                val mainImageUrl = state.contentBlocks.filterIsInstance<ContentBlock.Image>().firstOrNull()?.url
                
                val updatedMoment = state.moment.copy(
                    title = state.title.trim(),
                    content = serializedContent,
                    imageUrl = mainImageUrl ?: state.moment.imageUrl,
                    updatedAt = Clock.System.now()
                )
                saveMomentUseCase(updatedMoment)
            }
        }
    }

    fun deleteMoment() {
        val id = currentMomentId ?: return
        viewModelScope.launch {
            deleteMomentUseCase(id)
            _events.emit(MomentDetailEvent.MomentDeleted)
        }
    }
}

sealed interface MomentDetailUiState {
    data object Loading : MomentDetailUiState
    data class Success(
        val moment: Moment,
        val title: String,
        val contentBlocks: List<ContentBlock>,
        val requestedFocusIndex: Int? = null
    ) : MomentDetailUiState
    data object NotFound : MomentDetailUiState
}

sealed interface MomentDetailEvent {
    data object MomentDeleted : MomentDetailEvent
    data class Error(val message: String) : MomentDetailEvent
}
