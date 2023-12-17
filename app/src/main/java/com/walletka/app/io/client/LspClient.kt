package com.walletka.app.io.client

import android.util.Log
import com.walletka.app.AppState
import com.walletka.app.io.client.lsp.LspClientSpec
import com.walletka.app.io.client.model.LnUrlInvoice
import com.walletka.app.io.client.model.LspSignUpRequest
import com.walletka.app.io.client.model.RequestChannelRequest

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class LspClient @Inject constructor(
    private val appState: AppState
) {
    private val apiService by lazy {
        val client = Retrofit.Builder()
            .baseUrl(appState.lspBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        client.create(LspClientSpec::class.java)
    }

    suspend fun signUp(nodeId: String?, npub: String): String? =
        withContext(Dispatchers.IO) {
            val results = apiService.signUp(LspSignUpRequest(nodeId, npub))

            return@withContext results.body()?.alias
        }

    suspend fun isRegistered(alias: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val result = apiService.getUser(alias)
            return@withContext result.isSuccessful
        } catch (e: Exception) {
            Log.e("LspClient", "Cannot check if registered!", e)
            return@withContext false
        }
    }

    suspend fun requestChannel(
        alias: String,
        amount: ULong
    ): Boolean = withContext(Dispatchers.IO) {
        val result = apiService.requestChannel(RequestChannelRequest(alias, amount))
        return@withContext result.isSuccessful
    }

    fun getLnUrl(alias: String, amount: ULong? = null): String {
        var url = "${appState.lspBaseUrl}invoice/$alias"
        amount?.let {
            url += "?amount=$amount"
        }
        return url
    }

    suspend fun getLnUrlInvoice(url: String, amount: ULong? = null): LnUrlInvoice? = withContext(Dispatchers.IO) {
        val result = apiService.requestLnUrlInvoice(url, amount)
        return@withContext result.body()
    }

}