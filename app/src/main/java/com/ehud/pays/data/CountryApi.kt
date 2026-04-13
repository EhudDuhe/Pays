package com.ehud.pays.data

import com.ehud.pays.model.Country
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface CountryApi {
    @GET("v3.1/all?fields=name,flags,capital,population,continents")
    suspend fun getCountries(): List<Country>

    @GET("v3.1/region/{region}?fields=name,flags,capital,population,continents")
    suspend fun getCountriesByRegion(@Path("region") region: String): List<Country>

    companion object {
        fun create(): CountryApi {
            val logger = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .header("User-Agent", "PaysApp")
                        .header("Accept", "application/json")
                        .build()
                    chain.proceed(request)
                }
                .build()

            return Retrofit.Builder()
                .baseUrl("https://restcountries.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CountryApi::class.java)
        }
    }
}
