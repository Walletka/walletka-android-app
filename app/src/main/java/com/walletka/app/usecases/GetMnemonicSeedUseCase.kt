package com.walletka.app.usecases

import android.content.SharedPreferences
import arrow.core.Option
import arrow.core.toOption
import com.walletka.app.di.MnemonicSeedProvider
import javax.inject.Inject

class GetMnemonicSeedUseCase @Inject constructor(
    private val mnemonicSeedProvider: MnemonicSeedProvider
) {
    operator fun invoke(): Option<String> {
        return mnemonicSeedProvider.get().toOption()
    }
}