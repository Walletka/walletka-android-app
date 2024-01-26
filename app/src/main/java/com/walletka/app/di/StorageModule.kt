package com.walletka.app.di

import android.content.Context
import android.content.SharedPreferences
import com.walletka.app.io.database.WalletkaDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SharedPreferencesModule {

    @Singleton
    @Provides
    fun provideSharedPreference(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("walletka", Context.MODE_PRIVATE)
    }

    @Provides
    fun provideWalletkaDatabase(@ApplicationContext context: Context): WalletkaDatabase {
        return WalletkaDatabase.getDatabase(context)
    }
}

class MnemonicSeedProvider @Inject constructor(val sharedPreferences: SharedPreferences) {
    fun get(): String? {
        return sharedPreferences.getString("mnemonic_seed", null) // TODO: Use android keystore
    }
}