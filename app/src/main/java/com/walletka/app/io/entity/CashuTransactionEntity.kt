package com.walletka.app.io.entity

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "cashu_transactions")
data class CashuTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val sent: Boolean,
    val amount: Long,
    val timestamp: Long,
    val memo: String? = null,
    val fees: Long = 0,
    val secret: String? = null
) {
}

@Dao
interface CashuTransactionsDao {
    @Query("SELECT * FROM cashu_transactions")
    fun getAll(): Flow<List<CashuTransactionEntity>>


    @Query("SELECT * FROM cashu_transactions")
    fun getAllAsList(): List<CashuTransactionEntity>

    @Insert
    fun insert(vararg token: CashuTransactionEntity)

    @Delete
    fun delete(token: CashuTransactionEntity)
}