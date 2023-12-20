package com.walletka.app.dto

import java.math.BigDecimal

class Amount private constructor(private val amountMsat: ULong) {

    fun sats(): ULong {
        return amountMsat / MILISATS_IN_SAT.toULong()
    }

    fun msats(): ULong {
        return amountMsat
    }

    fun btc(): BigDecimal {
        return amountMsat.toString().toBigDecimal().div(BigDecimal(SATS_IN_BTC)).div(BigDecimal(MILISATS_IN_SAT))
    }

    operator fun plus(amount: Amount): Amount {
        return fromMsat(amountMsat + amount.msats())
    }

    operator fun minus(amount: Amount): Amount {
        return fromMsat(amountMsat - amount.msats())
    }

    companion object {

        private val SATS_IN_BTC = 100000000
        private val MILISATS_IN_SAT = 1000
        val zero = Amount.fromMsat(0u)

        fun fromSats(amount: ULong): Amount {
            return Amount(amount * 1000u)
        }

        fun fromMsat(amount: ULong): Amount {
            return Amount(amount)
        }

        fun fromBtc(amount: ULong): Amount {
            return Amount(amount * SATS_IN_BTC.toULong() * MILISATS_IN_SAT.toULong())
        }
    }
}