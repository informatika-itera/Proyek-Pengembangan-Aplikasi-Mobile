package com.kosthub.app.platform

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual fun showToast(platformContext: PlatformContext, message: String) {
    println("iOS Toast: $message")
}

actual fun openDialer(platformContext: PlatformContext, phoneNumber: String) {
    val cleanPhone = phoneNumber.filter { it.isDigit() || it == '+' }
    val url = NSURL(string = "tel:$cleanPhone")
    if (UIApplication.sharedApplication.canOpenURL(url)) {
        UIApplication.sharedApplication.openURL(url)
    }
}
