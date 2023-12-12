package com.walletka.app.io.repository

import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class IntroRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    private val INTRO_FINISHED_KEY = "intro_finished"

    fun isFinished(): Boolean {
        return sharedPreferences.getBoolean(INTRO_FINISHED_KEY, false)
    }

    suspend fun setFinished() {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit().putBoolean(INTRO_FINISHED_KEY, true).apply()
        }
    }
}