package com.walletka.app.usecases

import android.util.Log
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import com.walletka.app.io.client.NostrClient
import javax.inject.Inject

class GetNpubUseCase @Inject constructor(
    private val nostrClient: NostrClient
) {

    operator fun invoke(): Option<String> {
        return try {
            Some(nostrClient.getNpub())
        } catch (e: Exception) {
            Log.e("GetNpubUC", e.localizedMessage)
            None
        }
    }

}