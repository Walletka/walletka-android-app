package com.walletka.app.io.repository

import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LspRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    suspend fun storeAlias(alias: String) = withContext(Dispatchers.IO) {
        sharedPreferences.edit().putString("lsp_alias", alias).apply()
    }

    suspend fun getAlias(): String? = withContext(Dispatchers.IO) {
        sharedPreferences.getString("lsp_alias", null)
    }

}