package com.kosthub.app.platform

expect fun showToast(platformContext: PlatformContext, message: String)
expect fun openDialer(platformContext: PlatformContext, phoneNumber: String)
