package com.walletka.app.dto

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.pow

class Amount private constructor(val amountMsat: ULong, val symbol: String, val decimals: UInt) {

    private val SATS_IN_BTC = 10.0.pow(decimals.toDouble())

    fun sats(): ULong {
        return amountMsat / MILISATS_IN_SAT.toULong()
    }

    fun msats(): ULong {
        return amountMsat
    }

    fun btc(): BigDecimal {
        if (decimals == 0u) {
            return BigDecimal(sats().toLong())
        }
        return BigDecimal(amountMsat.toLong()).divide(
            BigDecimal(SATS_IN_BTC) * BigDecimal(MILISATS_IN_SAT),
            decimals.toInt(),
            RoundingMode.HALF_UP
        )
    }

    operator fun plus(amount: Amount): Amount {
        return fromMsat(amountMsat + amount.msats())
    }

    operator fun minus(amount: Amount): Amount {
        return fromMsat(amountMsat - amount.msats())
    }

    companion object {

        private val MILISATS_IN_SAT = 1000
        private val DEFAULT_DECIMALS = 8u
        val zero = Amount.fromMsat(0u)
        val BITCOIN_SYMBOL = 0x20BF.toChar().toString()

        fun fromSats(amount: ULong, symbol: String? = null, decimals: UInt? = null): Amount {
            return Amount(amount * 1000u, symbol ?: BITCOIN_SYMBOL, decimals ?: DEFAULT_DECIMALS)
        }

        fun fromMsat(amount: ULong, symbol: String? = null, decimals: UInt? = null): Amount {
            return Amount(amount, symbol ?: BITCOIN_SYMBOL, decimals ?: DEFAULT_DECIMALS)
        }

        fun fromBtc(amount: ULong, symbol: String? = null, decimals: UInt? = null): Amount {
            return Amount(
                amount * 10.0.pow((decimals ?: DEFAULT_DECIMALS).toDouble()).toULong() * MILISATS_IN_SAT.toULong(),
                symbol ?: BITCOIN_SYMBOL,
                decimals ?: DEFAULT_DECIMALS
            )
        }
    }
}