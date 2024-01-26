package com.walletka.app.usecases

import android.util.Log
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import com.walletka.app.dto.Amount
import com.walletka.app.dto.TransactionDetailDto
import com.walletka.app.dto.TransactionListItemDto
import com.walletka.app.enums.TransactionDirection
import com.walletka.app.enums.WalletLayer
import com.walletka.app.io.repository.LdkRepository
import com.walletka.app.wallet.BlockchainWallet
import com.walletka.app.wallet.CashuWallet
import com.walletka.app.wallet.LightningWallet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import javax.inject.Inject

class GetTransactionUseCase @Inject constructor(
    private val blockchainWallet: BlockchainWallet,
    private val ldkRepository: LdkRepository,
    private val cashuWallet: CashuWallet
) {

    suspend operator fun invoke(params: GetTransactionParams): Option<TransactionDetailDto> = withContext(Dispatchers.IO) {
        when (params.layer) {
            WalletLayer.Blockchain -> {
                blockchainWallet.getTransactions().firstOrNull { it.txid == params.txId }?.let { tx ->
                    val isSend = tx.sent > 0u
                    val zoneId = ZoneId
                        .systemDefault()
                        .rules
                        .getOffset(
                            Instant.now()
                        )

                    return@withContext Some(
                        TransactionDetailDto.BlockchainTransactionDetailDto(
                            tx.txid,
                            if (isSend) TransactionDirection.Sent else TransactionDirection.Received,
                            Amount.fromSats(if (isSend) tx.sent - tx.received else tx.received),
                            if (isSend) TransactionDirection.Sent.name else TransactionDirection.Received.name,
                            "",
                            LocalDateTime.ofEpochSecond(
                                tx.confirmationTime?.timestamp?.toLong() ?: LocalDateTime.now().toEpochSecond(zoneId),
                                0,
                                zoneId
                            ),
                            WalletLayer.Blockchain,
                            tx.confirmationTime != null,
                            Amount.fromSats(tx.fee ?: 0u),
                            tx
                        )
                    )
                }
                return@withContext None
            }

            WalletLayer.Lightning -> {
                try {
                    ldkRepository.getTransaction(params.txId.replace("lightning-", "").toInt())?.let { tx ->
                        return@withContext Some(
                            TransactionDetailDto.LightningTransactionDetailDto(
                                "lightning-${tx.id}",
                                if (tx.sent) TransactionDirection.Sent else TransactionDirection.Received,
                                Amount.fromMsat(tx.amountMsat.toULong()),
                                tx.memo ?: if (tx.sent) TransactionDirection.Sent.name else TransactionDirection.Received.name,
                                "",
                                LocalDateTime.ofEpochSecond(
                                    tx.timestamp,
                                    0,
                                    ZoneOffset.UTC
                                ),
                                WalletLayer.Lightning,
                                true,
                                Amount.zero, // Todo
                                tx.secret
                            )
                        )

                    }
                } catch (e: Exception) {
                    Log.e("GetTxUC", e.localizedMessage)
                }
                return@withContext None
            }

            WalletLayer.Cashu -> {
                cashuWallet.getAllTransactions().firstOrNull { "cashu-${it.id}" == params.txId }?.let { tx ->
                    return@withContext Some(
                        TransactionDetailDto.CashuTransactionDetailDto(
                            "cashu-${tx.id}",
                            if (tx.sent) TransactionDirection.Sent else TransactionDirection.Received,
                            Amount.fromSats(tx.amount.toULong()),
                            tx.memo ?: "",
                            "",
                            LocalDateTime.ofEpochSecond(tx.timestamp, 0, ZoneOffset.UTC),
                            WalletLayer.Cashu,
                            true, // Todo
                            Amount.fromSats(tx.fees.toULong()),
                            tx.secret // Todo
                        )
                    )
                }
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