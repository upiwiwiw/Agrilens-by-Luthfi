package com.example.capstone.data.retrofit

import android.content.Context
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL = "https://agrilens-api-902985170579.asia-southeast2.run.app/"

    fun createClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .cache(Cache(context.cacheDir, 10L * 1024 * 1024)) // 10 MB cache
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(7, TimeUnit.SECONDS)
            .readTimeout(7, TimeUnit.SECONDS)
            .build()
    }

    private fun createRetrofit(baseUrl: String, client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getRegisterService(context: Context): ApiService {
        val client = createClient(context)
        val retrofit = createRetrofit(BASE_URL, client)
        return retrofit.create(ApiService::class.java)
    }

    fun getLoginService(context: Context): ApiService {
        val client = createClient(context)
        val retrofit = createRetrofit(BASE_URL, client)
        return retrofit.create(ApiService::class.java)
    }
}
