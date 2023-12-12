package com.walletka.app.usecases.intro

import android.content.SharedPreferences
import com.walletka.app.enums.IntroState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SetIntroStateUseCase @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    suspend operator fun invoke(introState: IntroState) {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit().putString("intro_state", introState.name).apply()
        }
    }
}