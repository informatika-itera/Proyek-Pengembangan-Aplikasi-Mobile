package com.studyhub.core.util

import platform.Foundation.NSUUID

actual fun uuid(): String = NSUUID().UUIDString()
