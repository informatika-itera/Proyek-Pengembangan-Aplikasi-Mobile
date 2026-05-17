package com.example.masakuy.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.masakuy.core.util.formatCurrency
import com.example.masakuy.theme.OrangeMain
import com.example.masakuy.theme.LightGray

data class BudgetOption(val label: String, val value: Int)

@Composable
fun BudgetSelector(
    options: List<BudgetOption>,
    selectedBudget: Int?,
    onBudgetSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            BudgetButton(
                label = option.label,
                isSelected = selectedBudget == option.value,
                onClick = { onBudgetSelected(option.value) }
            )
        }
    }
}

@Composable
fun BudgetButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = label,
        modifier = Modifier
            .background(
                color = if (isSelected) OrangeMain else LightGray,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(12.dp, 8.dp),
        fontSize = 14.sp
    )
}