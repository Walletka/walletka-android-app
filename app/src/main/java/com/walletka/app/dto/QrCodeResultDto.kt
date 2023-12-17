package com.walletka.app.dto

sealed class QrCodeResultDto {
    data class BitcoinAddress(val address: String, val amountSat: ULong?): QrCodeResultDto()
    data class Bolt11Invoice(val bolt11Invoice: String, val amount: ULong?, val fallback: String?): QrCodeResultDto()
    data class CashuToken(val token: String): QrCodeResultDto()
    data class UnsupportedFormat(val rawValue: String?): QrCodeResultDto()
    data class Url(val url: String): QrCodeResultDto()
    data class EmailAddress(val emailAddress: String): QrCodeResultDto()
    data class Npub(val npub: String): QrCodeResultDto()
}