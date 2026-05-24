package com.studyhub.domain.repository

import com.studyhub.domain.model.Subject

interface SubjectRepository {
    suspend fun getAllSubjects(): List<Subject>
    suspend fun addSubject(subject: Subject)
    suspend fun updateSubject(subject: Subject)
    suspend fun deleteSubject(subjectId: String)
}
