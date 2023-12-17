package com.walletka.app.io.repository

import android.content.SharedPreferences
import android.util.Log
import androidx.annotation.WorkerThread
import com.walletka.app.io.database.WalletkaDatabase
import com.walletka.app.io.entity.CashuTokenEntity
import com.walletka.app.io.entity.CashuTransactionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import nostr_sdk.Timestamp
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CashuRepository @Inject constructor(
    private val database: WalletkaDatabase,
    private val sharedPreferences: SharedPreferences
) {
    private val cashuTokenDao = database.cashuTokenDao()
    private val cashuTransactionsDao = database.cashuTransactionsDao()

    val tokens = cashuTokenDao.getAll()
    val transactions = cashuTransactionsDao.getAll()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun saveToken(token: CashuTokenEntity) = withContext(Dispatchers.IO) {
        Log.i("CashuRepository", "token stored")
        cashuTokenDao.insert(token)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun saveTransaction(sent: Boolean, amount: Long, timestamp: ULong? = null) = withContext(Dispatchers.IO) {
        cashuTransactionsDao.insert(
            CashuTransactionEntity(
                0,
                sent,
                amount,
                timestamp?.toLong() ?: Timestamp.now().asSecs().toLong()
            )
        )
    }

    @WorkerThread
    suspend fun deleteAllTokens(vararg tokens: CashuTokenEntity) = withContext(Dispatchers.IO) {
        cashuTokenDao.delete(*tokens)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun saveLastNostrReceivedTokenTime(timestamp: ULong) = withContext(Dispatchers.IO) {
        sharedPreferences.edit()
            .putLong("last_nostr_received_token_time", timestamp.toLong())
            .apply()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getLastNostrReceivedTokenTime(): ULong = withContext(Dispatchers.IO){
        return@withContext sharedPreferences.getLong("last_nostr_received_token_time", 0).toULong()
    }

    fun getAllTransactions(): List<CashuTransactionEntity> {
        return cashuTransactionsDao.getAllAsList()
    }

    fun getAllTokens(): List<CashuTokenEntity> {
        return cashuTokenDao.getAllTokensAsList()
    }
}
