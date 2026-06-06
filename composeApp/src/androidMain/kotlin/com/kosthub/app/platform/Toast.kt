package com.kosthub.app.platform

import android.widget.Toast
import android.content.Intent
import android.net.Uri
import android.app.Activity

actual fun showToast(platformContext: PlatformContext, message: String) {
    Toast.makeText(platformContext.context, message, Toast.LENGTH_SHORT).show()
}

actual fun openDialer(platformContext: PlatformContext, phoneNumber: String) {
    val context = platformContext.context
    val cleanPhone = phoneNumber.filter { it.isDigit() || it == '+' }
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$cleanPhone")
    }
    if (context !is Activity) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}
