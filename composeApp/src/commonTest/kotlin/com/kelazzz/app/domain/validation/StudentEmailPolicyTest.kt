package com.kelazzz.app.domain.validation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StudentEmailPolicyTest {

    @Test
    fun acceptsStudentEmailAndUsernameWithoutDomain() {
        assertTrue(StudentEmailPolicy.acceptsLoginInput("nama.123@student.itera.ac.id"))
        assertTrue(StudentEmailPolicy.acceptsLoginInput("123140098"))
    }

    @Test
    fun rejectsNonStudentIteraEmail() {
        assertFalse(StudentEmailPolicy.acceptsLoginInput("dosen@itera.ac.id"))
        assertFalse(StudentEmailPolicy.acceptsLoginInput("admin@example.com"))
        assertFalse(StudentEmailPolicy.acceptsLoginInput("@student.itera.ac.id"))
    }

    @Test
    fun normalizesLoginInputToStudentEmail() {
        assertEquals(
            "123140098@student.itera.ac.id",
            StudentEmailPolicy.normalizeLoginInput(" 123140098 ")
        )
        assertEquals(
            "nama.123@student.itera.ac.id",
            StudentEmailPolicy.normalizeLoginInput("Nama.123@STUDENT.ITERA.AC.ID")
        )
    }
}
