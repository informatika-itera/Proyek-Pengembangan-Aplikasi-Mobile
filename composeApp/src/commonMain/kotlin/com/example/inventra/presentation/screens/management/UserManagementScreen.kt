package com.example.inventra.presentation.screens.management

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventra.domain.model.User
import com.example.inventra.domain.model.UserDivision
import com.example.inventra.domain.model.UserRole
import com.example.inventra.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// ==================== UI STATE ====================

data class UserManagementUiState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val isRegistering: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val showRegisterDialog: Boolean = false,
    // Form fields
    val newName: String = "",
    val newEmail: String = "",
    val newStudentId: String = "",
    val newPassword: String = "",
    val newDivision: UserDivision = UserDivision.PUBDOK,
    val newRole: UserRole = UserRole.MEMBER
)

// ==================== VIEWMODEL ====================

class UserManagementViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserManagementUiState())
    val uiState: StateFlow<UserManagementUiState> = _uiState.asStateFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            authRepository.getAllUsers()
                .onSuccess { users ->
                    _uiState.update { it.copy(isLoading = false, users = users) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun showRegisterDialog() = _uiState.update { it.copy(showRegisterDialog = true, error = null) }
    fun hideRegisterDialog() = _uiState.update {
        it.copy(showRegisterDialog = false, newName = "", newEmail = "", newPassword = "")
    }

    fun onNewNameChange(v: String) = _uiState.update { it.copy(newName = v) }
    fun onNewEmailChange(v: String) = _uiState.update { it.copy(newEmail = v) }
    fun onNewStudentIdChange(v: String) = _uiState.update { it.copy(newStudentId = v) }
    fun onNewPasswordChange(v: String) = _uiState.update { it.copy(newPassword = v) }
    
    fun onNewDivisionChange(v: UserDivision) {
        _uiState.update { 
            it.copy(
                newDivision = v,
                newRole = if (v == UserDivision.BENDAHARA_UMUM) UserRole.ADMIN else UserRole.MEMBER
            )
        }
    }

    fun onNewRoleChange(v: UserRole) = _uiState.update { it.copy(newRole = v) }

    fun registerUser() {
        val state = _uiState.value
        if (state.newName.isBlank() || state.newEmail.isBlank() || state.newPassword.isBlank()) {
            _uiState.update { it.copy(error = "Semua field wajib diisi") }
            return
        }
        if (state.newPassword.length < 6) {
            _uiState.update { it.copy(error = "Password minimal 6 karakter") }
            return
        }

        _uiState.update { it.copy(isRegistering = true, error = null) }
        viewModelScope.launch {
            authRepository.register(
                email = state.newEmail.trim(),
                password = state.newPassword,
                name = state.newName.trim(),
                division = state.newDivision.name,
                role = state.newRole.name,
                studentId = state.newStudentId.trim().ifBlank { null }
            ).onSuccess {
                _uiState.update {
                    it.copy(
                        isRegistering = false,
                        showRegisterDialog = false,
                        successMessage = "Akun berhasil dibuat untuk ${state.newName}",
                        newName = "", newEmail = "", newPassword = ""
                    )
                }
                loadUsers()
            }.onFailure { e ->
                _uiState.update { it.copy(isRegistering = false, error = e.message) }
            }
        }
    }

    fun deleteUser(userId: String, userName: String) {
        viewModelScope.launch {
            authRepository.deleteUser(userId)
                .onSuccess {
                    _uiState.update { it.copy(successMessage = "Akun $userName berhasil dihapus") }
                    loadUsers()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
        }
    }

    fun toggleRole(user: User) {
        val newRole = if (user.role == UserRole.ADMIN) UserRole.MEMBER.name else UserRole.ADMIN.name
        viewModelScope.launch {
            authRepository.updateUserRole(user.id, newRole)
                .onSuccess { loadUsers() }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun clearMessages() = _uiState.update { it.copy(error = null, successMessage = null) }

    fun editUser(userId: String, newName: String, newEmail: String, newStudentId: String) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                authRepository.updateUserName(userId, newName).getOrThrow()
                authRepository.updateUserEmail(userId, newEmail).getOrThrow()
                authRepository.updateUserStudentId(userId, newStudentId.ifBlank { null }).getOrThrow()
                _uiState.update { it.copy(successMessage = "Data akun berhasil diperbarui", isLoading = false) }
                loadUsers()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun editPassword(userId: String, newPass: String) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            authRepository.updateUserPassword(userId, newPass)
                .onSuccess {
                    _uiState.update { it.copy(successMessage = "Password berhasil diubah", isLoading = false) }
                    loadUsers()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
        }
    }
}

// ==================== SCREEN ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    viewModel: UserManagementViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Manajemen Akun",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = viewModel::showRegisterDialog,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Tambah Akun")
            }
        },
        containerColor = Color.Transparent
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Info Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Sebagai Admin, Anda dapat membuat, menghapus, mengubah data, dan mengubah role akun anggota divisi.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Error banner
            AnimatedVisibility(visible = uiState.error != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        uiState.error ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            "${uiState.users.size} Akun Terdaftar",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    items(uiState.users, key = { it.id }) { user ->
                        UserCard(
                            user = user,
                            onDelete = { viewModel.deleteUser(user.id, user.name) },
                            onToggleRole = { viewModel.toggleRole(user) },
                            onEdit = { name, email, studentId -> viewModel.editUser(user.id, name, email, studentId) },
                            onEditPassword = { pass -> viewModel.editPassword(user.id, pass) },
                            onClick = { onNavigateToDetail(user.id) },
                            uriHandler = uriHandler
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }

    // ==================== REGISTER DIALOG ====================
    if (uiState.showRegisterDialog) {
        RegisterUserDialog(
            uiState = uiState,
            onDismiss = viewModel::hideRegisterDialog,
            onRegister = viewModel::registerUser,
            onNameChange = viewModel::onNewNameChange,
            onEmailChange = viewModel::onNewEmailChange,
            onStudentIdChange = viewModel::onNewStudentIdChange,
            onPasswordChange = viewModel::onNewPasswordChange,
            onDivisionChange = viewModel::onNewDivisionChange,
            onRoleChange = viewModel::onNewRoleChange
        )
    }
}

// ==================== USER CARD ====================

@Composable
private fun UserCard(
    user: User,
    onDelete: () -> Unit,
    onToggleRole: () -> Unit,
    onEdit: (name: String, email: String, studentId: String) -> Unit,
    onEditPassword: (String) -> Unit,
    onClick: () -> Unit,
    uriHandler: androidx.compose.ui.platform.UriHandler
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    
    var editNameValue by remember { mutableStateOf(user.name) }
    var editEmailValue by remember { mutableStateOf(user.email) }
    var editStudentIdValue by remember { mutableStateOf(user.studentId ?: "") }
    var newPasswordValue by remember { mutableStateOf("") }
    
    val isAdmin = user.role == UserRole.ADMIN

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isAdmin)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = if (isAdmin) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            user.name.take(1).uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isAdmin) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(user.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        if (isAdmin) {
                            Spacer(Modifier.width(6.dp))
                            Surface(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(4.dp)) {
                                Text("ADMIN", style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                            }
                        }
                    }
                    Text(user.division.displayName, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline)
                    
                    if (!user.studentId.isNullOrBlank()) {
                        Text(
                            text = user.studentId,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    if (!user.phone.isNullOrBlank()) {
                        Text(
                            text = user.phone,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline,
                            modifier = Modifier.clickable {
                                val phone = user.phone.replace(Regex("[^0-9]"), "")
                                val formattedPhone = if (phone.startsWith("0")) "62" + phone.substring(1) else phone
                                uriHandler.openUri("https://wa.me/$formattedPhone")
                            }
                        )
                    }
                }
            }
            
            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            Spacer(Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { 
                    editNameValue = user.name
                    editEmailValue = user.email
                    editStudentIdValue = user.studentId ?: ""
                    showEditDialog = true 
                }, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Edit, "Edit", tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = { 
                    newPasswordValue = ""
                    showPasswordDialog = true 
                }, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.LockReset, "Password", tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onToggleRole, modifier = Modifier.size(36.dp)) {
                    Icon(if (isAdmin) Icons.Default.AdminPanelSettings else Icons.Default.Person,
                        "Role", tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = { showDeleteDialog = true }, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.DeleteOutline, "Hapus", tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp))
                }
            }
        }
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Data Akun", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(value = editNameValue, onValueChange = { editNameValue = it },
                        label = { Text("Nama Lengkap") }, singleLine = true,
                        shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth())
                    
                    OutlinedTextField(value = editEmailValue, onValueChange = { editEmailValue = it },
                        label = { Text("Email") }, singleLine = true,
                        shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth())

                    OutlinedTextField(value = editStudentIdValue, onValueChange = { editStudentIdValue = it },
                        label = { Text("NIM / Student ID") }, singleLine = true,
                        shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(onClick = { 
                    if (editNameValue.isNotBlank() && editEmailValue.isNotBlank()) { 
                        onEdit(editNameValue.trim(), editEmailValue.trim(), editStudentIdValue.trim())
                        showEditDialog = false 
                    } 
                }) { Text("Simpan") }
            },
            dismissButton = { TextButton(onClick = { showEditDialog = false }) { Text("Batal") } }
        )
    }

    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showPasswordDialog = false },
            title = { Text("Ubah Password", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newPasswordValue, onValueChange = { newPasswordValue = it },
                    label = { Text("Password Baru (min 6 karakter)") }, 
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(8.dp), 
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = { 
                    if (newPasswordValue.length >= 6) { 
                        onEditPassword(newPasswordValue)
                        showPasswordDialog = false 
                    } 
                }) { Text("Ubah") }
            },
            dismissButton = { TextButton(onClick = { showPasswordDialog = false }) { Text("Batal") } }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Akun") },
            text = { Text("Yakin ingin menghapus akun ${user.name}?") },
            confirmButton = {
                TextButton(onClick = { showDeleteDialog = false; onDelete() },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) { Text("Hapus") }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") } }
        )
    }
}

// ==================== REGISTER DIALOG ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegisterUserDialog(
    uiState: UserManagementUiState,
    onDismiss: () -> Unit,
    onRegister: () -> Unit,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onStudentIdChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onDivisionChange: (UserDivision) -> Unit,
    onRoleChange: (UserRole) -> Unit
) {
    var showDivisionMenu by remember { mutableStateOf(false) }
    var showRoleMenu by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.PersonAdd,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Buat Akun Baru", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.error != null) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            uiState.error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = uiState.newName,
                    onValueChange = onNameChange,
                    label = { Text("Nama Lengkap *") },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = uiState.newEmail,
                    onValueChange = onEmailChange,
                    label = { Text("Email *") },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = uiState.newStudentId,
                    onValueChange = onStudentIdChange,
                    label = { Text("NIM / Student ID") },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = uiState.newPassword,
                    onValueChange = onPasswordChange,
                    label = { Text("Password * (min 6 karakter)") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = showDivisionMenu,
                    onExpandedChange = { showDivisionMenu = it }
                ) {
                    OutlinedTextField(
                        value = uiState.newDivision.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Divisi") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = showDivisionMenu)
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                    )
                    ExposedDropdownMenu(
                        expanded = showDivisionMenu,
                        onDismissRequest = { showDivisionMenu = false }
                    ) {
                        UserDivision.entries.forEach { div ->
                            DropdownMenuItem(
                                text = { Text(div.displayName) },
                                onClick = {
                                    onDivisionChange(div)
                                    showDivisionMenu = false
                                }
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = showRoleMenu,
                    onExpandedChange = { showRoleMenu = it }
                ) {
                    OutlinedTextField(
                        value = uiState.newRole.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Role") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = showRoleMenu)
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                    )
                    ExposedDropdownMenu(
                        expanded = showRoleMenu,
                        onDismissRequest = { showRoleMenu = false }
                    ) {
                        UserRole.entries.forEach { role ->
                            DropdownMenuItem(
                                text = { Text(role.displayName) },
                                onClick = {
                                    onRoleChange(role)
                                    showRoleMenu = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onRegister,
                enabled = !uiState.isRegistering
            ) {
                if (uiState.isRegistering) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Buat Akun")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}