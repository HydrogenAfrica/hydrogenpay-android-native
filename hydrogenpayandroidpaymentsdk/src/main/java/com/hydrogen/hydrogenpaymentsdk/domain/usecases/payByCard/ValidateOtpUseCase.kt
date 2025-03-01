package com.hydrogen.hydrogenpaymentsdk.domain.usecases.payByCard

import com.hydrogen.hydrogenpaymentsdk.domain.models.OtpValidationResponseDomain
import com.hydrogen.hydrogenpaymentsdk.domain.repository.PayByCardRepository
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.ViewState
import com.hydrogen.hydrogenpaymentsdk.utils.ModelMapper.toDomain
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf

internal class ValidateOtpUseCase(private val payByCardRepository: PayByCardRepository) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(
        otpCode: String,
        providerId: String
    ): Flow<ViewState<OtpValidationResponseDomain?>> =
        payByCardRepository.validateOtpCode(otpCode, providerId).flatMapConcat {
            if (it.content != null && it.message.contains("Failed", true)) {
                flowOf(ViewState.error(it.content.toDomain(), message = it.message))
            } else {
                val result = it.content?.toDomain()
                val message = it.message
                val status = it.status
                flowOf(ViewState(status, result, message))
            }
        }
}