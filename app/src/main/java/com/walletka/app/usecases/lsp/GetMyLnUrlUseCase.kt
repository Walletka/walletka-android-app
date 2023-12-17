package com.walletka.app.usecases.lsp

import com.walletka.app.io.client.LspClient
import com.walletka.app.io.repository.LspRepository
import javax.inject.Inject

class GetMyLnUrlUseCase @Inject constructor(
    private val lspRepository: LspRepository,
    private val lspClient: LspClient
) {

    suspend operator fun invoke(amountMSat: ULong? = null): String? {
        val alias = lspRepository.getAlias() ?: return null

        return lspClient.getLnUrl(alias, amountMSat)
    }

}