package com.kosthub.app.data.local

class KostDatabaseFactory(private val driverFactory: DatabaseDriverFactory) {
    fun create(): KostDatabase {
        return KostDatabase(driverFactory.createDriver())
    }
}
