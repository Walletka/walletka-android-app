package com.walletka.app.io.entity

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "lightning_transactions")
data class LightningTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val sent: Boolean,
    val amountMsat: Long,
    val timestamp: Long,
    val memo: String? = null,
) {
}

@Dao
interface LightningTransactionsDao {
    @Query("SELECT * FROM lightning_transactions")
    fun getAll(): Flow<List<LightningTransactionEntity>>


    @Query("SELECT * FROM lightning_transactions")
    fun getAllAsList(): List<LightningTransactionEntity>

    @Insert
    fun insert(vararg token: LightningTransactionEntity)

    @Delete
    fun delete(token: LightningTransactionEntity)
}