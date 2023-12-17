package com.walletka.app.usecases.lsp

import android.util.Log
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.walletka.app.errors.WalletkaError
import com.walletka.app.io.client.LspClient
import com.walletka.app.io.repository.LspRepository
import javax.inject.Inject

class SignUpToLspUseCase @Inject constructor(
    private val lspClient: LspClient,
    private val lspRepository: LspRepository
) {

    suspend operator fun invoke(params: Params): Either<WalletkaError, String> {
        Log.i("SignupLspUC", "Signing up to the lsp")
        return try {
            val alias = lspClient.signUp(params.nodeId, params.npub) ?: return WalletkaError.CantSignupToLsp().left()
            Log.i("SignupLspUC", "Signed up successfully, given alias is $alias")

            lspRepository.storeAlias(alias)

            alias.right()
        } catch (e: Exception) {
            Log.e("SignupLspUC", e.localizedMessage)
            WalletkaError.CantSignupToLsp(e.localizedMessage).left()
        }
    }

    data class Params(
        val npub: String,
        val nodeId: String?
    )

}