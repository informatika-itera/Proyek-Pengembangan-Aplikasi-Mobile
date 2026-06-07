package com.kelazzz.app.presentation.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
actual fun ScheduleDateField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    modifier: Modifier
) {
    val context = LocalContext.current
    val selectedDate = value.toCalendarDateOrToday()

    Box(
        modifier = modifier.clickable {
            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    val date = Calendar.getInstance().apply {
                        set(Calendar.YEAR, year)
                        set(Calendar.MONTH, month)
                        set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    }
                    onValueChange(DATE_FORMAT.format(date.time))
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            enabled = false,
            label = { Text("Tanggal") },
            placeholder = { Text("Pilih tanggal") },
            singleLine = true,
            isError = isError,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = null
                )
            },
            shape = RoundedCornerShape(16.dp),
            colors = disabledPickerColors(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
actual fun ScheduleTimeField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier
) {
    val context = LocalContext.current
    val selectedTime = value.substringBefore("-").trim().toCalendarTimeOrNow()

    Box(
        modifier = modifier.clickable {
            TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    onValueChange("${hourOfDay.twoDigits()}:${minute.twoDigits()}")
                },
                selectedTime.get(Calendar.HOUR_OF_DAY),
                selectedTime.get(Calendar.MINUTE),
                true
            ).show()
        }
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            enabled = false,
            label = { Text("Waktu mulai") },
            placeholder = { Text("Pilih waktu") },
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null
                )
            },
            shape = RoundedCornerShape(16.dp),
            colors = disabledPickerColors(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun disabledPickerColors() = OutlinedTextFieldDefaults.colors(
    disabledTextColor = MaterialTheme.colorScheme.onSurface,
    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
    disabledLeadingIconColor = MaterialTheme.colorScheme.primary,
    disabledContainerColor = MaterialTheme.colorScheme.surface,
    disabledBorderColor = MaterialTheme.colorScheme.outlineVariant,
    errorContainerColor = MaterialTheme.colorScheme.surface,
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
)

private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale.US)

private fun String.toCalendarDateOrToday(): Calendar {
    val parsedDate = runCatching { DATE_FORMAT.parse(trim()) }.getOrNull()
    return Calendar.getInstance().apply {
        if (parsedDate != null) time = parsedDate
    }
}

private fun String.toCalendarTimeOrNow(): Calendar {
    val timeParts = split(":")
    val hour = timeParts.getOrNull(0)?.toIntOrNull()
    val minute = timeParts.getOrNull(1)?.toIntOrNull()
    return Calendar.getInstance().apply {
        if (hour != null && minute != null && hour in 0..23 && minute in 0..59) {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }
    }
}

private fun Int.twoDigits(): String {
    return toString().padStart(2, '0')
}
