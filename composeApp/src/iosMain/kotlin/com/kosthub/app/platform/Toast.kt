package com.kosthub.app.platform

actual fun showToast(platformContext: PlatformContext, message: String) {
    println("iOS Toast: $message")
}
