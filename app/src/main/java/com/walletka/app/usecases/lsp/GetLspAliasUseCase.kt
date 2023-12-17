package com.walletka.app.usecases.lsp

import com.walletka.app.io.repository.LspRepository
import javax.inject.Inject

class GetLspAliasUseCase @Inject constructor(
    private val lspRepository: LspRepository
) {
    suspend operator fun invoke(): String? {
        return lspRepository.getAlias()
    }
}