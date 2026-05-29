package com.kosthub.app.platform

import android.widget.Toast

actual fun showToast(platformContext: PlatformContext, message: String) {
    Toast.makeText(platformContext.context, message, Toast.LENGTH_SHORT).show()
}
