package com.walletka.app.wallet

import android.app.Application
import android.content.Context
import android.util.Log
import com.walletka.app.AppState
import com.walletka.app.di.MnemonicSeedProvider
import com.walletka.core.WalletkaBuilder
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class WalletkaCore @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mnemonicSeedProvider: MnemonicSeedProvider,
    private val appState: AppState,
) {


    suspend fun start() {
        val file = File(context.filesDir, appState.walletkaCorePath)
        if (!file.exists()) {
            file.mkdir()
        }

        val a = WalletkaBuilder()
        a.setLocalDbStore(file.absolutePath)
        a.setMnemonic(mnemonicSeedProvider.get()!!)
        a.setDataPath(file.absolutePath)
        a.setElectrumUrl(appState.electrumUrl)

        val walletka = a.build()
        walletka.sync()
        val balance = walletka.getBalance(null)
        val address = walletka.getBitcoinAddress()
        Log.i("CORE", "Balance $balance")
        Log.i("CORE", "Address: $address")
    }


}