package com.walletka.app.usecases

import android.util.Log
import android.webkit.URLUtil
import com.walletka.app.dto.Amount
import com.walletka.app.dto.QrCodeResultDto
import javax.inject.Inject

class AnalyzeQrCodeUseCase @Inject constructor() {

    operator fun invoke(input: String): QrCodeResultDto {
        if (input.startsWith("ln")) {
            return QrCodeResultDto.Bolt11Invoice(input, null, null)
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
                Amount.fromSats(amount ?: 0u),
                address
            ) else QrCodeResultDto.BitcoinAddress(address, Amount.fromSats(amount ?: 0u))
        }

        if (input.startsWith("cashuA")) {
            return QrCodeResultDto.CashuToken(input)
        }

        if (input.startsWith("npub")) {
            return QrCodeResultDto.Npub(input)
        }

        if (URLUtil.isValidUrl(input)) {
            return QrCodeResultDto.Url(input)
        }
//rgb:~/~/utxob:R3Cdz9R-VLYgy3kHc-xnBHjDBGz-951RdnoBj-W5Nt8C1CM-LNxsSn?expiry=1708551767&endpoints=rpcs://proxy.iriswallet.com/0.2/json-rpc

        if (input.startsWith("rgb:~") || input.startsWith("bcrt:utxob:")) {
            val utxob =
                if (input.startsWith("rgb:~"))
                    input.substring(0, input.indexOfFirst { it == '?' })
                        .removePrefix("rgb:~/~/")
                else input

            val parameters = input.split('&').map {
                val parts = it.split('=')
                val name = parts.firstOrNull() ?: ""
                val value = parts.drop(1).firstOrNull() ?: ""
                Pair(name, value)
            }.associate { Pair(it.first, it.second) }
            return QrCodeResultDto.RgbInvoice(
                utxob,
                parameters["expiry"]?.toULong() ?: 0u,
                parameters["endpoints"] ?: ""
            )
        }

        try {
            // Todo validate Address(input)
            return QrCodeResultDto.BitcoinAddress(input, Amount.zero)
        } catch (_: Exception) {

        }

        if (input.startsWith("0x")) {
            return QrCodeResultDto.RootstockAddress(input)
        }

        return QrCodeResultDto.UnsupportedFormat(input)
    }

}