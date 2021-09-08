package com.samruddhi.mvvmarchitecturelivedataretrofitpaging.webservice

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.*

class AuthenticationInterceptor(private var apiKey: String) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        interceptRequest(builder)
        return chain.proceed(builder.build())
    }

    private fun interceptRequest(builder: Request.Builder) {
        builder.header("user-key", apiKey)
        builder.header("Accept-Language", Locale.getDefault().toLanguageTag())
    }
}