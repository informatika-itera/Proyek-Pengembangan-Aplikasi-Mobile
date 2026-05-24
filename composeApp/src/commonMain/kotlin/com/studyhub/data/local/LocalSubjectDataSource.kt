package com.studyhub.data.local

import com.studyhub.database.StudyHubDatabase
import com.studyhub.domain.model.Subject

class LocalSubjectDataSource(private val database: StudyHubDatabase) {

    fun selectAllSubjects(): List<Subject> =
        database.subjectEntityQueries.selectAllSubjects()
            .executeAsList()
            .map { Subject(it.id, it.name, it.colorHex, it.icon, it.createdAt) }

    fun insertSubject(subject: Subject) {
        database.subjectEntityQueries.insertSubject(
            subject.id, subject.name, subject.colorHex, subject.icon, subject.createdAt
        )
    }

    fun updateSubject(subject: Subject) {
        database.subjectEntityQueries.updateSubject(
            subject.name, subject.colorHex, subject.icon, subject.id
        )
    }

    fun deleteSubject(id: String) {
        database.subjectEntityQueries.deleteSubject(id)
    }
}
