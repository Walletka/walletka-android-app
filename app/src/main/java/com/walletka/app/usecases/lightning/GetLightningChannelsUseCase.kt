package com.walletka.app.usecases.lightning

import com.walletka.app.wallet.LightningWallet
import org.lightningdevkit.ldknode.ChannelDetails
import javax.inject.Inject

class GetLightningChannelsUseCase @Inject constructor(
    private val lightningWallet: LightningWallet
) {

    operator fun invoke(): kotlinx.coroutines.flow.Flow<List<ChannelDetails>> {
        return lightningWallet.channels
    }

}
