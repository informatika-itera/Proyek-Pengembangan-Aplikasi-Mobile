package com.mywallet.utils

/**
 * Formats a number with dot as thousands separator for IDR.
 * Example: 4500000 -> "4.500.000"
 */
fun formatCurrency(amount: Double): String {
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
