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
import com.hydrogen.hydrogenpaymentsdk.domain.models.OtpValidationResponseDomain
import com.hydrogen.hydrogenpaymentsdk.domain.models.PayByCardResponseDomain
import com.hydrogen.hydrogenpaymentsdk.domain.models.PaymentConfirmationResponse
import com.hydrogen.hydrogenpaymentsdk.domain.models.PaymentMethod
import com.hydrogen.hydrogenpaymentsdk.domain.models.ResendOTPResponseDataDomain
import com.hydrogen.hydrogenpaymentsdk.domain.models.TransactionDetails
import com.hydrogen.hydrogenpaymentsdk.domain.models.TransactionStatus
import com.hydrogen.hydrogenpaymentsdk.domain.usecases.GetBankTransactionStatusUseCase
import com.hydrogen.hydrogenpaymentsdk.domain.usecases.InitiatePaymentUseCase
import com.hydrogen.hydrogenpaymentsdk.domain.usecases.PayByTransferUseCase
import com.hydrogen.hydrogenpaymentsdk.domain.usecases.countdownTimer.CountdownTimerUseCase
import com.hydrogen.hydrogenpaymentsdk.domain.usecases.payByCard.GetCardProviderUseCase
import com.hydrogen.hydrogenpaymentsdk.domain.usecases.payByCard.PayByCardTransactionStatusUseCase
import com.hydrogen.hydrogenpaymentsdk.domain.usecases.payByCard.PayByCardUseCase
import com.hydrogen.hydrogenpaymentsdk.domain.usecases.payByCard.ResendOtpUseCase
import com.hydrogen.hydrogenpaymentsdk.domain.usecases.payByCard.ValidateOtpUseCase
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.UIEvent
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.ViewState
import com.hydrogen.hydrogenpaymentsdk.utils.AppConstants.INT_MAX_OTP_TRY_COUNT
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
    private val initiatePaymentUseCase: InitiatePaymentUseCase,
    private val ioDispatcher: CoroutineDispatcher,
    private val countdownTimerUseCase: CountdownTimerUseCase,
    private val getBankTransferStatusUseCase: GetBankTransactionStatusUseCase,
    private val getCardProviderUseCase: GetCardProviderUseCase,
    private val payByCardUseCase: PayByCardUseCase,
    private val validateOtpUseCase: ValidateOtpUseCase,
    private val resendOtpUseCase: ResendOtpUseCase,
    private val payByCardTransactionStatusUseCase: PayByCardTransactionStatusUseCase
) : ViewModel() {
    private val _otpCodeTryCount: MutableLiveData<String> = MutableLiveData("0")
    val otpCodeTryCount: LiveData<String> get() = _otpCodeTryCount
    private val _validateOtpCode: MutableLiveData<ViewState<UIEvent<OtpValidationResponseDomain?>>> =
        MutableLiveData(ViewState.initialDefault(null))
    val validateOtpCode: LiveData<ViewState<UIEvent<OtpValidationResponseDomain?>>> get() = _validateOtpCode
    private val _resendOtpCode: MutableLiveData<ViewState<UIEvent<ResendOTPResponseDataDomain?>>> =
        MutableLiveData(ViewState.initialDefault(null))
    val resendOtpCode: LiveData<ViewState<UIEvent<ResendOTPResponseDataDomain?>>> get() = _resendOtpCode
    private val _cardPaymentResponse: MutableLiveData<ViewState<PayByCardResponseDomain?>> =
        MutableLiveData(ViewState.initialDefault(null))
    val cardPaymentResponse: LiveData<ViewState<PayByCardResponseDomain?>> get() = _cardPaymentResponse
    private val _cardProvider: MutableLiveData<ViewState<UIEvent<CardProviderResponse>?>> =
        MutableLiveData((ViewState.initialDefault(null)))
    val cardProvider: LiveData<ViewState<UIEvent<CardProviderResponse>?>> get() = _cardProvider

    private val _transactionStatus: MutableLiveData<ViewState<TransactionStatus?>> =
        MutableLiveData(ViewState.initialDefault(null))
    val transactionStatus: LiveData<ViewState<TransactionStatus?>> get() = _transactionStatus


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

    fun getBankTransferStatus() {
        _transactionStatus.value = ViewState.loading(null)
        viewModelScope.launch(ioDispatcher) {
            val transactionDetails = _paymentMethodsAndTransactionDetails.value.content!!.second!!
            val initiatePaymentRequest = _bankTransferRequest.value!!
            getBankTransferStatusUseCase.invoke(
                transactionDetails.transactionId,
                transactionDetails,
                initiatePaymentRequest
            )
                .collectLatest {
                    _transactionStatus.postValue(it)
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

    fun validateOtp(otp: String) {
        _validateOtpCode.value = ViewState.loading(null)
        incrementOtpTrialCount()
        viewModelScope.launch(ioDispatcher) {
            validateOtpUseCase.invoke(otp, cardProvider.value!!.content!!.peekContent().providerId)
                .collect {
                    val result = UIEvent(it.content)
                    _validateOtpCode.postValue(ViewState(it.status, result, it.message))
                }
        }
    }

    fun resendOtp() {
        _resendOtpCode.value = ViewState.loading(null)
        viewModelScope.launch(ioDispatcher) {
            val providerId = cardProvider.value!!.content!!.peekContent().providerId
            val currency =
                paymentMethodsAndTransactionDetails.value.content!!.second!!.currencyInfo.alpha2Code
            resendOtpUseCase.invoke(providerId, currency)
                .collect {
                    val result = UIEvent(it.content)
                    _resendOtpCode.postValue(ViewState(it.status, result, it.message))
                }
        }
    }

    fun getPayByCardTransactionStatus(){
        _transactionStatus.value = ViewState.loading(null)
        viewModelScope.launch(ioDispatcher) {
            val transactionDetails = _paymentMethodsAndTransactionDetails.value.content!!.second!!
            val initiatePaymentRequest = _bankTransferRequest.value!!
            payByCardTransactionStatusUseCase.invoke(
                transactionDetails.transactionId,
                transactionDetails,
                initiatePaymentRequest
            )
                .collectLatest {
                    _transactionStatus.postValue(it)
                }
        }
    }

    fun getReceiptPayload(paymentType: String): HydrogenPayPaymentTransactionReceipt =
        transactionStatus.value!!.content!!.getReceiptPayload(paymentType)

    private fun incrementOtpTrialCount() {
        if (_otpCodeTryCount.value!!.toInt() < INT_MAX_OTP_TRY_COUNT) {
            _otpCodeTryCount.postValue(((_otpCodeTryCount.value!!.toInt() + 1).toString()))
        }
    }

    override fun onCleared() {
        super.onCleared()
        countdownTimerUseCase.reset()
    }
}