package id.pusakakata.presentation.screens.addedit

data class AddEditUiState(
    val wordId: String? = null,
    val term: String = "",
    val definition: String = "",
    val category: String = "Umum",
    val example: String = "",
    val isSuccess: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val canSave: Boolean get() = term.isNotBlank() && definition.isNotBlank()
}
