package com.example.inventra.presentation.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.inventra.core.localization.AppStrings
import com.example.inventra.core.localization.Language
import com.example.inventra.core.localization.LocalLanguage
import com.example.inventra.core.localization.Strings
import com.example.inventra.core.util.rememberImagePickerLauncher
import com.example.inventra.domain.model.User
import com.example.inventra.domain.model.UserDivision
import com.example.inventra.domain.model.UserRole
import com.example.inventra.domain.repository.AuthRepository
import com.example.inventra.presentation.components.InventRaBottomNav
import com.example.inventra.presentation.theme.LocalThemeIsDark
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

data class ProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val isEditMode: Boolean = false,
    val editName: String = "",
    val editPhone: String = "",
    val editStudentId: String = "",
    val editDivisionHead: String = "",
    val editStaffList: String = "",
    val divisionMembers: List<User> = emptyList(),
    val isLoadingMembers: Boolean = false
)

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val itemRepository: com.example.inventra.domain.repository.ItemRepository,
    private val borrowRepository: com.example.inventra.domain.repository.BorrowRepository,
    private val userPreferences: com.example.inventra.data.local.datastore.UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init { loadProfile() }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setDarkMode(enabled)
        }
    }

    fun setLanguage(language: Language) {
        viewModelScope.launch {
            userPreferences.setLanguage(language.code)
        }
    }

    fun resetAllData(strings: Strings) {
        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            try {
                itemRepository.deleteAll()
                borrowRepository.deleteAll()
                _uiState.update { it.copy(isSaving = false, successMessage = strings.resetDataSuccess) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, error = "${strings.resetDataError}: ${e.message}") }
            }
        }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onComplete()
        }
    }

    fun loadProfile() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            _uiState.update {
                it.copy(
                    isLoading = false, 
                    user = user,
                    editName = user?.name ?: "", 
                    editPhone = user?.phone ?: "",
                    editStudentId = user?.studentId ?: "",
                    editDivisionHead = user?.divisionHead ?: "",
                    editStaffList = user?.staffList ?: ""
                )
            }
            if (user != null) loadDivisionMembers(user)
        }
    }

    private fun loadDivisionMembers(currentUser: User) {
        _uiState.update { it.copy(isLoadingMembers = true) }
        viewModelScope.launch {
            authRepository.getAllUsers()
                .onSuccess { users ->
                    val members = users.filter { it.division == currentUser.division }
                    _uiState.update { it.copy(divisionMembers = members, isLoadingMembers = false) }
                }
                .onFailure { _uiState.update { it.copy(isLoadingMembers = false) } }
        }
    }

    fun enterEditMode() = _uiState.update { it.copy(isEditMode = true) }
    fun exitEditMode() = _uiState.update { it.copy(isEditMode = false) }
    fun onNameChange(v: String) = _uiState.update { it.copy(editName = v) }
    fun onPhoneChange(v: String) = _uiState.update { it.copy(editPhone = v) }
    fun onStudentIdChange(v: String) = _uiState.update { it.copy(editStudentId = v) }
    fun onDivisionHeadChange(v: String) = _uiState.update { it.copy(editDivisionHead = v) }
    fun onStaffListChange(v: String) = _uiState.update { it.copy(editStaffList = v) }

    fun uploadAvatar(bytes: ByteArray, fileName: String, strings: Strings) {
        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            authRepository.updateAvatar(bytes, fileName)
                .onSuccess { url ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            user = it.user?.copy(avatarUrl = url),
                            successMessage = strings.profilePhotoUpdated
                        )
                    }
                    loadProfile() // Re-fetch to ensure sync
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isSaving = false, error = e.message) }
                }
        }
    }

    fun saveProfile(strings: Strings) {
        val state = _uiState.value
        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            authRepository.updateProfile(
                name = state.editName,
                phone = state.editPhone.ifBlank { null },
                avatarUrl = null,
                divisionHead = state.editDivisionHead,
                staffList = state.editStaffList,
                studentId = state.editStudentId.ifBlank { null }
            ).onSuccess { user ->
                _uiState.update { it.copy(isSaving = false, isEditMode = false, user = user, successMessage = strings.profileUpdated) }
                loadProfile() // Re-fetch
                loadDivisionMembers(user)
            }.onFailure { e ->
                _uiState.update { it.copy(isSaving = false, error = e.message) }
            }
        }
    }

    fun clearMessages() = _uiState.update { it.copy(error = null, successMessage = null) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onLogoutClick: () -> Unit,
    onNavigateToUserManagement: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDarkTheme = LocalThemeIsDark.current
    val currentLanguage = LocalLanguage.current
    val strings = AppStrings.current
    val snackbarHostState = remember { SnackbarHostState() }
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current

    var showImageSourceOptions by remember { mutableStateOf(false) }
    val imagePicker = rememberImagePickerLauncher { bytes, fileName ->
        viewModel.uploadAvatar(bytes, fileName, strings)
    }

    if (showImageSourceOptions) {
        AlertDialog(
            onDismissRequest = { showImageSourceOptions = false },
            title = { Text(strings.choosePhotoSource) },
            text = { Text(strings.choosePhotoSourceDesc) },
            confirmButton = {
                TextButton(onClick = {
                    showImageSourceOptions = false
                    imagePicker.takePhoto()
                }) {
                    Text(strings.camera)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showImageSourceOptions = false
                    imagePicker.pickImage()
                }) {
                    Text(strings.gallery)
                }
            }
        )
    }

    LaunchedEffect(uiState.successMessage, uiState.error) {
        uiState.successMessage?.let { snackbarHostState.showSnackbar(it); viewModel.clearMessages() }
        uiState.error?.let { snackbarHostState.showSnackbar(it); viewModel.clearMessages() }
    }

    val user = uiState.user
    val isBendaharaUmum = user?.division == UserDivision.BENDAHARA_UMUM
    var showAvatarDialog by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(strings.myProfile, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                actions = {
                    if (!uiState.isEditMode) {
                        IconButton(onClick = viewModel::enterEditMode) {
                            Icon(Icons.Default.Edit, strings.editProfile)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                )
            )
        },
        bottomBar = { InventRaBottomNav(currentRoute = currentRoute, onNavigate = onNavigate) },
        containerColor = Color.Transparent
    ) { paddingValues ->

        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(paddingValues), Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        if (showAvatarDialog && user?.avatarUrl != null) {
            AlertDialog(
                onDismissRequest = { showAvatarDialog = false },
                text = {
                    AsyncImage(
                        model = user.avatarUrl,
                        contentDescription = "Foto Profil Besar",
                        modifier = Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showAvatarDialog = false }) { Text(strings.close) }
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Avatar ──────────────────────────────────────────────────────
            Box(modifier = Modifier.size(96.dp), contentAlignment = Alignment.BottomEnd) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(96.dp).clickable { if (user?.avatarUrl != null) showAvatarDialog = true }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (user?.avatarUrl != null) {
                            AsyncImage(
                                model = user.avatarUrl,
                                contentDescription = "Foto Profil",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Text(
                                (user?.name ?: "?").take(1).uppercase(),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp).clickable { showImageSourceOptions = true }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                        } else {
                            Icon(Icons.Default.CameraAlt, strings.changePhoto, tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))

            // ── Nama & Role ────────────────────────────────────────────────
            Text(user?.name ?: "Pengguna", style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold)
            
            if (!user?.studentId.isNullOrBlank()) {
                Text(
                    text = user?.studentId ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            
            if (!user?.phone.isNullOrBlank()) {
                Text(
                    text = user?.phone ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline,
                    modifier = Modifier
                        .padding(vertical = 2.dp)
                        .clickable {
                            val phone = user?.phone?.replace(Regex("[^0-9]"), "") ?: ""
                            val formattedPhone = if (phone.startsWith("0")) "62" + phone.substring(1) else phone
                            if (formattedPhone.isNotBlank()) {
                                uriHandler.openUri("https://wa.me/$formattedPhone")
                            }
                        }
                )
            }

            if (isBendaharaUmum) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(4.dp)) {
                        Text("BENDAHARA UMUM", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }
            Spacer(Modifier.height(24.dp))

            // ── Edit Mode ──────────────────────────────────────────────────
            if (uiState.isEditMode) {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(strings.editProfile, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        
                        OutlinedTextField(value = uiState.editName, onValueChange = viewModel::onNameChange,
                            label = { Text(strings.fullName) }, singleLine = true,
                            shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth())

                        OutlinedTextField(value = uiState.editStudentId, onValueChange = viewModel::onStudentIdChange,
                            label = { Text("NIM / Student ID") }, singleLine = true,
                            shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth())
                        
                        OutlinedTextField(value = uiState.editPhone, onValueChange = viewModel::onPhoneChange,
                            label = { Text(strings.phoneNumber) }, singleLine = true,
                            shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth())

                        OutlinedTextField(value = uiState.editDivisionHead, onValueChange = viewModel::onDivisionHeadChange,
                            label = { Text(if (isBendaharaUmum) "Bendahara Umum" else strings.adminHead) }, singleLine = true,
                            shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth())

                        OutlinedTextField(value = uiState.editStaffList, onValueChange = viewModel::onStaffListChange,
                            label = { Text(if (isBendaharaUmum) "Daftar Staff (Pisahkan dengan koma)" else strings.members + " (Pisahkan dengan koma)") },
                            placeholder = { Text("Nama 1, Nama 2, ...") },
                            shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth(),
                            minLines = 2)

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = viewModel::exitEditMode, modifier = Modifier.weight(1f)) { Text(strings.cancel) }
                            Button(onClick = { viewModel.saveProfile(strings) }, modifier = Modifier.weight(1f),
                                enabled = !uiState.isSaving) {
                                if (uiState.isSaving) CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                                else Text(strings.save)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // ── Division Info ──────────────────────────────────────────────
            DivisionInfoCard(
                user = user,
                strings = strings
            )
            Spacer(Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(Modifier.padding(16.dp)) {
                    Text(strings.settings, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(if (isDarkTheme.value) Icons.Default.DarkMode else Icons.Default.LightMode,
                                null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(strings.darkMode, style = MaterialTheme.typography.titleSmall)
                                Text(if (isDarkTheme.value) strings.darkThemeActive else strings.darkThemeInactive,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.outline)
                            }
                        }
                        Switch(checked = isDarkTheme.value, onCheckedChange = { 
                            isDarkTheme.value = it
                            viewModel.setDarkMode(it)
                        })
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // Language Selection
                    var showLanguageDialog by remember { mutableStateOf(false) }
                    if (showLanguageDialog) {
                        AlertDialog(
                            onDismissRequest = { showLanguageDialog = false },
                            title = { Text(strings.changeLanguage) },
                            text = {
                                Column {
                                    Language.entries.forEach { lang ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    viewModel.setLanguage(lang)
                                                    showLanguageDialog = false
                                                }
                                                .padding(vertical = 12.dp, horizontal = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            RadioButton(
                                                selected = currentLanguage.value == lang,
                                                onClick = null
                                            )
                                            Spacer(Modifier.width(12.dp))
                                            Text(lang.displayName)
                                        }
                                    }
                                }
                            },
                            confirmButton = {
                                TextButton(onClick = { showLanguageDialog = false }) { Text(strings.close) }
                            }
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth().clickable { showLanguageDialog = true }.padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Language, null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(strings.language, style = MaterialTheme.typography.titleSmall)
                                Text(currentLanguage.value.displayName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.outline)
                            }
                        }
                        Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.outline)
                    }
                }
            }

            if (user?.role == UserRole.ADMIN) {
                var showResetDialog by remember { mutableStateOf(false) }

                if (showResetDialog) {
                    AlertDialog(
                        onDismissRequest = { showResetDialog = false },
                        title = { Text(strings.confirmResetTitle) },
                        text = { Text(strings.confirmResetMessage) },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    showResetDialog = false
                                    viewModel.resetAllData(strings)
                                },
                                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                            ) { Text(strings.resetAllData, fontWeight = FontWeight.Bold) }
                        },
                        dismissButton = {
                            TextButton(onClick = { showResetDialog = false }) { Text(strings.cancel) }
                        }
                    )
                }

                Spacer(Modifier.height(16.dp))
                Card(modifier = Modifier.fillMaxWidth().clickable { onNavigateToUserManagement() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ManageAccounts, null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.size(28.dp))
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(strings.accountManagement, style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                            Text(strings.createAndManageAccountDesc,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f))
                        }
                        Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                    }
                }

                Spacer(Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { showResetDialog = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DeleteForever, null,
                            tint = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.size(28.dp))
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(strings.resetAllData, style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer)
                            Text(strings.resetDataDesc,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f))
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            OutlinedButton(
                onClick = {
                    viewModel.logout { onLogoutClick() }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                Icon(Icons.AutoMirrored.Filled.Logout, null)
                Spacer(Modifier.width(8.dp))
                Text(strings.logout, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(24.dp))
            Spacer(Modifier.height(8.dp))
            Text(
                text = "InventRa v1.0.0",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun DivisionInfoCard(user: User?, strings: Strings) {
    if (user == null) return
    val isBendaharaUmum = user.division == UserDivision.BENDAHARA_UMUM
    val staffList = user.staffList?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() } ?: emptyList()
    
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(if (isBendaharaUmum) Icons.Default.AccountBalance else Icons.Default.Groups, 
                    null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text(
                    if (isBendaharaUmum) user.division.displayName else "Divisi ${user.division.displayName}", 
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(12.dp))
            
            val headLabel = if (isBendaharaUmum) "Bendahara Umum" else strings.adminHead
            
            Text(headLabel, style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp))
            
            if (user.divisionHead.isNullOrBlank()) {
                Text("- Belum diisi -", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
            } else {
                Surface(color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(if (isBendaharaUmum) Icons.Default.Person else Icons.Default.Star, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(18.dp))
                            }
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(user.divisionHead, style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            Text(
                if (isBendaharaUmum) "Daftar Staff" else strings.members, 
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            if (staffList.isEmpty()) {
                Text(strings.noMembers, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline)
            } else {
                staffList.forEachIndexed { index, name ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.size(28.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("${index + 1}", style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer)
                            }
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(name, style = MaterialTheme.typography.bodyMedium)
                    }
                    if (index < staffList.size - 1) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                            modifier = Modifier.padding(start = 38.dp))
                    }
                }
            }
        }
    }
}
