package com.kosthub.app.platform

import android.content.Context

actual class PlatformContext actual constructor(val value: Any) {
	val context: Context
		get() = value as Context
}
