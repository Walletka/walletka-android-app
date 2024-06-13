package com.walletka.app.usecases

import android.util.Log
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.flatten
import com.walletka.app.dto.Amount
import com.walletka.app.dto.TransactionDetailDto
import com.walletka.app.enums.TransactionDirection
import com.walletka.app.enums.WalletLayer
import com.walletka.app.io.repository.LdkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import javax.inject.Inject

class GetTransactionUseCase @Inject constructor(
    private val ldkRepository: LdkRepository,
) {

    suspend operator fun invoke(params: GetTransactionParams): Option<TransactionDetailDto> = withContext(Dispatchers.IO) {
        when (params.layer) {
            WalletLayer.Blockchain -> {
                return@withContext None
            }

            WalletLayer.Lightning -> {
                return@withContext None
            }

            WalletLayer.Cashu -> {
                return@withContext None
            }

            WalletLayer.RGB -> {
                return@withContext None
            }

            WalletLayer.Rootstock -> {
                // Todo
                return@withContext None
            }

            WalletLayer.Core -> {
                return@withContext None
            }

            WalletLayer.All -> TODO()
        }
    }

    data class GetTransactionParams(
        val layer: WalletLayer,
        val txId: String
    )

}