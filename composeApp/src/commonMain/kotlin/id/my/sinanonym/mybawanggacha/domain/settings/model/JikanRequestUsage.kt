package id.my.sinanonym.mybawanggacha.domain.settings.model

data class JikanRequestUsage(
    val usedLastSecond: Int = 0,
    val secondLimit: Int = 3,
    val usedLastMinute: Int = 0,
    val minuteLimit: Int = 60,
    val remainingThisMinute: Int = 60,
    val msUntilNextRequest: Long = 0L
) {
    companion object {
        val Empty = JikanRequestUsage()
    }
}
