package com.walletka.app.io.repository

import androidx.annotation.WorkerThread
import com.walletka.app.io.database.WalletkaDatabase
import com.walletka.app.io.entity.LightningTransactionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import nostr_sdk.Timestamp
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LdkRepository @Inject constructor(
    private val database: WalletkaDatabase
) {

    private val lightningTransactionsDao = database.lightningTransactionsDao()

    val transactions = lightningTransactionsDao.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun saveTransaction(sent: Boolean, amount: Long, description: String? = null, timestamp: ULong? = null) = withContext(Dispatchers.IO) {
        lightningTransactionsDao.insert(
            LightningTransactionEntity(
                0,
                sent,
                amount,
                timestamp?.toLong() ?: Timestamp.now().asSecs().toLong(),
                description
            )
        )
    }

    suspend fun getTransaction(id: Int): LightningTransactionEntity? {
        return lightningTransactionsDao.getById(id)
    }
}