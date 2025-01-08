package com.hydrogen.hydrogenpaymentsdk.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.PayByTransferRequestBody
import com.hydrogen.hydrogenpaymentsdk.di.HydrogenPayDiModule.providesIoDispatchers
import com.hydrogen.hydrogenpaymentsdk.di.HydrogenPayDiModule.providesPayByTransferUseCase
import com.hydrogen.hydrogenpaymentsdk.di.HydrogenPayDiModule.providesPaymentConfirmationUseCase
import com.hydrogen.hydrogenpaymentsdk.domain.models.PayByTransferResponse
import com.hydrogen.hydrogenpaymentsdk.domain.models.PaymentConfirmationResponse
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.ViewState
import com.hydrogen.hydrogenpaymentsdk.usecases.payByTransfer.PayByTransferUseCase
import com.hydrogen.hydrogenpaymentsdk.usecases.paymentConfirmation.PaymentConfirmationUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppViewModel : ViewModel() {
    private val payByTransferUseCase: PayByTransferUseCase = providesPayByTransferUseCase()
    private val paymentConfirmationUseCase: PaymentConfirmationUseCase =
        providesPaymentConfirmationUseCase()
    private val ioDispatcher: CoroutineDispatcher = providesIoDispatchers()

    private val _bankTransferResponseState =
        MutableStateFlow<ViewState<PayByTransferResponse?>>(ViewState.initialDefault(null))
    val bankTransferResponseState = _bankTransferResponseState.asStateFlow()

    private val _paymentConfirmation =
        MutableStateFlow<ViewState<PaymentConfirmationResponse?>>(ViewState.initialDefault(null))
    val paymentConfirmation = _paymentConfirmation.asStateFlow()

    fun payByTransfer(payByTransferRequestBody: PayByTransferRequestBody) {
        _bankTransferResponseState.update { ViewState.loading(null) }
        viewModelScope.launch(ioDispatcher) {
            payByTransferUseCase.payByTransfer(payByTransferRequestBody)
                .collectLatest {
                    _bankTransferResponseState.update { it }
                }
        }
    }

    fun confirmPayment(transactionRef: String) {
        _paymentConfirmation.update { ViewState.loading(null) }
        viewModelScope.launch(ioDispatcher) {
            paymentConfirmationUseCase.confirmPayment(transactionRef)
                .collectLatest {
                    _paymentConfirmation.update { it }
                }
        }
    }
}