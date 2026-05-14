package com.example.mybawanggacha

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
