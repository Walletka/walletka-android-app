package com.walletka.app.usecases.cashu

import com.walletka.app.io.entity.CashuTokenEntity
import com.walletka.app.wallet.WalletkaCore
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCashuTokensUseCase @Inject constructor(
    private val walletkaCore: WalletkaCore
) {

    suspend operator fun invoke(): kotlinx.coroutines.flow.Flow<Map<String, List<CashuTokenEntity>>> {
        return flow {
        }
    }
}
