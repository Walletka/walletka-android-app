package com.walletka.app.usecases

import arrow.core.padZip
import com.walletka.app.dto.Amount
import com.walletka.app.dto.TransactionListItemDto
import com.walletka.app.enums.TransactionDirection
import com.walletka.app.enums.WalletLayer
import com.walletka.app.ui.components.TransactionListItem
import com.walletka.app.wallet.BlockchainWallet
import com.walletka.app.wallet.CashuWallet
import com.walletka.app.wallet.LightningWallet
import com.walletka.app.wallet.RgbWallet
import com.walletka.app.wallet.RootstockWallet
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.rgbtools.TransferKind
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val blockchainWallet: BlockchainWallet,
    private val lightningWallet: LightningWallet,
    private val cashuWallet: CashuWallet,
    private val rgbWallet: RgbWallet,
    private val rootstockWallet: RootstockWallet,
) {

    suspend operator fun invoke(params: Params): kotlinx.coroutines.flow.Flow<List<TransactionListItemDto>> {

        val blockchainTransactions = blockchainWallet.transactions.map {
            it.sortedByDescending { it.confirmationTime?.timestamp ?: ULong.MAX_VALUE }.map { tx ->
                val isSend = tx.sent > 0u
                val zoneId = ZoneId
                    .systemDefault()
                    .rules
                    .getOffset(
                        Instant.now()
                    );
                TransactionListItemDto(
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
                    tx.confirmationTime != null
                )
            }
        }

        val lightningTransactions = lightningWallet.transactions.map {
            it.map { tx ->
                TransactionListItemDto(
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
                    true
                )
            }
        }

        val cashuTransactions = cashuWallet.transactionsFlow.map {
            it.sortedByDescending { it.timestamp }.map { tx ->
                TransactionListItemDto(
                    "cashu-${tx.id}",
                    if (tx.sent) TransactionDirection.Sent else TransactionDirection.Received,
                    Amount.fromSats(tx.amount.toULong()),
                    tx.memo ?: if (tx.sent) TransactionDirection.Sent.name else TransactionDirection.Received.name,
                    "",
                    LocalDateTime.ofEpochSecond(tx.timestamp, 0, ZoneOffset.UTC),
                    WalletLayer.Cashu,
                    true
                )
            }
        }

        val rgbTransactions = rgbWallet.rgbAssets.map {
            it.flatMap { it.value }.sortedByDescending { it.time }.map { tx ->
                TransactionListItemDto(
                    tx.id,
                    tx.direction,
                    tx.amount,
                    tx.primaryText,
                    tx.secondaryText,
                    tx.time,
                    tx.walletLayer,
                    tx.confirmed
                )
            }
        }

        val rootstockTransactions = rootstockWallet.transactions.map {
            it.map {tx ->
                TransactionListItemDto(
                    tx.hash,
                    TransactionDirection.Received,
                    Amount.fromSats(tx.value.toLong().toULong() / 1000000000u),
                    "PText",
                    "SText",
                    LocalDateTime.now(),
                    WalletLayer.Rootstock,
                    true
                )
            }
        }

        return combine(
            blockchainTransactions,
            lightningTransactions,
            cashuTransactions,
            rgbTransactions,
            rootstockTransactions) { b, l, c, r, r1 -> b + l + c + r + r1 }
    }


    data class Params(
        val onchain: Boolean = true,
        val lightning: Boolean = true,
        val cashu: Boolean = true
    )
}