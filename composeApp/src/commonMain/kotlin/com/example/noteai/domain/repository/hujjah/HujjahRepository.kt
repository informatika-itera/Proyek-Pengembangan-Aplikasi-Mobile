package com.example.noteai.domain.repository.hujjah

import com.example.noteai.domain.model.islamic.IslamicReference
import com.example.noteai.domain.model.islamic.TopicOption
import kotlinx.coroutines.flow.Flow

interface HujjahRepository {
    fun getTopics(): Flow<List<TopicOption>>
    fun getReferencesByTopic(topicId: String): Flow<List<IslamicReference>>
    fun getReferenceById(referenceId: String): Flow<IslamicReference?>
}
