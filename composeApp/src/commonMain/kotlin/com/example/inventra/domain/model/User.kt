package com.example.inventra.domain.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val division: UserDivision,
    val studentId: String? = null,
    val phone: String? = null,
    val avatarUrl: String? = null,
    val isActive: Boolean = true,
    val divisionHead: String? = null,
    val staffList: String? = null // Comma separated names
)

enum class UserRole(val displayName: String) {
    ADMIN("Admin"),
    MEMBER("Anggota")
}

enum class UserDivision(val displayName: String) {
    BENDAHARA_UMUM("Bendahara Umum"),
    PUBDOK("Pubdok"),
    KONTEN("Konten"),
    DEKRAF("Dekraf"),
    TECHNOPRENEUR("Technopreneur"),
    BEASISWA("Beasiswa"),
    PPK("PPK"),
    KAJITEK("Kajitek"),
    INTRAKAMPUS("Intrakampus"),
    EKSTRAKAMPUS("Ekstrakampus"),
    SOSIAL_MASYARAKAT("Sosial Masyarakat"),
    KADERISASI("Kaderisasi"),
    PENGEMBANGAN_ANGGOTA("Pengembangan Anggota"),
    SENI_OLAHRAGA("Seni & Olahraga"),
    HARMONISASI("Harmonisasi")
}