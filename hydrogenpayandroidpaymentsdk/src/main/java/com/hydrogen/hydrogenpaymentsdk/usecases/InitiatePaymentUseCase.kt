package com.hydrogen.hydrogenpaymentsdk.usecases

import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.PayByTransferRequest
import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.TransactionDetailsRequest
import com.hydrogen.hydrogenpaymentsdk.domain.models.PaymentMethod
import com.hydrogen.hydrogenpaymentsdk.domain.models.TransactionDetails
import com.hydrogen.hydrogenpaymentsdk.domain.repository.Repository
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.ViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map

class InitiatePaymentUseCase(
    private val repository: Repository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(initiatePaymentRequest: PayByTransferRequest): Flow<ViewState<Pair<List<PaymentMethod>?, TransactionDetails?>>> =
        repository.initiatePayment(initiatePaymentRequest)
            .flatMapConcat {
                repository.getPaymentMethod(it.content!!.transactionId)
                    .flatMapConcat { paymentMethod ->
                        val transactionDetailsRequest =
                            TransactionDetailsRequest(it.content.transactionId)
                        repository.getTransactionDetails(transactionDetailsRequest)
                            .map { transDetails ->
                                ViewState(
                                    transDetails.status,
                                    Pair(paymentMethod.content, transDetails.content),
                                    transDetails.message
                                )
                            }
                    }
            }
}