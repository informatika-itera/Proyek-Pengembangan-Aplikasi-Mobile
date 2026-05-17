package com.example.pusakakata

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform