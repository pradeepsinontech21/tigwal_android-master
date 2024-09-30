package com.tigwal.data.api

import com.tigwal.app.rest.RestConstant
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitClient {

    companion object {
        private const val TIME = 5
        private val retrofitClient: Retrofit.Builder by lazy {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .connectTimeout(TIME.toLong(), TimeUnit.MINUTES)
                .readTimeout(TIME.toLong(), TimeUnit.MINUTES)
                .writeTimeout(TIME.toLong(), TimeUnit.MINUTES)
                .addInterceptor(logging)
                .build()

            Retrofit.Builder()
                .baseUrl(RestConstant.BASE_URLS)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)

        }

        val apiInterface: ApiService by lazy {
            retrofitClient
                .build()
                .create(ApiService::class.java)
        }
    }
}
