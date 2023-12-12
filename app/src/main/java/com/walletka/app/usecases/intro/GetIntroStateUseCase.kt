package com.walletka.app.usecases.intro

import android.content.SharedPreferences
import com.walletka.app.enums.IntroState
import javax.inject.Inject

class GetIntroStateUseCase @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    operator fun invoke(): IntroState {
        val stateName = sharedPreferences.getString("intro_state", "")

        return stateName?.let { IntroState.byNameIgnoreCaseOrNull(it) } ?: IntroState.Welcome
    }

}