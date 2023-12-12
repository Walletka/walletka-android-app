package com.walletka.app.enums

enum class IntroState {
    Welcome, IntroSlides, Mnemonic, Settings, Done;

    companion object {
        fun byNameIgnoreCaseOrNull(input: String): IntroState? {
            return values().firstOrNull { it.name.equals(input, true) }
        }

        fun fromOrdinal(ordinal: Int): IntroState? {
            return values().getOrNull(ordinal)
        }
    }
}