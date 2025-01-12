package com.hydrogen.hydrogenpaymentsdk.presentation.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.PayByTransferRequest
import com.hydrogen.hydrogenpaymentsdk.di.HydrogenPayDiModule.providesIoDispatchers
import com.hydrogen.hydrogenpaymentsdk.di.HydrogenPayDiModule.providesPayByTransferUseCase
import com.hydrogen.hydrogenpaymentsdk.di.HydrogenPayDiModule.providesPaymentConfirmationUseCase
import com.hydrogen.hydrogenpaymentsdk.domain.models.PayByTransferResponse
import com.hydrogen.hydrogenpaymentsdk.domain.models.PaymentConfirmationResponse
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.ViewState
import com.hydrogen.hydrogenpaymentsdk.usecases.countdownTimer.CountdownTimerUseCase
import com.hydrogen.hydrogenpaymentsdk.usecases.payByTransfer.PayByTransferUseCase
import com.hydrogen.hydrogenpaymentsdk.usecases.paymentConfirmation.PaymentConfirmationUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppViewModel (
    private val payByTransferUseCase: PayByTransferUseCase,
    private val paymentConfirmationUseCase: PaymentConfirmationUseCase,
    private val ioDispatcher: CoroutineDispatcher,
    private val countdownTimerUseCase: CountdownTimerUseCase
) : ViewModel() {
    private val _timeLeft: MutableStateFlow<String> = MutableStateFlow("")
    val timeLeft: StateFlow<String> = _timeLeft.asStateFlow()

    private val _bankTransferRequest: MutableLiveData<PayByTransferRequest> = MutableLiveData()
    val bankTransferRequest get() = _bankTransferRequest

    private val _bankTransferResponseState: MutableLiveData<ViewState<PayByTransferResponse?>> =
        MutableLiveData(ViewState.initialDefault(null))
    val bankTransferResponseState: LiveData<ViewState<PayByTransferResponse?>> get() = _bankTransferResponseState

    private val _paymentConfirmation =
        MutableLiveData<ViewState<PaymentConfirmationResponse?>>(ViewState.initialDefault(null))
    val paymentConfirmation get() = _paymentConfirmation

    init {
        observeTimer()
    }

    private fun observeTimer() {
        viewModelScope.launch {
            countdownTimerUseCase.timeLeft.collect { remainingTime ->
                val minutes = remainingTime / 60
                val seconds = remainingTime % 60
                _timeLeft.value = "$minutes:$seconds"
            }
        }
    }

    fun startTimer(minutes: Int) = countdownTimerUseCase.start(minutes)
    fun pauseTimer() = countdownTimerUseCase.pause()
    fun resumeTimer() = countdownTimerUseCase.resume()
    fun resetTimer() = countdownTimerUseCase.reset()

    fun payByTransfer() {
        _bankTransferResponseState.value = ViewState.loading(null)
        viewModelScope.launch(ioDispatcher) {
            payByTransferUseCase.payByTransfer(bankTransferRequest.value!!)
                .collectLatest {
                    _bankTransferResponseState.postValue(it)
                }
        }
    }

    fun confirmPayment() {
        _paymentConfirmation.value = ViewState.loading(null)
        viewModelScope.launch(ioDispatcher) {
            paymentConfirmationUseCase.confirmPayment(_bankTransferResponseState.value!!.content!!.transactionRef)
                .collectLatest {
                    _paymentConfirmation.value = it
                }
        }
    }

    fun setBankTransferRequest(bankTransferRequest: PayByTransferRequest) {
        _bankTransferRequest.value = bankTransferRequest
    }
}