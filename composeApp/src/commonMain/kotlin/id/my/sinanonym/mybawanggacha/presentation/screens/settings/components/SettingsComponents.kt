package id.my.sinanonym.mybawanggacha.presentation.screens.settings.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import id.my.sinanonym.mybawanggacha.core.build.AppBuildInfo
import id.my.sinanonym.mybawanggacha.core.build.AppBuildInfo.Companion.isKnownBuildValue
import id.my.sinanonym.mybawanggacha.core.build.AppBuildInfoProvider
import id.my.sinanonym.mybawanggacha.domain.settings.model.AiApiModel
import id.my.sinanonym.mybawanggacha.domain.settings.model.AiApiSettings
import id.my.sinanonym.mybawanggacha.domain.settings.model.AiPersonality
import id.my.sinanonym.mybawanggacha.domain.settings.model.AppColorScheme
import id.my.sinanonym.mybawanggacha.domain.settings.model.NetworkMode
import id.my.sinanonym.mybawanggacha.domain.settings.model.ThemeMode
import id.my.sinanonym.mybawanggacha.generated.resources.Res
import id.my.sinanonym.mybawanggacha.generated.resources.mbg_launcher_foreground
import id.my.sinanonym.mybawanggacha.presentation.components.MBGMainRailKey
import id.my.sinanonym.mybawanggacha.presentation.components.MBGRailBackButton
import id.my.sinanonym.mybawanggacha.presentation.components.MBGSideRailScaffold
import id.my.sinanonym.mybawanggacha.presentation.screens.settings.*
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun SettingsMainMenu(
    themeMode: ThemeMode,
    isDarkMode: Boolean,
    networkMode: NetworkMode,
    appColorScheme: AppColorScheme,
    aiApiSettings: AiApiSettings,
    requestUsage: SettingsRequestUsageUiState,
    onPaneSelected: (SettingsPane) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        SettingsMenuRow(
            icon = Icons.Default.Verified,
            title = "Appearance",
            description = buildString {
                append(
                    when (themeMode) {
                        ThemeMode.System -> if (isDarkMode) "System theme: dark" else "System theme: light"
                        ThemeMode.Light -> "Light theme"
                        ThemeMode.Dark -> "Dark theme"
                    }
                )
                append(" • ")
                append(appColorScheme.label)
            },
            onClick = { onPaneSelected(SettingsPane.Appearance) }
        )

        SettingsMenuRow(
            icon = Icons.Default.Cloud,
            title = "Data & Offline",
            description = networkMode.description,
            onClick = { onPaneSelected(SettingsPane.DataAccess) }
        )

        SettingsMenuRow(
            icon = Icons.Default.VpnKey,
            title = "AI API",
            description = "${aiApiSettings.model.label} • ${aiApiSettings.personality.label} • ${if (aiApiSettings.hasToken) "Token tersimpan" else "Token belum diisi"}",
            onClick = { onPaneSelected(SettingsPane.Api) }
        )

        SettingsMenuRow(
            icon = Icons.Default.Storage,
            title = "Request Usage",
            description = "${requestUsage.usedLastMinute}/${requestUsage.minuteLimit} request menit ini • ${requestUsage.remainingThisMinute} tersisa",
            onClick = { onPaneSelected(SettingsPane.RequestUsage) }
        )

        SettingsMenuRow(
            icon = Icons.Default.Info,
            title = "About",
            description = "Versi, app info, dan sumber data.",
            onClick = { onPaneSelected(SettingsPane.About) }
        )
    }
}

@Composable
internal fun SettingsPaneHeader(
    title: String,
    description: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SettingsMenuRow(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


@Composable
internal fun SettingsThemeSection(
    themeMode: ThemeMode,
    isDarkMode: Boolean,
    onThemeModeSelected: (ThemeMode) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Theme",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = when (themeMode) {
                ThemeMode.System -> if (isDarkMode) "Mengikuti sistem: gelap" else "Mengikuti sistem: terang"
                ThemeMode.Light -> "Tema terang aktif"
                ThemeMode.Dark -> "Tema gelap aktif"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SettingsChoiceChip(
                label = "System",
                selected = themeMode == ThemeMode.System,
                onClick = { onThemeModeSelected(ThemeMode.System) }
            )
            SettingsChoiceChip(
                label = "Light",
                selected = themeMode == ThemeMode.Light,
                onClick = { onThemeModeSelected(ThemeMode.Light) }
            )
            SettingsChoiceChip(
                label = "Dark",
                selected = themeMode == ThemeMode.Dark,
                onClick = { onThemeModeSelected(ThemeMode.Dark) }
            )
        }
    }
}


@Composable
internal fun SettingsColorSchemeSection(
    selected: AppColorScheme,
    isDarkMode: Boolean,
    onSelected: (AppColorScheme) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Colorscheme",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(
                items = AppColorScheme.entries.toList(),
                key = { scheme -> scheme.name }
            ) { scheme ->
                SettingsColorSchemeRow(
                    scheme = scheme,
                    selected = scheme == selected,
                    isDarkMode = isDarkMode,
                    onClick = { onSelected(scheme) }
                )
            }
        }
    }
}

@Composable
private fun SettingsColorSchemeRow(
    scheme: AppColorScheme,
    selected: Boolean,
    isDarkMode: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(230.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.60f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            },
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = scheme.label,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                scheme.swatches(darkTheme = isDarkMode).forEach { hex ->
                    ColorSwatch(hex = hex)
                }
            }
        }
    }
}

@Composable
private fun ColorSwatch(hex: String) {
    Surface(
        modifier = Modifier.size(18.dp),
        shape = CircleShape,
        color = hex.toColor()
    ) {}
}

private fun String.toColor(): Color {
    val rgb = removePrefix("#").toLongOrNull(radix = 16) ?: 0L
    return Color(0xFF000000L or rgb)
}


@Composable
internal fun SettingsApiSection(
    settings: AiApiSettings,
    onModelSelected: (AiApiModel) -> Unit,
    onPersonalitySelected: (AiPersonality) -> Unit,
    onTokenChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Model",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold
        )

        AiModelDropdown(
            selected = settings.model,
            onSelected = onModelSelected
        )

        SettingsDivider()

        Text(
            text = "Personality",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold
        )

        AiPersonalityDropdown(
            selected = settings.personality,
            onSelected = onPersonalitySelected
        )

        SettingsDivider()

        Text(
            text = "API token",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold
        )

        OutlinedTextField(
            value = settings.token,
            onValueChange = onTokenChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("Token") },
            placeholder = { Text("Gemini API key") },
            visualTransformation = PasswordVisualTransformation()
        )

        Text(
            text = if (settings.hasToken) {
                "Token tersimpan lokal. Kosongkan untuk fallback ke config platform."
            } else {
                "Kosong: memakai config platform jika tersedia."
            },
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AiModelDropdown(
    selected: AiApiModel,
    onSelected: (AiApiModel) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = "${selected.label}  ·  ${selected.modelId}",
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            singleLine = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null
                )
            }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clickable { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            AiApiModel.entries.forEach { model ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(
                                text = model.label,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = model.modelId,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    onClick = {
                        expanded = false
                        onSelected(model)
                    }
                )
            }
        }
    }
}

@Composable
private fun AiPersonalityDropdown(
    selected: AiPersonality,
    onSelected: (AiPersonality) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = "${selected.label}  ·  ${selected.description}",
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            singleLine = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null
                )
            }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clickable { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            AiPersonality.entries.forEach { personality ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(
                                text = personality.label,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = personality.description,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    onClick = {
                        expanded = false
                        onSelected(personality)
                    }
                )
            }
        }
    }
}


@Composable
internal fun SettingsNetworkSection(
    networkMode: NetworkMode,
    onNetworkModeSelected: (NetworkMode) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Network",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = networkMode.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SettingsChoiceChip(
                label = NetworkMode.Auto.label,
                selected = networkMode == NetworkMode.Auto,
                onClick = { onNetworkModeSelected(NetworkMode.Auto) }
            )
            SettingsChoiceChip(
                label = NetworkMode.OfflineOnly.label,
                selected = networkMode == NetworkMode.OfflineOnly,
                onClick = { onNetworkModeSelected(NetworkMode.OfflineOnly) }
            )
        }
    }
}

@Composable
internal fun SettingsRequestUsageSection(
    requestUsage: SettingsRequestUsageUiState
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Jikan Request Budget",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold
        )

        SettingsRequestUsageCard(requestUsage = requestUsage)
    }
}

@Composable
private fun SettingsRequestUsageCard(
    requestUsage: SettingsRequestUsageUiState
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Storage,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Request terpakai",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${requestUsage.usedLastMinute}/${requestUsage.minuteLimit}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                LinearProgressIndicator(
                    progress = { requestUsage.minuteProgress.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SettingsUsageMetric(
                        label = "Menit ini",
                        value = "${requestUsage.remainingThisMinute} tersisa",
                        modifier = Modifier.weight(1f)
                    )
                    SettingsUsageMetric(
                        label = "Detik ini",
                        value = "${requestUsage.usedLastSecond}/${requestUsage.secondLimit}",
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(
                    text = requestUsage.cooldownLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SettingsUsageMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
    }
}


@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 4.dp),
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    )
}

@Composable
private fun SettingsChoiceChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text = label) }
    )
}

@Composable
internal fun SettingsAboutSection(
    showTitle: Boolean = true
) {
    val buildInfo = AppBuildInfoProvider.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (showTitle) {
            Text(
                text = "About",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold
            )
        }

        SettingsAboutHero(buildInfo = buildInfo)

        SettingsBuildInfoCard(buildInfo = buildInfo)

        SettingsDetailCard(
            icon = Icons.Default.Cloud,
            title = "Data & AI",
            rows = listOf(
                "Data source" to buildInfo.dataSource,
                "AI provider" to buildInfo.aiProvider,
                "Library" to "Local SQLDelight database + DataStore preferences",
                "Offline mode" to "Network/cache policy dapat diatur di Data & Offline"
            )
        )

        SettingsDetailCard(
            icon = Icons.Default.Storage,
            title = "Runtime",
            rows = listOf(
                "Platform" to buildInfo.runtimePlatform,
                "Device" to buildInfo.device,
                "Application ID" to buildInfo.applicationId,
                "Database schema" to buildInfo.databaseSchema
            )
        )

        SettingsDevelopersCard()
    }
}

@Composable
private fun SettingsAboutHero(buildInfo: AppBuildInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.28f),
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 22.dp, horizontal = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SettingsAppLogoBadge()

            Text(
                text = buildInfo.appName,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Anime, manga, library, gacha, dan AI assistant dalam satu aplikasi.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SettingsAppLogoBadge() {
    Image(
        painter = painterResource(Res.drawable.mbg_launcher_foreground),
        contentDescription = "MyBawangGacha app logo",
        modifier = Modifier.size(200.dp),
        contentScale = ContentScale.Fit,
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
    )
}

@Composable
private fun SettingsDevelopersCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Developers",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

                SettingsDeveloperRow(
                    name = "Varasina Farmadani",
                    handle = "@sinavarasina",
                    githubUrl = "github.com/sinavarasina",
                    email = "sina@sinanonym.my.id",
                    avatarUrl = "https://github.com/sinavarasina.png?size=128"
                )

                SettingsDeveloperRow(
                    name = "Faiq",
                    handle = "@Faiq1818",
                    githubUrl = "github.com/Faiq1818",
                    email = "ghozyerlanggafaiq@gmail.com",
                    avatarUrl = "https://github.com/Faiq1818.png?size=128"
                )
            }
        }
    }
}

@Composable
private fun SettingsDeveloperRow(
    name: String,
    handle: String,
    githubUrl: String,
    email: String,
    avatarUrl: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = avatarUrl,
            contentDescription = "$name GitHub avatar",
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "$handle • $githubUrl",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = email,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun SettingsBuildInfoCard(buildInfo: AppBuildInfo) {
    SettingsDetailCard(
        icon = Icons.Default.Verified,
        title = "Build info",
        rows = buildInfoRows(buildInfo)
    )
}

private fun buildInfoRows(buildInfo: AppBuildInfo): List<Pair<String, String>> {
    return buildList {
        add("Version" to buildInfo.versionDisplay())
        add("Profile" to buildInfo.buildProfile)
        add("Target" to buildInfo.buildTarget)
        add("Repository" to buildInfo.repository)

        if (buildInfo.hasEmbeddedGitMetadata) {
            addKnown("Branch", buildInfo.branch)
            addKnown("Commit", buildInfo.commit)
            addKnown("State", buildInfo.commitState)
            addKnown("Build date", buildInfo.buildDate)
            addKnown("CI", buildInfo.ci)
            addKnown("Run ID", buildInfo.ciRunId)
        } else {
            add("Git metadata" to "not embedded in this composeApp library build")
        }

        add("Version ABI" to buildInfo.versionAbiString)
    }
}

private fun AppBuildInfo.versionDisplay(): String {
    return if (versionCode.isKnownBuildValue()) {
        "$versionName ($versionCode)"
    } else {
        versionName
    }
}

private fun MutableList<Pair<String, String>>.addKnown(
    label: String,
    value: String
) {
    if (value.isKnownBuildValue()) {
        add(label to value)
    }
}

@Composable
private fun SettingsDetailCard(
    icon: ImageVector,
    title: String,
    rows: List<Pair<String, String>>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

                rows.forEach { (label, value) ->
                    SettingsDetailRow(
                        label = label,
                        value = value
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsDetailRow(
    label: String,
    value: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = value.ifBlank { "unknown" },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontFamily = FontFamily.Monospace
        )
    }
}