package id.my.sinanonym.mybawanggacha

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
