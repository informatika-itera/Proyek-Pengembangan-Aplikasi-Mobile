package com.studyhub.data.repository

import com.studyhub.data.local.LocalSubjectDataSource
import com.studyhub.domain.model.Subject
import com.studyhub.domain.repository.SubjectRepository

class SubjectRepositoryImpl(
    private val localDataSource: LocalSubjectDataSource
) : SubjectRepository {
    override suspend fun getAllSubjects() = localDataSource.selectAllSubjects()
    override suspend fun addSubject(subject: Subject) = localDataSource.insertSubject(subject)
    override suspend fun updateSubject(subject: Subject) = localDataSource.updateSubject(subject)
    override suspend fun deleteSubject(subjectId: String) = localDataSource.deleteSubject(subjectId)
}
