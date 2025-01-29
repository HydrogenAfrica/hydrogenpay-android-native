package com.hydrogen.hydrogenpaymentsdk.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hydrogen.hydrogenpaymentsdk.presentation.viewModels.AppViewModel

internal class AppViewModelProviderFactory(
    private val hydrogenPayDiModule: HydrogenPayDiModule
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            return AppViewModel(
                hydrogenPayDiModule.providesPayByTransferUseCase(),
                hydrogenPayDiModule.providesPaymentConfirmationUseCase(),
                hydrogenPayDiModule.providesInitiatePaymentUseCase(),
                hydrogenPayDiModule.providesIoDispatchers(),
                hydrogenPayDiModule.providesCountdownTimerUseCase(),
                hydrogenPayDiModule.providesGetBankTransferStatusUseCase()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}