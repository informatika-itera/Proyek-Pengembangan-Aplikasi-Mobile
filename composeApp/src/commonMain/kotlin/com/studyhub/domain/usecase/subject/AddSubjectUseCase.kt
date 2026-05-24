package com.studyhub.domain.usecase.subject

import com.studyhub.domain.model.Subject
import com.studyhub.domain.repository.SubjectRepository

class AddSubjectUseCase(private val subjectRepository: SubjectRepository) {
    suspend operator fun invoke(subject: Subject) {
        require(subject.name.isNotBlank()) { "Nama mata kuliah tidak boleh kosong" }
        subjectRepository.addSubject(subject)
    }
}
