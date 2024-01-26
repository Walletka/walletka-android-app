package com.walletka.app.io.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.walletka.app.io.entity.CashuTokenDao
import com.walletka.app.io.entity.CashuTokenEntity
import com.walletka.app.io.entity.CashuTransactionEntity
import com.walletka.app.io.entity.CashuTransactionsDao
import com.walletka.app.io.entity.LightningTransactionEntity
import com.walletka.app.io.entity.LightningTransactionsDao

@Database(
    entities = [CashuTokenEntity::class, CashuTransactionEntity::class, LightningTransactionEntity::class],
    version = 4,
    exportSchema = false
)
public abstract class WalletkaDatabase : RoomDatabase() {

    abstract fun cashuTokenDao(): CashuTokenDao
    abstract fun cashuTransactionsDao(): CashuTransactionsDao
    abstract fun lightningTransactionsDao(): LightningTransactionsDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: WalletkaDatabase? = null

        fun getDatabase(context: Context): WalletkaDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WalletkaDatabase::class.java,
                    "walletka_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
