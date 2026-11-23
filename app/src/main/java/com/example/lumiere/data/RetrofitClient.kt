package com.example.lumiere.data

import com.example.lumiere.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val url = chain.request().url.newBuilder()
                .addQueryParameter("api_key", BuildConfig.TMDB_API_KEY)
                .build()
            chain.proceed(chain.request().newBuilder().url(url).build())
        }
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    val api: TmdbApi = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(TmdbApi::class.java)
}