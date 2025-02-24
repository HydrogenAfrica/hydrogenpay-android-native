package com.hydrogen.hydrogenpaymentsdk.domain.usecases.payByCard

import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses.OtpValidationResponseDTO
import com.hydrogen.hydrogenpaymentsdk.domain.repository.PayByCardRepository
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.ViewState
import kotlinx.coroutines.flow.Flow

internal class ValidateOtpUseCase(private val payByCardRepository: PayByCardRepository) {
    operator fun invoke(
        otpCode: String,
        providerId: String
    ): Flow<ViewState<OtpValidationResponseDTO?>> =
        payByCardRepository.validateOtpCode(otpCode, providerId)
}