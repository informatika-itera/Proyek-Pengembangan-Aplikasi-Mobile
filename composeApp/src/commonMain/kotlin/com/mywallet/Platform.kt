package com.mywallet

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform