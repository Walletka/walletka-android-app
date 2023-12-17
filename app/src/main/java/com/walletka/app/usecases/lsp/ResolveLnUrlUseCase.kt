package com.walletka.app.usecases.lsp

import android.util.Log
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import com.walletka.app.io.client.LspClient
import com.walletka.app.io.client.model.LnUrlInvoice
import javax.inject.Inject

class ResolveLnUrlUseCase @Inject constructor(
    private val lspClient: LspClient
) {

    suspend operator fun invoke(url: String, amount: ULong? = null): Option<LnUrlInvoice> {
        return try {
            val res = lspClient.getLnUrlInvoice(url, amount) ?: return None
            Some(res)
        } catch (e: Exception) {
            Log.e("ResolveLnUrlUC", e.localizedMessage)
            None
        }
    }

}