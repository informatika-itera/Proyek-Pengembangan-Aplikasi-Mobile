package com.dailybliss.app.presentation.screens.addnote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailybliss.app.domain.model.ContentBlock
import com.dailybliss.app.domain.model.Moment
import com.dailybliss.app.domain.model.MomentContent
import com.dailybliss.app.domain.usecase.GetMomentByIdUseCase
import com.dailybliss.app.domain.usecase.SaveMomentUseCase
import com.dailybliss.app.presentation.util.FileStorage
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json

class CreateMomentViewModel(
    private val saveMomentUseCase: SaveMomentUseCase,
    private val getMomentByIdUseCase: GetMomentByIdUseCase,
    private val fileStorage: FileStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateMomentUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<CreateMomentEvent>()
    val events = _events.asSharedFlow()

    private var currentMomentId: Long? = null
    private val json = Json { ignoreUnknownKeys = true }

    fun loadMoment(id: Long) {
        if (currentMomentId == id) return
        currentMomentId = id
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val moment = getMomentByIdUseCase(id).first()
            
            moment?.let {
                val parsedContent = try {
                    if (it.content.startsWith("{\"blocks\":")) {
                        json.decodeFromString<MomentContent>(it.content)
                    } else {
                        MomentContent(listOf(ContentBlock.Text(it.content)))
                    }
                } catch (e: Exception) {
                    MomentContent(listOf(ContentBlock.Text(it.content)))
                }

                _uiState.update { state ->
                    state.copy(
                        title = it.title,
                        contentBlocks = if (parsedContent.blocks.isEmpty()) listOf(ContentBlock.Text("")) else parsedContent.blocks,
                        imageUrl = it.imageUrl,
                        isLoading = false,
                        isEditMode = true,
                        createdAt = it.createdAt
                    )
                }
            }
        }
    }

    fun onTitleChange(title: String) {
        _uiState.update { it.copy(title = title, titleError = null) }
    }

    fun onBlockChange(index: Int, block: ContentBlock) {
        _uiState.update { state ->
            val newBlocks = state.contentBlocks.toMutableList()
            if (index in newBlocks.indices) {
                newBlocks[index] = block
                state.copy(contentBlocks = newBlocks)
            } else state
        }
    }

    fun addTextBlock(afterIndex: Int? = null) {
        _uiState.update { state ->
            val newBlocks = state.contentBlocks.toMutableList()
            val newBlock = ContentBlock.Text("")
            val newIndex = if (afterIndex != null && afterIndex + 1 <= newBlocks.size) {
                afterIndex + 1
            } else {
                newBlocks.size
            }
            newBlocks.add(newIndex, newBlock)
            state.copy(
                contentBlocks = newBlocks,
                requestedFocusIndex = newIndex
            )
        }
    }

    fun addImageBlock(bytes: ByteArray, afterIndex: Int? = null) {
        viewModelScope.launch {
            val localPath = fileStorage.saveImage(bytes)
            if (localPath != null) {
                _uiState.update { state ->
                    val newBlocks = state.contentBlocks.toMutableList()
                    val newBlock = ContentBlock.Image(localPath)
                    val newIndex = if (afterIndex != null && afterIndex + 1 <= newBlocks.size) {
                        afterIndex + 1
                    } else {
                        newBlocks.size
                    }
                    newBlocks.add(newIndex, newBlock)
                    state.copy(
                        contentBlocks = newBlocks,
                        requestedFocusIndex = null
                    )
                }
            }
        }
    }

    fun clearFocusRequest() {
        _uiState.update { it.copy(requestedFocusIndex = null) }
    }

    fun removeBlock(index: Int) {
        _uiState.update { state ->
            if (state.contentBlocks.size > 1) {
                val newBlocks = state.contentBlocks.toMutableList()
                newBlocks.removeAt(index)
                val focusBackIndex = if (index > 0) index - 1 else 0
                state.copy(
                    contentBlocks = newBlocks,
                    requestedFocusIndex = focusBackIndex
                )
            } else {
                state.copy(contentBlocks = listOf(ContentBlock.Text("")))
            }
        }
    }

    fun saveMoment() {
        val state = _uiState.value
        if (state.title.isBlank() && state.contentBlocks.all { it is ContentBlock.Text && it.text.isBlank() }) {
            _uiState.update { it.copy(titleError = "Tuliskan sesuatu...") }
            return
        }
        
        viewModelScope.launch {
            val serializedContent = json.encodeToString(MomentContent.serializer(), MomentContent(state.contentBlocks))
            val mainImageUrl = state.contentBlocks.filterIsInstance<ContentBlock.Image>().firstOrNull()?.url
            
            val moment = Moment(
                id = currentMomentId ?: 0,
                title = state.title.trim(),
                content = serializedContent,
                imageUrl = mainImageUrl ?: state.imageUrl,
                createdAt = if (currentMomentId == null) Clock.System.now() else state.createdAt,
                updatedAt = Clock.System.now()
            )
            
            val newId = saveMomentUseCase(moment)
            if (currentMomentId == null) {
                currentMomentId = newId
            }
            _events.emit(CreateMomentEvent.MomentSaved)
        }
    }
}

data class CreateMomentUiState(
    val title: String = "",
    val contentBlocks: List<ContentBlock> = listOf(ContentBlock.Text("")),
    val imageUrl: String? = null,
    val isLoading: Boolean = false,
    val isEditMode: Boolean = false,
    val titleError: String? = null,
    val createdAt: Instant = Clock.System.now(),
    val requestedFocusIndex: Int? = null
)

sealed interface CreateMomentEvent {
    data object MomentSaved : CreateMomentEvent
    data class Error(val message: String) : CreateMomentEvent
}
