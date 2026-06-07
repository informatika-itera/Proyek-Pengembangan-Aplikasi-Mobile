package com.studyhub.data.fake

import com.studyhub.data.local.NotifHistoryDataSource
import com.studyhub.domain.model.NotifHistoryItem
import kotlinx.datetime.Clock

class FakeNotifHistoryDataSource : NotifHistoryDataSource {
    val items = mutableListOf<NotifHistoryItem>()
    var lastMarkedReadId: String? = null
    var clearAllCalled = false
    var insertCalled = false

    override fun getAll() = items.toList()
    override fun getUnreadCount() = items.count { !it.isRead }
    override fun getCount() = items.size
    
    override fun insert(taskId: String, taskTitle: String,
        taskSubject: String, aiReason: String) {
        insertCalled = true
        items.add(NotifHistoryItem(
            id = "test_${items.size}",
            taskId = taskId,
            taskTitle = taskTitle,
            taskSubject = taskSubject,
            aiReason = aiReason,
            sentAt = Clock.System.now().toEpochMilliseconds(),
            isRead = false
        ))
    }
    
    override fun markAllRead() { 
        // No-op
    }
    
    override fun markRead(id: String) { 
        lastMarkedReadId = id 
    }
    
    override fun deleteById(id: String) { 
        items.removeAll { it.id == id } 
    }
    
    override fun deleteAll() { 
        clearAllCalled = true
        items.clear() 
    }
    
    override fun deleteOlderThan(timestamp: Long) {
        items.removeAll { it.sentAt < timestamp }
    }
    
    override fun deleteExcessItems(max: Int) {
        if (items.size > max) {
            val toRemove = items.size - max
            repeat(toRemove) { items.removeAt(0) }
        }
    }
}
