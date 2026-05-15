package com.dailybliss.app.core.util

import io.ktor.util.encodeBase64

fun ByteArray.toBase64(): String = this.encodeBase64()
