package com.hydrogen.hydrogenpaymentsdk.di

import com.hydrogen.hydrogenpaymentsdk.data.local.sharedPrefs.SharedPrefManager
import com.hydrogen.hydrogenpaymentsdk.data.local.sharedPrefs.SharedPrefsManagerContract
import com.hydrogen.hydrogenpaymentsdk.data.remote.apis.HydrogenPaymentGateWayApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object HydrogenPayDiModule {
    private fun providesBaseUrl(): String = "https://api.hydrogenpay.com/bepay/api/v1/Merchant/"
    private fun providesLoggingInterceptor(): Interceptor = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    private fun providesOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .callTimeout(20, TimeUnit.SECONDS)
        .addInterceptor(providesLoggingInterceptor())
        .build()

    private fun providesRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(providesBaseUrl())
            .client(providesOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun providesSharedPrefManager(
    ): SharedPrefManager = SharedPrefManager()

    fun providesApiService(): HydrogenPaymentGateWayApiService =
        providesRetrofit().create(HydrogenPaymentGateWayApiService::class.java)

    fun providesSharedPrefs(): SharedPrefsManagerContract = providesSharedPrefManager()
}