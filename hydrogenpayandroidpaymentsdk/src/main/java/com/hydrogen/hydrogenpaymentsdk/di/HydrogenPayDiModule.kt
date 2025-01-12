package com.hydrogen.hydrogenpaymentsdk.di

import com.google.gson.Gson
import com.hydrogen.hydrogenpaymentsdk.data.local.sharedPrefs.SharedPrefManager
import com.hydrogen.hydrogenpaymentsdk.data.local.sharedPrefs.SharedPrefsManagerContract
import com.hydrogen.hydrogenpaymentsdk.data.remote.apis.HydrogenPaymentGateWayApiService
import com.hydrogen.hydrogenpaymentsdk.data.remote.interceptors.AuthInterceptor
import com.hydrogen.hydrogenpaymentsdk.domain.repository.Repository
import com.hydrogen.hydrogenpaymentsdk.domain.repository.RepositoryImpl
import com.hydrogen.hydrogenpaymentsdk.usecases.countdownTimer.CountdownTimerUseCase
import com.hydrogen.hydrogenpaymentsdk.usecases.payByTransfer.PayByTransferUseCase
import com.hydrogen.hydrogenpaymentsdk.usecases.payByTransfer.PayByTransferUseCaseImpl
import com.hydrogen.hydrogenpaymentsdk.usecases.paymentConfirmation.PaymentConfirmationUseCase
import com.hydrogen.hydrogenpaymentsdk.usecases.paymentConfirmation.PaymentConfirmationUseCaseImpl
import com.hydrogen.hydrogenpaymentsdk.utils.AppConstants.BASE_URL
import com.hydrogen.hydrogenpaymentsdk.utils.NetworkUtil
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

internal object HydrogenPayDiModule {
    private fun providesBaseUrl(): String = BASE_URL
    private fun providesLoggingInterceptor(): Interceptor = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }
    private fun providesAuthInterceptor(): Interceptor = AuthInterceptor()

    private fun providesOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .callTimeout(20, TimeUnit.SECONDS)
        .addInterceptor(providesAuthInterceptor())
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

    private fun providesApiService(): HydrogenPaymentGateWayApiService =
        providesRetrofit().create(HydrogenPaymentGateWayApiService::class.java)

    fun providesGson(): Gson = Gson()

    private fun providesNetworkUtil(): NetworkUtil = NetworkUtil()

    fun providesIoDispatchers(): CoroutineDispatcher = Dispatchers.IO
    fun providesCountdownTimerUseCase(): CountdownTimerUseCase = CountdownTimerUseCase()

    fun providesSharedPrefs(): SharedPrefsManagerContract = providesSharedPrefManager()

    private fun providesRepository(): Repository = RepositoryImpl(providesApiService(), providesNetworkUtil())
    fun providesPayByTransferUseCase(): PayByTransferUseCase = PayByTransferUseCaseImpl(
        providesRepository()
    )

    fun providesPaymentConfirmationUseCase(): PaymentConfirmationUseCase = PaymentConfirmationUseCaseImpl(
        providesRepository()
    )
}