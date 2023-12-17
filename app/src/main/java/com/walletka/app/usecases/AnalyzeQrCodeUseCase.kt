package com.walletka.app.usecases

import com.walletka.app.dto.QrCodeResultDto
import javax.inject.Inject

class AnalyzeQrCodeUseCase @Inject constructor() {

    operator fun invoke(input: String): QrCodeResultDto {
        if (input.startsWith("ln")) {
            return QrCodeResultDto.Bolt11Invoice(input,null,null)
        }

        if (input.startsWith("bitcoin:")) {
            val addressEndIndex = if (input.indexOf("?") != -1) input.indexOf("?") else input.length
            val address = input.substring(8, addressEndIndex)
            val params = input.substring(input.indexOf("?") + 1).split("&").map {
                val parts = it.split('=')
                val name = parts.firstOrNull() ?: ""
                val value = parts.drop(1).firstOrNull() ?: ""
                Pair(name, value)
            }
            val lnInvoice = params.firstOrNull { it.first == "lightning" }?.second
            val amount =
                params.firstOrNull { it.first == "amount" }?.second?.toDouble()?.times(100000000)
                    ?.toULong()

            return if (lnInvoice != null) QrCodeResultDto.Bolt11Invoice(
                lnInvoice,
                amount,
                address
            ) else QrCodeResultDto.BitcoinAddress(address, amount)
        }

        if (input.startsWith("cashuA")) {
            return QrCodeResultDto.CashuToken(input)
        }

        if (input.startsWith("npub")) {
            return QrCodeResultDto.Npub(input)
        }

        return QrCodeResultDto.UnsupportedFormat(input)
    }

}