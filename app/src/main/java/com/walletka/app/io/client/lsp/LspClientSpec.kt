package com.walletka.app.io.client.lsp

import com.walletka.app.io.client.model.LnUrlInvoice
import com.walletka.app.io.client.model.LspSignUpRequest
import com.walletka.app.io.client.model.LspSignUpResponse
import com.walletka.app.io.client.model.RequestChannelRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface LspClientSpec {
    @POST("signup")
    suspend fun signUp(@Body body: LspSignUpRequest): Response<LspSignUpResponse>

    @GET("user/{alias}")
    suspend fun getUser(@Path("alias") alias: String): Response<ResponseBody>

    @POST("channel/request")
    suspend fun requestChannel(@Body body: RequestChannelRequest): Response<ResponseBody>

    @GET
    suspend fun requestLnUrlInvoice(
        @Url url: String,
        @Query("amount") amount: ULong? = null
    ): Response<LnUrlInvoice>
}