package com.samruddhi.shimmerviewloadding.webservice.api

import android.content.Context
import com.samruddhi.shimmerviewloadding.R
import com.samruddhi.shimmerviewloadding.webservice.RetrofitClient

object WebApi {
    fun getAPIService(context: Context): WebServiceApi {
        return RetrofitClient.getClient(context.getString(R.string.app_url), context.getString(R.string.api_key))!!
            .create(
                WebServiceApi::class.java
            )
    }
}