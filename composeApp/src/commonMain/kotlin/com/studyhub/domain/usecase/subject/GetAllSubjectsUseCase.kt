package com.studyhub.domain.usecase.subject

import com.studyhub.domain.model.Subject
import com.studyhub.domain.repository.SubjectRepository

class GetAllSubjectsUseCase(private val subjectRepository: SubjectRepository) {
    suspend operator fun invoke(): List<Subject> =
        subjectRepository.getAllSubjects()
}
