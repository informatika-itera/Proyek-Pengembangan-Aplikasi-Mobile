package id.my.sinanonym.mybawanggacha.domain.settings.model

enum class AppColorScheme(
    val label: String,
    val description: String,
    val lightSwatches: List<String>,
    val darkSwatches: List<String>
) {
    CodeGeass(
        label = "Code Geass",
        description = "C.C green untuk light, Lelouch purple-black untuk dark.",
        lightSwatches = listOf("#30746B", "#D1EF9D", "#697FA6", "#B3A45C", "#DFE3EB", "#F2F7FD"),
        darkSwatches = listOf("#9489CC", "#5051BF", "#F4E4BC", "#AD2748", "#2A2D35", "#0F0F12")
    ),
    PakHabib(
        label = "Pak Habib",
        description = "Material baseline: ungu aman, netral, tidak neko-neko.",
        lightSwatches = listOf("#6750A4", "#EADDFF", "#625B71", "#7D5260", "#E7E0EC", "#FFFBFE"),
        darkSwatches = listOf("#D0BCFF", "#4F378B", "#CCC2DC", "#EFB8C8", "#49454F", "#1C1B1F")
    ),
    Gruvbox(
        label = "Gruvbox",
        description = "Retro warm palette: earthy, low contrast, terminal friendly.",
        lightSwatches = listOf("#FBF1C7", "#79740E", "#B57614", "#427B58", "#D5C4A1", "#3C3836"),
        darkSwatches = listOf("#282828", "#B8BB26", "#FABD2F", "#8EC07C", "#504945", "#EBDBB2")
    ),
    Catppuccin(
        label = "Catppuccin",
        description = "Latte untuk light, Mocha untuk dark.",
        lightSwatches = listOf("#EFF1F5", "#8839EF", "#1E66F5", "#40A02B", "#FE640B", "#4C4F69"),
        darkSwatches = listOf("#1E1E2E", "#CBA6F7", "#89B4FA", "#A6E3A1", "#FAB387", "#CDD6F4")
    ),
    HatsuneMiku(
        label = "Hatsune Miku",
        description = "Miku cyan/teal dengan aksen pink dan neutral charcoal.",
        lightSwatches = listOf("#F2F8FA", "#86CECB", "#137A7F", "#E12885", "#BEC8D1", "#373B3E"),
        darkSwatches = listOf("#0F1214", "#86CECB", "#137A7F", "#E12885", "#5C7478", "#F2F8FA")
    );

    fun swatches(darkTheme: Boolean): List<String> {
        return if (darkTheme) darkSwatches else lightSwatches
    }

    companion object {
        fun fromString(value: String?): AppColorScheme {
            return entries.firstOrNull { scheme ->
                scheme.name.equals(value, ignoreCase = true)
            } ?: CodeGeass
        }
    }
}
