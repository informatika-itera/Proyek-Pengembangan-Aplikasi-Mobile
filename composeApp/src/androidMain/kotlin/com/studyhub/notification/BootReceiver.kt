package com.studyhub.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.studyhub.domain.repository.ReminderRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BootReceiver : BroadcastReceiver(), KoinComponent {
    
    private val reminderRepository: ReminderRepository by inject()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    reminderRepository.rescheduleAllOnBoot()
                } catch (e: Exception) { }
            }
        }
    }
}
