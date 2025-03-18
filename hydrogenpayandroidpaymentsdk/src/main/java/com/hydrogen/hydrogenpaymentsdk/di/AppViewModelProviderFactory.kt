package com.hydrogen.hydrogenpaymentsdk.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hydrogen.hydrogenpaymentsdk.presentation.viewModels.AppViewModel
import com.hydrogen.hydrogenpaymentsdk.presentation.viewModels.SetUpViewModel

internal class AppViewModelProviderFactory(
    private val hydrogenPayDiModule: HydrogenPayDiModule
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            return AppViewModel(
                hydrogenPayDiModule.providesPayByTransferUseCase(),
                hydrogenPayDiModule.providesInitiatePaymentUseCase(),
                hydrogenPayDiModule.providesIoDispatchers(),
                hydrogenPayDiModule.providesCountdownTimerUseCase(),
                hydrogenPayDiModule.providesGetBankTransferStatusUseCase(),
                hydrogenPayDiModule.providesGetCardProviderUseCase(),
                hydrogenPayDiModule.providesPayByCardUseCase(),
                hydrogenPayDiModule.providesValidateOtpUseCase(),
                hydrogenPayDiModule.providesResendOtpUseCase(),
                hydrogenPayDiModule.providesPayByCardTransactionStatusUseCase(),
                hydrogenPayDiModule.providesGetSavedCardsUseCase(),
                hydrogenPayDiModule.providesSelectCardUseCase(),
                hydrogenPayDiModule.providesDeleteASavedCardUseCase()
            ) as T
        }
        else if (modelClass.isAssignableFrom(SetUpViewModel::class.java)) {
            return SetUpViewModel(hydrogenPayDiModule.providesSetUpUseCase()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}