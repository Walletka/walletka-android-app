package com.walletka.app.usecases

import com.walletka.app.AppState
import com.walletka.app.enums.WalletkaConnectionStatus
import com.walletka.app.wallet.LightningWallet
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetConnectionStatusUseCase @Inject constructor(
    private val appState: AppState,
    private val lightningWallet: LightningWallet
) {

    operator fun invoke(): kotlinx.coroutines.flow.Flow<WalletkaConnectionStatusDto> {
        val internetConnected = flow<Boolean> { emit(true) } // Todo
        val lspConnected = lightningWallet.peers.map { it.firstOrNull { it.nodeId == appState.lspPeerId }?.isConnected ?: false }
        return combine(internetConnected, lspConnected) { i, l ->
            WalletkaConnectionStatusDto(
                internetConnected = i,
                lspConnected = l
            )
        }
    }

}

data class WalletkaConnectionStatusDto(
    val internetConnected: Boolean,
    val lspConnected: Boolean
) {
    fun status(): WalletkaConnectionStatus {
        if (!internetConnected)
            return WalletkaConnectionStatus.Offline

        return if (lspConnected) WalletkaConnectionStatus.Connected
        else WalletkaConnectionStatus.NotConnected
    }
}
