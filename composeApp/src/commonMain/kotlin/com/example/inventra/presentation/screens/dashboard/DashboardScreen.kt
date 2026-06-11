package com.example.inventra.presentation.screens.dashboard

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Outbound
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.inventra.core.localization.AppStrings
import com.example.inventra.domain.model.BorrowRecord
import com.example.inventra.domain.model.BorrowStatus
import com.example.inventra.presentation.components.GlassCard
import com.example.inventra.presentation.components.InventRaBottomNav
import com.example.inventra.presentation.components.LoadingIndicator
import com.example.inventra.presentation.util.formatDateOnly
import inventra.composeapp.generated.resources.Res
import inventra.composeapp.generated.resources.logo_hmif
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onNavigateToAddItem: () -> Unit
) {
    val viewModel: DashboardViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val isAdmin = currentUser?.role == com.example.inventra.domain.model.UserRole.ADMIN
    val strings = AppStrings.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = org.jetbrains.compose.resources.painterResource(Res.drawable.logo_hmif),
                            contentDescription = null,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            strings.appName,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                )
            )
        },
        bottomBar = {
            InventRaBottomNav(currentRoute = currentRoute, onNavigate = onNavigate)
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        AnimatedContent(
            targetState = uiState,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "dashboard_content"
        ) { state ->
            when (state) {
                is DashboardUiState.Loading -> LoadingIndicator(Modifier.padding(paddingValues))
                is DashboardUiState.Error -> Box(
                    Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(strings.failedToLoadData, color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(8.dp))
                        Text(state.message, style = MaterialTheme.typography.bodySmall)
                    }
                }

                is DashboardUiState.Success -> DashboardContent(
                    state = state,
                    paddingValues = paddingValues,
                    isAdmin = isAdmin,
                    onNavigateToAddItem = onNavigateToAddItem
                )
            }
        }
    }
}

@Composable
private fun DashboardContent(
    state: DashboardUiState.Success,
    paddingValues: PaddingValues,
    isAdmin: Boolean,
    onNavigateToAddItem: () -> Unit
) {
    val strings = AppStrings.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        Text(
            strings.dashboardOverview,
            fontSize = 20.sp, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            strings.dashboardSubtitle,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        if (state.overdueItems > 0) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            "${state.overdueItems} ${strings.overdueAlert}",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(strings.takeAction, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard(
                Modifier.weight(1f), Icons.Default.Inventory,
                MaterialTheme.colorScheme.primary, strings.totalItems, state.totalItems.toString()
            )
            StatCard(
                Modifier.weight(1f), Icons.AutoMirrored.Filled.Outbound,
                MaterialTheme.colorScheme.secondary, strings.borrowed, state.borrowedItems.toString()
            )
        }
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard(
                Modifier.weight(1f), Icons.Default.CheckCircle,
                Color(0xFF4CAF50), strings.available,
                (state.totalItems - state.borrowedItems).toString()
            )
            StatCard(
                Modifier.weight(1f), Icons.Default.Warning,
                MaterialTheme.colorScheme.error, strings.overdue, state.overdueItems.toString()
            )
        }
        if (isAdmin) {
            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.AddCircle, null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        strings.registerNewItem, fontSize = 20.sp, fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        strings.registerNewItemDesc,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = onNavigateToAddItem,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) { Text(strings.getStarted, fontWeight = FontWeight.Bold) }
                }
            }
        }

        if (state.activeBorrowings.isNotEmpty()) {
            Spacer(Modifier.height(24.dp))
            Text(
                strings.activeBorrowing, style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            state.activeBorrowings.forEach { record ->
                ActiveBorrowingCard(record)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier, icon: ImageVector, iconTint: Color, label: String, value: String
) {
    GlassCard(modifier = modifier.height(130.dp)) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(28.dp))
            Column {
                Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.outline,
                    fontWeight = FontWeight.Bold)
                Text(value, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = iconTint)
            }
        }
    }
}

@Composable
private fun ActiveBorrowingCard(record: BorrowRecord) {
    val isOverdue = record.status == BorrowStatus.OVERDUE
    val strings = AppStrings.current
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isOverdue)
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(record.itemName, style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold)
                Text("${strings.borrower}: ${record.borrowerName} (${record.borrowerDivision})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline)
                Text("${strings.dueDate}: ${record.dueDate.formatDateOnly()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isOverdue) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.outline)
            }
            Surface(
                color = if (isOverdue) MaterialTheme.colorScheme.errorContainer
                else MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    if (isOverdue) strings.overdue else strings.active,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isOverdue) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}