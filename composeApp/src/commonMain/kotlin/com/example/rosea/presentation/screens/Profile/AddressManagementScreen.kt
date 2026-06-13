package com.example.rosea.presentation.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rosea.domain.model.Address
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressManagementScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddressViewModel = koinViewModel()
) {
    val addresses by viewModel.addresses.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }
    var selectedAddress by remember { mutableStateOf<Address?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Address Management", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    selectedAddress = null
                    showDialog = true
                },
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text("Add Address") },
                shape = RoundedCornerShape(16.dp)
            )
        }
    ) { padding ->
        if (addresses.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Outlined.LocationOn, null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                    Spacer(Modifier.height(16.dp))
                    Text("No addresses found", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(addresses, key = { it.id }) { address ->
                    AddressCard(
                        address = address,
                        onEdit = {
                            selectedAddress = address
                            showDialog = true
                        },
                        onDelete = { viewModel.deleteAddress(address.id) },
                        onSetDefault = { viewModel.makeDefault(address.id) }
                    )
                }
            }
        }
    }

    if (showDialog) {
        AddressDialog(
            address = selectedAddress,
            onDismiss = { showDialog = false },
            onSave = { label, receiver, phone, fullAddress, isDefault ->
                viewModel.saveAddress(
                    id = selectedAddress?.id ?: 0L,
                    label = label,
                    receiverName = receiver,
                    phoneNumber = phone,
                    fullAddress = fullAddress,
                    isDefault = isDefault
                )
                showDialog = false
            }
        )
    }
}

@Composable
fun AddressCard(
    address: Address,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSetDefault: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        border = if (address.isDefault) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(address.label, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                if (address.isDefault) {
                    Spacer(Modifier.width(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            "Default",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(address.receiverName, fontWeight = FontWeight.SemiBold)
            Text(address.phoneNumber, fontSize = 13.sp, color = Color.Gray)
            Text(address.fullAddress, fontSize = 14.sp, color = Color.Gray)
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                if (!address.isDefault) {
                    TextButton(onClick = onSetDefault) { Text("Set Default") }
                }
                TextButton(onClick = onEdit) { Text("Edit") }
                TextButton(onClick = onDelete) { Text("Delete", color = Color.Red) }
            }
        }
    }
}

@Composable
fun AddressDialog(
    address: Address?,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, Boolean) -> Unit
) {
    var label by remember { mutableStateOf(address?.label ?: "") }
    var receiver by remember { mutableStateOf(address?.receiverName ?: "") }
    var phone by remember { mutableStateOf(address?.phoneNumber ?: "") }
    var fullAddress by remember { mutableStateOf(address?.fullAddress ?: "") }
    var isDefault by remember { mutableStateOf(address?.isDefault ?: false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (address == null) "Add Address" else "Edit Address") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = label, onValueChange = { label = it }, label = { Text("Label (e.g. Home, Office)") })
                OutlinedTextField(value = receiver, onValueChange = { receiver = it }, label = { Text("Receiver Name") })
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") })
                OutlinedTextField(
                    value = fullAddress,
                    onValueChange = { fullAddress = it },
                    label = { Text("Full Address") },
                    modifier = Modifier.height(100.dp)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isDefault, onCheckedChange = { isDefault = it })
                    Text("Set as Default Address")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(label, receiver, phone, fullAddress, isDefault) },
                enabled = label.isNotBlank() && receiver.isNotBlank() && phone.isNotBlank() && fullAddress.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
