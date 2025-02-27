package com.hydrogen.hydrogenpaymentsdk.presentation.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.models.DeviceInformation
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.requests.PayByTransferRequest
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses.CardProviderResponse
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses.InitiatePayByTransferResponseDTO
import com.hydrogen.hydrogenpaymentsdk.domain.models.HydrogenPayPaymentTransactionReceipt
import com.hydrogen.hydrogenpaymentsdk.domain.models.PayByCardResponseDomain
import com.hydrogen.hydrogenpaymentsdk.domain.models.PaymentConfirmationResponse
import com.hydrogen.hydrogenpaymentsdk.domain.models.PaymentMethod
import com.hydrogen.hydrogenpaymentsdk.domain.models.TransactionDetails
import com.hydrogen.hydrogenpaymentsdk.domain.models.TransactionStatus
import com.hydrogen.hydrogenpaymentsdk.domain.usecases.GetBankTransactionStatusUseCase
import com.hydrogen.hydrogenpaymentsdk.domain.usecases.InitiatePaymentUseCase
import com.hydrogen.hydrogenpaymentsdk.domain.usecases.PayByTransferUseCase
import com.hydrogen.hydrogenpaymentsdk.domain.usecases.PaymentConfirmationUseCase
import com.hydrogen.hydrogenpaymentsdk.domain.usecases.countdownTimer.CountdownTimerUseCase
import com.hydrogen.hydrogenpaymentsdk.domain.usecases.payByCard.GetCardProviderUseCase
import com.hydrogen.hydrogenpaymentsdk.domain.usecases.payByCard.PayByCardUseCase
import com.hydrogen.hydrogenpaymentsdk.domain.usecases.payByCard.ValidateOtpUseCase
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.UIEvent
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.ViewState
import com.hydrogen.hydrogenpaymentsdk.utils.AppConstants.LONG_TIME_15_SEC
import com.hydrogen.hydrogenpaymentsdk.utils.ModelMapper.getReceiptPayload
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class AppViewModel(
    private val payByTransferUseCase: PayByTransferUseCase,
    private val paymentConfirmationUseCase: PaymentConfirmationUseCase,
    private val initiatePaymentUseCase: InitiatePaymentUseCase,
    private val ioDispatcher: CoroutineDispatcher,
    private val countdownTimerUseCase: CountdownTimerUseCase,
    private val getBankTransferStatusUseCase: GetBankTransactionStatusUseCase,
    private val getCardProviderUseCase: GetCardProviderUseCase,
    private val payByCardUseCase: PayByCardUseCase,
    private val validateOtpUseCase: ValidateOtpUseCase
) : ViewModel() {
    private val _cardPaymentResponse: MutableLiveData<ViewState<PayByCardResponseDomain?>> =
        MutableLiveData(ViewState.initialDefault(null))
    val cardPaymentResponse: LiveData<ViewState<PayByCardResponseDomain?>> get() = _cardPaymentResponse
    private val _cardProvider: MutableLiveData<ViewState<UIEvent<CardProviderResponse>?>> =
        MutableLiveData((ViewState.initialDefault(null)))
    val cardProvider: LiveData<ViewState<UIEvent<CardProviderResponse>?>> get() = _cardProvider

    private val _bankTransferPaymentStatus: MutableLiveData<ViewState<TransactionStatus?>> =
        MutableLiveData(ViewState.initialDefault(null))
    val bankTransferPaymentStatus: LiveData<ViewState<TransactionStatus?>> get() = _bankTransferPaymentStatus


    private val _bankTransferStatus: MutableLiveData<ViewState<PaymentConfirmationResponse?>> =
        MutableLiveData(ViewState.initialDefault(null))
    val bankTransferStatus: LiveData<ViewState<PaymentConfirmationResponse?>> get() = _bankTransferStatus

    private val _paymentMethodsAndTransactionDetails: MutableStateFlow<ViewState<Pair<List<PaymentMethod>?, TransactionDetails?>?>> =
        MutableStateFlow(ViewState.initialDefault(null))
    val paymentMethodsAndTransactionDetails: StateFlow<ViewState<Pair<List<PaymentMethod>?, TransactionDetails?>?>> =
        _paymentMethodsAndTransactionDetails.asStateFlow()

    private val _timeLeft: MutableStateFlow<String> = MutableStateFlow("")
    val timeLeft: StateFlow<String> = _timeLeft.asStateFlow()

    private val _timeLeftToRedirectToMerchantAppAfterSuccessfulPayment: MutableStateFlow<Long> =
        MutableStateFlow(-2L) // Set standby time to -2L
    val timeLeftToRedirectToMerchantAppAfterSuccessfulPayment: StateFlow<Long> =
        _timeLeftToRedirectToMerchantAppAfterSuccessfulPayment.asStateFlow()

    private val _bankTransferRequest: MutableLiveData<PayByTransferRequest> = MutableLiveData()
    val bankTransferRequest get() = _bankTransferRequest

    private val _bankTransferResponseState: MutableLiveData<ViewState<InitiatePayByTransferResponseDTO?>> =
        MutableLiveData(ViewState.initialDefault(null))
    val bankTransferResponseState: LiveData<ViewState<InitiatePayByTransferResponseDTO?>> get() = _bankTransferResponseState

    private val _paymentConfirmation =
        MutableLiveData<ViewState<PaymentConfirmationResponse?>>(ViewState.initialDefault(null))
    val paymentConfirmation get() = _paymentConfirmation

    init {
        observeTimer()
    }

    private fun observeTimer() {
        viewModelScope.launch {
            countdownTimerUseCase.timeLeft.collect { remainingTime ->
                val minutes = (remainingTime / 60).let { if (it < 10) "0$it" else "$it" }
                val seconds = (remainingTime % 60).let { if (it < 10) "0$it" else "$it" }
                _timeLeft.update { "$minutes:$seconds" }
            }
        }
    }

    fun startTimer(minutes: Long) = countdownTimerUseCase.start(minutes)
    fun pauseTimer() = countdownTimerUseCase.pause()
    fun resumeTimer() = countdownTimerUseCase.resume()
    fun resetTimer() = countdownTimerUseCase.reset()

    fun payByTransfer() {
        _bankTransferResponseState.value = ViewState.loading(null)
        viewModelScope.launch(ioDispatcher) {
            payByTransferUseCase.invoke()
                .collect {
                    _bankTransferResponseState.postValue(it)
                }
        }
    }

    fun confirmPayment() {
        _paymentConfirmation.value = ViewState.loading(null)
        viewModelScope.launch(ioDispatcher) {
            paymentConfirmationUseCase.invoke()
                .collectLatest {
                    _paymentConfirmation.postValue(it)
                }
        }
    }

    fun getBankTransferStatus() {
        _bankTransferPaymentStatus.value = ViewState.loading(null)
        viewModelScope.launch(ioDispatcher) {
            val transactionDetails = _paymentMethodsAndTransactionDetails.value.content!!.second!!
            val initiatePaymentRequest = _bankTransferRequest.value!!
            getBankTransferStatusUseCase.invoke(
                transactionDetails.transactionId,
                transactionDetails,
                initiatePaymentRequest
            )
                .collectLatest {
                    _bankTransferPaymentStatus.postValue(it)
                }
        }
    }

    fun setBankTransferRequest(bankTransferRequest: PayByTransferRequest) {
        _bankTransferRequest.value = bankTransferRequest
    }

    fun startRedirectTimer(redirectTime: Long = LONG_TIME_15_SEC) {
        viewModelScope.launch(Dispatchers.Default) {
            _timeLeftToRedirectToMerchantAppAfterSuccessfulPayment.update { redirectTime }
            while (_timeLeftToRedirectToMerchantAppAfterSuccessfulPayment.value >= 0) {
                delay(1000L)
                _timeLeftToRedirectToMerchantAppAfterSuccessfulPayment.update { (_timeLeftToRedirectToMerchantAppAfterSuccessfulPayment.value - 1) }
            }
        }
    }

    fun initiatePayment() {
        viewModelScope.launch(ioDispatcher) {
            initiatePaymentUseCase.invoke(_bankTransferRequest.value!!)
                .collect { result ->
                    _paymentMethodsAndTransactionDetails.update { result }
                }
        }
    }

    fun getCardProvider(cardNumber: String) {
        _cardProvider.value = ViewState.loading(null)
        viewModelScope.launch(ioDispatcher) {
            getCardProviderUseCase.invoke(cardNumber)
                .collect { result ->
                    val newContent = result.content?.let { UIEvent(result.content) }
                    _cardProvider.postValue(
                        ViewState(result.status, newContent, result.message)
                    )
                }
        }
    }

    fun resetCardProvider() {
        _cardProvider.value = ViewState.initialDefault(null)
    }

    fun payByCard(
        cardNumber: String,
        cardExpiration: String,
        cvv: String,
        isCardSaved: Boolean,
        pin: String,
        deviceInformation: DeviceInformation
    ) {
        _cardPaymentResponse.value = ViewState.loading(null)
        viewModelScope.launch(ioDispatcher) {
            val payByCardRequest = _bankTransferRequest.value!!
            val provider = cardProvider.value!!.content!!.peekContent()
            payByCardUseCase.invoke(
                payByCardRequest.email,
                provider.providerId,
                cardNumber,
                cardExpiration,
                cvv,
                isCardSaved,
                pin,
                deviceInformation,
                _paymentMethodsAndTransactionDetails.value.content!!.second!!.currencyInfo
            ).collectLatest {
                _cardPaymentResponse.postValue(it)
            }
        }
    }

    fun getReceiptPayload(): HydrogenPayPaymentTransactionReceipt =
        _paymentConfirmation.value!!.content!!.getReceiptPayload(
            _bankTransferResponseState.value!!.content!!,
            _bankTransferRequest.value!!.customerName
        )

    override fun onCleared() {
        super.onCleared()
        countdownTimerUseCase.reset()
    }
}