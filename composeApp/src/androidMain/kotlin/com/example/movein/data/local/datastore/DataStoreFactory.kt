package com.example.movein.data.local.datastore

import android.content.Context

class AndroidDataStoreFactory(private val context: Context) : DataStoreFactory {
    override fun producePath(): String {
        return context.filesDir.absolutePath
    }
}