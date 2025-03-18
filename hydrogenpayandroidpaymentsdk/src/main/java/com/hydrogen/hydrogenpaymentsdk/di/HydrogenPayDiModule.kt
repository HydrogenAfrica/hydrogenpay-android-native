package com.hydrogen.hydrogenpaymentsdk.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hydrogen.hydrogenpaymentsdk.data.local.sharedPrefs.SessionManager
import com.hydrogen.hydrogenpaymentsdk.data.local.sharedPrefs.SessionManagerContract
import com.hydrogen.hydrogenpaymentsdk.data.local.sharedPrefs.SharedPrefManager
import com.hydrogen.hydrogenpaymentsdk.data.local.sharedPrefs.SharedPrefsManagerContract
import com.hydrogen.hydrogenpaymentsdk.data.remote.apis.BankTransferApiService
import com.hydrogen.hydrogenpaymentsdk.data.remote.apis.HydrogenPaymentGateWayApiService
import com.hydrogen.hydrogenpaymentsdk.data.remote.apis.PayByCardApiService
import com.hydrogen.hydrogenpaymentsdk.data.remote.interceptors.AuthInterceptor
import com.hydrogen.hydrogenpaymentsdk.data.repositoryImpl.BankTransferRepositoryImpl
import com.hydrogen.hydrogenpaymentsdk.data.repositoryImpl.PayByCardRepositoryImpl
import com.hydrogen.hydrogenpaymentsdk.data.repositoryImpl.RepositoryImpl
import com.hydrogen.hydrogenpaymentsdk.domain.repository.BankTransferRepository
import com.hydrogen.hydrogenpaymentsdk.domain.repository.DataEncryptionContract
import com.hydrogen.hydrogenpaymentsdk.domain.repository.PayByCardRepository
import com.hydrogen.hydrogenpaymentsdk.domain.repository.Repository
import com.hydrogen.hydrogenpaymentsdk.domain.usecases.GetBankTransactionStatusUseCase
import com.hydrogen.hydrogenpaymentsdk.domain.usecases.InitiatePaymentUseCase
import com.hydrogen.hydrogenpaymentsdk.domain.usecases.PayByTransferUseCase
import com.hydrogen.hydrogenpaymentsdk.domain.usecases.SetUpUseCase
import com.hydrogen.hydrogenpaymentsdk.domain.usecases.countdownTimer.CountdownTimerUseCase
import com.hydrogen.hydrogenpaymentsdk.domain.usecases.payByCard.DeleteSavedCardUseCase
import com.hydrogen.hydrogenpaymentsdk.domain.usecases.payByCard.GetCardProviderUseCase
import com.hydrogen.hydrogenpaymentsdk.domain.usecases.payByCard.GetSavedCardsUseCase
import com.hydrogen.hydrogenpaymentsdk.domain.usecases.payByCard.PayByCardTransactionStatusUseCase
import com.hydrogen.hydrogenpaymentsdk.domain.usecases.payByCard.PayByCardUseCase
import com.hydrogen.hydrogenpaymentsdk.domain.usecases.payByCard.ResendOtpUseCase
import com.hydrogen.hydrogenpaymentsdk.domain.usecases.payByCard.SelectASavedCardUseCase
import com.hydrogen.hydrogenpaymentsdk.domain.usecases.payByCard.ValidateOtpUseCase
import com.hydrogen.hydrogenpaymentsdk.utils.AppConstants.BASE_URL
import com.hydrogen.hydrogenpaymentsdk.utils.AppConstants.ENC_IV
import com.hydrogen.hydrogenpaymentsdk.utils.AppConstants.ENC_KEY
import com.hydrogen.hydrogenpaymentsdk.utils.DataEncryption
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

    private fun providesSessionManagerContract(): SessionManagerContract = providesSessionManager()

    private fun providesAuthInterceptor(): Interceptor =
        AuthInterceptor(providesSessionManagerContract())

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
            .addConverterFactory(GsonConverterFactory.create(providesGson()))
            .build()
    }

    private fun providesSharedPrefManager(
    ): SharedPrefManager = SharedPrefManager()

    private fun providesSessionManager(): SessionManager =
        SessionManager(providesSharedPrefs(), providesGson())

    private fun providesApiService(): HydrogenPaymentGateWayApiService =
        providesRetrofit().create(HydrogenPaymentGateWayApiService::class.java)

    private fun providesBankTransferApiService(): BankTransferApiService =
        providesRetrofit().create(BankTransferApiService::class.java)

    fun providesInitiatePaymentUseCase(): InitiatePaymentUseCase =
        InitiatePaymentUseCase(providesRepository(), providesNetworkUtil())

    fun providesGson(): Gson = GsonBuilder()
        .disableHtmlEscaping() // Prevents `=` from becoming `\u003d`
        .create()

    private fun providesNetworkUtil(): NetworkUtil = NetworkUtil()

    fun providesIoDispatchers(): CoroutineDispatcher = Dispatchers.IO
    fun providesCountdownTimerUseCase(): CountdownTimerUseCase = CountdownTimerUseCase()

    private fun providesSharedPrefs(): SharedPrefsManagerContract = providesSharedPrefManager()

    private fun providesRepository(): Repository =
        RepositoryImpl(
            providesApiService(),
            providesNetworkUtil(),
            providesSessionManagerContract()
        )

    private fun providesBankTransferRepository(): BankTransferRepository =
        BankTransferRepositoryImpl(
            providesBankTransferApiService(),
            providesNetworkUtil()
        )

    private fun providesPayByCardApiService(): PayByCardApiService =
        providesRetrofit().create(PayByCardApiService::class.java)

    private fun providesDataEncryptionContract(): DataEncryptionContract =
        DataEncryption(ENC_KEY, ENC_IV)

    private fun providesPayByCardRepository(): PayByCardRepository = PayByCardRepositoryImpl(
        providesPayByCardApiService(),
        providesNetworkUtil(),
        providesSessionManagerContract(),
        providesDataEncryptionContract()
    )

    fun providesPayByTransferUseCase(): PayByTransferUseCase = PayByTransferUseCase(
        providesBankTransferRepository(),
        providesSessionManagerContract()
    )

    fun providesGetBankTransferStatusUseCase(): GetBankTransactionStatusUseCase =
        GetBankTransactionStatusUseCase(
            providesBankTransferRepository()
        )

    fun providesSetUpUseCase(): SetUpUseCase = SetUpUseCase(providesSessionManagerContract())

    fun providesGetCardProviderUseCase(): GetCardProviderUseCase =
        GetCardProviderUseCase(providesPayByCardRepository())

    fun providesPayByCardUseCase(): PayByCardUseCase =
        PayByCardUseCase(
            providesPayByCardRepository(),
            providesDataEncryptionContract(),
            providesGson()
        )

    fun providesValidateOtpUseCase(): ValidateOtpUseCase =
        ValidateOtpUseCase(providesPayByCardRepository())

    fun providesResendOtpUseCase(): ResendOtpUseCase =
        ResendOtpUseCase(providesPayByCardRepository())

    fun providesPayByCardTransactionStatusUseCase(): PayByCardTransactionStatusUseCase =
        PayByCardTransactionStatusUseCase(
            providesPayByCardRepository()
        )

    fun providesGetSavedCardsUseCase(): GetSavedCardsUseCase = GetSavedCardsUseCase(providesPayByCardRepository())

    fun providesSelectCardUseCase(): SelectASavedCardUseCase = SelectASavedCardUseCase()
    fun providesDeleteASavedCardUseCase(): DeleteSavedCardUseCase = DeleteSavedCardUseCase()
}