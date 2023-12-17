package com.walletka.app.enums

enum class PayInvoiceResult {
    Waiting, Success, Error;

    companion object {
        fun byNameIgnoreCaseOrNull(input: String): PayInvoiceResult? {
            return PayInvoiceResult.values().firstOrNull { it.name.equals(input, true) }
        }

        fun fromOrdinal(ordinal: Int): PayInvoiceResult? {
            return PayInvoiceResult.values().getOrNull(ordinal)
        }
    }
}