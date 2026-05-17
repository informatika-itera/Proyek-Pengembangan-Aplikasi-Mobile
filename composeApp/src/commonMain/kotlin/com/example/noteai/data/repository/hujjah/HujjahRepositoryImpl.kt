package com.example.noteai.data.repository.hujjah

import com.example.noteai.data.sample.SampleIslamicReferences
import com.example.noteai.domain.model.islamic.IslamicReference
import com.example.noteai.domain.model.islamic.TopicOption
import com.example.noteai.domain.repository.hujjah.HujjahRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class HujjahRepositoryImpl : HujjahRepository {
    override fun getTopics(): Flow<List<TopicOption>> {
        return flowOf(SampleIslamicReferences.topics)
    }

    override fun getReferencesByTopic(topicId: String): Flow<List<IslamicReference>> {
        return flowOf(
            SampleIslamicReferences.references.filter { reference ->
                reference.topicId == topicId
            }
        )
    }

    override fun getReferenceById(referenceId: String): Flow<IslamicReference?> {
        return flowOf(
            SampleIslamicReferences.references.find { reference ->
                reference.id == referenceId
            }
        )
    }
}
