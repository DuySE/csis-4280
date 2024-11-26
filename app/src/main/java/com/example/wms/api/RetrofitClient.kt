package com.example.wms.api

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.BufferedReader
import java.io.InputStreamReader

object RetrofitClient {

    private fun getBaseUrl(context: Context): String {
        val inputStream = context.assets.open("base_url.txt")
        val reader = BufferedReader(InputStreamReader(inputStream))
        return reader.readLine()
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    // Pass context from MainActivity to load base URL from assets
    fun getInstance(context: Context): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(getBaseUrl(context))
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }
}
