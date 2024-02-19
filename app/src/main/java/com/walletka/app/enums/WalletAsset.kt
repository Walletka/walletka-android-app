package com.walletka.app.enums

enum class WalletLayer {
    Blockchain, Lightning, Cashu, All;

    companion object {
        fun byNameIgnoreCaseOrNull(input: String): WalletLayer? {
            return entries.firstOrNull { it.name.equals(input, true) }
        }

        fun fromOrdinal(ordinal: Int): WalletLayer? {
            return entries.getOrNull(ordinal)
        }
    }
}
