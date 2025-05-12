package com.sashiomarda.wifimapping.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val logging = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val instance: UploadApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://wifimapping.biz.id/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UploadApi::class.java)
    }
}
