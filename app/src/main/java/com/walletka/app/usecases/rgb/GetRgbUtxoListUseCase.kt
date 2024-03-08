package com.walletka.app.usecases.rgb

import com.walletka.app.dto.RgbUnspentDto
import com.walletka.app.wallet.RgbWallet
import org.rgbtools.Unspent
import javax.inject.Inject

class GetRgbUtxoListUseCase @Inject constructor(
    private val rgbWallet: RgbWallet
) {

    operator fun invoke(): kotlinx.coroutines.flow.Flow<Map<Unspent, List<RgbUnspentDto>>> {
        return rgbWallet.rgbUtxos
    }

}