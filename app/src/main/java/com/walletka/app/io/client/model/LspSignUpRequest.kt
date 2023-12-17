package com.walletka.app.io.client.model

data class LspSignUpRequest (
    val node_id: String?,
    val nostr_pubkey: String,
)

data class LspSignUpResponse(
    val alias: String
)