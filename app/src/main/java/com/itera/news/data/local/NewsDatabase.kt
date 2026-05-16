package com.itera.news.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.itera.news.data.local.dao.ArticleDao
import com.itera.news.data.local.entity.ArticleEntity

@Database(entities = [ArticleEntity::class], version = 1, exportSchema = false)
abstract class NewsDatabase : RoomDatabase() {
    abstract val articleDao: ArticleDao
}