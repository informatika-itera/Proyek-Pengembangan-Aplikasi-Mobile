package id.my.sinanonym.mybawanggacha.domain.settings.model

enum class NetworkMode(
    val label: String,
    val description: String
) {
    Auto(
        label = "Auto",
        description = "Gunakan network jika tersedia, fallback ke cache saat gagal."
    ),
    OfflineOnly(
        label = "Offline only",
        description = "Jangan melakukan request Jikan; hanya gunakan cache lokal."
    );

    val allowsNetwork: Boolean
        get() = this == Auto

    companion object {
        fun fromString(value: String?): NetworkMode {
            return entries.firstOrNull { mode ->
                mode.name.equals(value, ignoreCase = true)
            } ?: Auto
        }
    }
}
