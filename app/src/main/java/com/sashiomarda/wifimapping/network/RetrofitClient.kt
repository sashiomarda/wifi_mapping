package com.sashiomarda.wifimapping.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private val logging = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(60, TimeUnit.SECONDS)  // waktu tunggu koneksi
        .readTimeout(300, TimeUnit.SECONDS)    // waktu tunggu membaca respon
        .writeTimeout(120, TimeUnit.SECONDS)   // waktu tunggu mengirim data
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
