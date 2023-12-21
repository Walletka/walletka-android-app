package com.walletka.app.usecases

import com.walletka.app.errors.WalletkaError
import android.content.SharedPreferences
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import arrow.core.valid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bitcoindevkit.Mnemonic
import org.bitcoindevkit.WordCount
import javax.inject.Inject

class StoreMnemonicSeedUseCase @Inject constructor(
    private val sharedPreferences: SharedPreferences // TODO: Don't try to access access data layer directly from usecase
) {
    suspend operator fun invoke(params: Params): Either<WalletkaError, Unit> {
        // TODO: Use android keystore
        params.mnemonicSeed?.let {
            try {
                Mnemonic.fromString(it)
            } catch (e: Exception) {
                return WalletkaError.InvalidMnemonicSeed(e.localizedMessage).left()
            }
        }

        withContext(Dispatchers.IO) {
            val mnemonicSeed = params.mnemonicSeed ?: Mnemonic(WordCount.WORDS12).asString()
            sharedPreferences.edit().putString("mnemonic_seed", mnemonicSeed).apply()
        }
        return Unit.right()
    }

    data class Params(val mnemonicSeed: String?)

}