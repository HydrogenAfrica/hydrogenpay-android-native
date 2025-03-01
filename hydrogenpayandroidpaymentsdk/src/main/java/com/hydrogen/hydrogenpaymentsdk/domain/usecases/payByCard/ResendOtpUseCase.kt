package com.hydrogen.hydrogenpaymentsdk.domain.usecases.payByCard

import com.hydrogen.hydrogenpaymentsdk.domain.models.ResendOTPResponseDataDomain
import com.hydrogen.hydrogenpaymentsdk.domain.repository.PayByCardRepository
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.ViewState
import com.hydrogen.hydrogenpaymentsdk.utils.AppConstants.STRING_DEFAULT_CURRENCY_NGN
import com.hydrogen.hydrogenpaymentsdk.utils.ModelMapper.toDomain
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf

internal class ResendOtpUseCase(
    private val payByCardRepository: PayByCardRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(
        providerId: String,
        currency: String = STRING_DEFAULT_CURRENCY_NGN
    ): Flow<ViewState<ResendOTPResponseDataDomain?>> =
        payByCardRepository.resendOtpCode(currency, providerId).flatMapConcat {
            val result = it.content?.data?.toDomain()

            flowOf(ViewState(it.status, result, it.message))
        }
}