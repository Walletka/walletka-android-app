package com.walletka.app.usecases.cashu

import com.walletka.app.io.entity.CashuTokenEntity
import com.walletka.app.walletka.CashuWallet
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class GetCashuTokensUseCase @Inject constructor(
    private val cashuWallet: CashuWallet
) {

    suspend operator fun invoke(): kotlinx.coroutines.flow.Flow<Map<String, List<CashuTokenEntity>>> {
        return flow {
            while (true) {
                val res = cashuWallet.getAllTokens().groupBy { it.mintUrl }.toMap()
                emit(res)

                delay(1000)
            }
        }
    }
}
