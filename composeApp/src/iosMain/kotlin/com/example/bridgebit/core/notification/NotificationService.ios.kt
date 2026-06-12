package com.example.bridgebit.core.notification

import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNUserNotificationCenter

actual class NotificationService actual constructor() {

    actual fun requestPermission(onResult: (Boolean) -> Unit) {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        center.requestAuthorizationWithOptions(
            UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
        ) { granted, _ ->
            onResult(granted)
        }
    }

    actual fun showNotification(title: String, body: String) {
        val content = UNMutableNotificationContent()
        content.setTitle(title)
        content.setBody(body)

        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = "bridgebit_${platform.Foundation.NSUUID().UUIDString()}",
            content = content,
            trigger = null // Menampilkan langsung (tanpa trigger waktu)
        )

        UNUserNotificationCenter.currentNotificationCenter().addNotificationRequest(request) { error ->
            if (error != null) {
                println("Error showing notification: ${error.localizedDescription}")
            }
        }
    }
}
