package com.example.fintrack.core.util

import kotlin.math.roundToLong

object CurrencyFormatter {
    
    fun formatCurrency(amount: Double, currencyCode: String): String {
        val isNegative = amount < 0
        val absAmount = if (isNegative) -amount else amount
        
        val formattedAmount = if (currencyCode == "USD") {
            val rounded = (absAmount * 100).roundToLong() / 100.0
            val str = rounded.toString()
            val parts = str.split(".")
            val whole = parts[0]
            val decimal = if (parts.size > 1) parts[1].padEnd(2, '0').take(2) else "00"
            val formattedWhole = whole.reversed().chunked(3).joinToString(",").reversed()
            "$$formattedWhole.$decimal"
        } else if (currencyCode == "IDR") {
            val rounded = absAmount.roundToLong()
            val formattedWhole = rounded.toString().reversed().chunked(3).joinToString(".").reversed()
            "Rp $formattedWhole"
        } else {
            // Generic formatting for other currencies if needed later
            val rounded = (absAmount * 100).roundToLong() / 100.0
            val str = rounded.toString()
            val parts = str.split(".")
            val whole = parts[0]
            val decimal = if (parts.size > 1) parts[1].padEnd(2, '0').take(2) else "00"
            val formattedWhole = whole.reversed().chunked(3).joinToString(",").reversed()
            "$currencyCode $formattedWhole.$decimal"
        }
        
        return if (isNegative) "-$formattedAmount" else formattedAmount
    }
}
