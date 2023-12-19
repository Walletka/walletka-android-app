package com.walletka.app.ui.pages.scanner

import android.util.Log
import com.walletka.app.dto.QrCodeResultDto

fun getQrCodeResultRoute(result: QrCodeResultDto): String? {
    when (result) {
        is QrCodeResultDto.BitcoinAddress -> {
            Log.i("QrCodeScanner", "Found BitcoinAddress ${result.address}")
            var route = "pay?destination=" +result.address

            result.amountSat?.let {
                route += "&amount=$it"
            }

            return route
        }

        is QrCodeResultDto.Bolt11Invoice -> {
            Log.i("QrCodeScanner", "Found Bolt11 invoice ${result.bolt11Invoice}")
            var route = "pay?destination=" + result.bolt11Invoice

            result.amount?.let {
                route += "&amount=$it"
            }

            return route
        }

        is QrCodeResultDto.CashuToken -> {
            Log.i("QrCodeScanner", "Found Cashu token ${result.token}")

            return "claimCashuToken?token=${result.token}"
        }

        is QrCodeResultDto.EmailAddress -> {
            Log.i("QrCodeScanner", "Found Email address ${result.emailAddress}")
        }

        is QrCodeResultDto.UnsupportedFormat -> {
            Log.i("QrCodeScanner", "Found Unknown value ${result.rawValue}")
        }

        is QrCodeResultDto.Url -> {
            Log.i("QrCodeScanner", "Found Url ${result.url}")
        }
        is QrCodeResultDto.Npub -> {
            Log.i("QrCodeScanner", "Found Npub ${result.npub}")
            return "pay?destination=${result.npub}"
        }
    }
    return null
}