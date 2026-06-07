package com.example.synesthesia.core.notification

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class JournalReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val helper = NotificationHelper(applicationContext)
        helper.showReminderNotification()
        return Result.success()
    }
}
