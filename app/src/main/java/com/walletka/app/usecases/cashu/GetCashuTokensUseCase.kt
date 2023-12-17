package com.walletka.app.usecases.cashu

import com.walletka.app.io.entity.CashuTokenEntity
import com.walletka.app.wallet.CashuWallet
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCashuTokensUseCase @Inject constructor(
    private val cashuWallet: CashuWallet
) {

    suspend operator fun invoke(): kotlinx.coroutines.flow.Flow<Map<String, List<CashuTokenEntity>>> {
        return cashuWallet.tokensFlow.map {
            it.groupBy { it.mintUrl }.toMap()
        }
    }
}
