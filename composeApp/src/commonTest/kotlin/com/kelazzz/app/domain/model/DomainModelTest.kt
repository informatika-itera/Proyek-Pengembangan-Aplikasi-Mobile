package com.kelazzz.app.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class DomainModelTest {

    @Test
    fun attendanceSummaryCalculatesPercentage() {
        val summary = AttendanceSummary(
            mataKuliahId = "IF001",
            mataKuliahNama = "Basis Data",
            totalPertemuan = 12,
            totalHadir = 9,
            totalAlpha = 2,
            totalIzin = 1,
            totalSakit = 0
        )

        assertEquals(75f, summary.persentaseKehadiran)
    }

    @Test
    fun attendanceSummaryRiskLevelUsesAlphaCount() {
        assertEquals(RiskLevel.AMAN, summaryWithAlpha(2).riskLevel)
        assertEquals(RiskLevel.WARNING, summaryWithAlpha(3).riskLevel)
        assertEquals(RiskLevel.BAHAYA, summaryWithAlpha(4).riskLevel)
    }

    @Test
    fun themeModeFallsBackToSystemForUnknownStoredValue() {
        assertEquals(ThemeMode.DARK, ThemeMode.fromStoredValue("DARK"))
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromStoredValue("unknown"))
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromStoredValue(null))
    }

    private fun summaryWithAlpha(alpha: Int) = AttendanceSummary(
        mataKuliahId = "IF001",
        mataKuliahNama = "Basis Data",
        totalPertemuan = 10,
        totalHadir = 10 - alpha,
        totalAlpha = alpha,
        totalIzin = 0,
        totalSakit = 0
    )
}
