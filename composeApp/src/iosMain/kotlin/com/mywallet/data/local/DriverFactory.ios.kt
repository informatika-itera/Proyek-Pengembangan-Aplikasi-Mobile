package com.mywallet.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.mywallet.db.WalletDatabase

class IosDriverFactory : DriverFactory {
    override fun createDriver(): SqlDriver {
        return NativeSqliteDriver(WalletDatabase.Schema, "wallet_v4.db")
    }
}
