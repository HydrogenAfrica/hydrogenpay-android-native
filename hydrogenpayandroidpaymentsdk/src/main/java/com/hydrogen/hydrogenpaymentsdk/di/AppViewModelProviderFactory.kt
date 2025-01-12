package com.hydrogen.hydrogenpaymentsdk.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hydrogen.hydrogenpaymentsdk.di.HydrogenPayDiModule.providesCountdownTimerUseCase
import com.hydrogen.hydrogenpaymentsdk.di.HydrogenPayDiModule.providesIoDispatchers
import com.hydrogen.hydrogenpaymentsdk.di.HydrogenPayDiModule.providesPayByTransferUseCase
import com.hydrogen.hydrogenpaymentsdk.di.HydrogenPayDiModule.providesPaymentConfirmationUseCase
import com.hydrogen.hydrogenpaymentsdk.presentation.viewModels.AppViewModel

internal class AppViewModelProviderFactory(
): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            return AppViewModel(
                providesPayByTransferUseCase(),
                providesPaymentConfirmationUseCase(),
                providesIoDispatchers(),
                providesCountdownTimerUseCase()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}