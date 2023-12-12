package com.walletka.app.usecases

import android.content.SharedPreferences
import arrow.core.Option
import arrow.core.toOption
import javax.inject.Inject

class GetMnemonicSeedUseCase @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    operator fun invoke(): Option<String> {
        return sharedPreferences.getString("mnemonic_seed", null).toOption()
    }
}