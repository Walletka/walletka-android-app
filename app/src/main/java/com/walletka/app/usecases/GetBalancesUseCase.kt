package com.walletka.app.usecases

import com.walletka.app.walletka.CashuWallet
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetBalancesUseCase @Inject constructor(
    private val cashuWallet: CashuWallet
) {

    suspend operator fun invoke(params: Params): kotlinx.coroutines.flow.Flow<GetBalancesResponse> {

        return flow {
            while (true) {
                // cashu
                val tokens = cashuWallet.getAllTokens()
                val cashuTotalBalance = tokens.sumOf { it.amount }

                emit(
                    GetBalancesResponse(
                        cashuBalanceSat = cashuTotalBalance.toULong()
                    )
                )
                delay(1000)
            }
        }
    }

    data class Params(
        val onchain: Boolean = true,
        val lightning: Boolean = true,
        val cashu: Boolean = true
    )

    data class GetBalancesResponse(
        val onchainBalanceSat: ULong = 0u,
        val lightningBalanceMSat: ULong = 0u,
        val cashuBalanceSat: ULong = 0u,
    )

}