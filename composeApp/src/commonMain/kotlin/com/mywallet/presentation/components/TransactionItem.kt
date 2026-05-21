package com.mywallet.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mywallet.domain.model.Transaction
import com.mywallet.domain.model.TransactionType

@Composable
fun TransactionItem(
    transaction: Transaction,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val isIncome = transaction.type == TransactionType.INCOME
            val icon = if (isIncome) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward
            
            // Define semantic colors
            val itemColor = if (isIncome) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            val iconBg = itemColor.copy(alpha = 0.12f)
            
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = itemColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    maxLines = 2
                )
                Text(
                    text = "${transaction.date} • ${transaction.time}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = (if (isIncome) "+" else "-") + " Rp. ${formatCurrency(transaction.amount)}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = itemColor
                ),
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}

private fun formatCurrency(amount: Double): String {
    val longAmount = amount.toLong()
    val str = longAmount.toString()
    val result = StringBuilder()
    var count = 0
    for (i in str.length - 1 downTo 0) {
        result.append(str[i])
        count++
        if (count == 3 && i != 0) {
            result.append('.')
            count = 0
        }
    }
    return result.reverse().toString()
}
