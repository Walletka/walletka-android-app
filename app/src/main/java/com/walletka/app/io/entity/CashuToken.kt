package com.walletka.app.io.entity

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "cashu_tokens")
data class CashuTokenEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val tokenId: String,
    val c: String,
    val amount: Long,
    val secret: String,
    @ColumnInfo(name = "mint_url")
    val mintUrl: String
) {
}

@Dao
interface CashuTokenDao {

    @Query("SELECT * FROM cashu_tokens")
    fun getAll(): Flow<List<CashuTokenEntity>>

    @Query("SELECT * FROM cashu_tokens")
    fun getAllTokensAsList(): List<CashuTokenEntity>

    @Insert
    fun insert(vararg token: CashuTokenEntity)

    @Delete
    fun delete(vararg token: CashuTokenEntity)

}