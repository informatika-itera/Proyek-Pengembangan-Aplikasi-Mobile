package id.my.sinanonym.mybawanggacha.domain.settings.model

enum class JikanServiceStatusState {
    Checking,
    Active,
    Down
}

data class JikanServiceStatus(
    val state: JikanServiceStatusState = JikanServiceStatusState.Checking,
    val statusCode: Int? = null,
    val type: String = "",
    val message: String = ""
) {
    val isActive: Boolean
        get() = state == JikanServiceStatusState.Active

    companion object {
        val Checking = JikanServiceStatus(
            state = JikanServiceStatusState.Checking
        )

        val Active = JikanServiceStatus(
            state = JikanServiceStatusState.Active,
            message = "Jikan service is active."
        )
    }
}
