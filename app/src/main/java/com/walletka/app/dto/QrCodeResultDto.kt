package com.walletka.app.dto

sealed class QrCodeResultDto {
    data class BitcoinAddress(val address: String, val amount: Amount?): QrCodeResultDto()
    data class Bolt11Invoice(val bolt11Invoice: String, val amount: Amount?, val fallback: String?): QrCodeResultDto()
    data class CashuToken(val token: String): QrCodeResultDto()
    data class UnsupportedFormat(val rawValue: String?): QrCodeResultDto()
    data class Url(val url: String): QrCodeResultDto()
    data class EmailAddress(val emailAddress: String): QrCodeResultDto()
    data class Npub(val npub: String): QrCodeResultDto()
    data class RgbInvoice(val utxob: String, val expiry: ULong, val endpoint: String): QrCodeResultDto()
}