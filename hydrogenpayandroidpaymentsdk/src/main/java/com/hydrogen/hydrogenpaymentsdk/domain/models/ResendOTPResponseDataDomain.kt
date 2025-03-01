package com.hydrogen.hydrogenpaymentsdk.domain.models

import com.hydrogen.hydrogenpaymentsdk.data.remote.dtos.responses.OTPError

data class ResendOTPResponseDataDomain(
    val amount: String,
    val approvalCode: Any,
    val cardType: Any,
    val clientReferenceInformationCode: Any,
    val errors: List<OTPError>,
    val message: String,
    val paymentId: String,
    val processorResponse: ResendOTPProcessorResponse,
    val reConciliationId: Any,
    val resendOtpRetryCount: Int,
    val responseCode: String,
    val status: Any,
    val supportMessage: Any,
    val terminalId: Any,
    val transactionId: String,
    val transactionRef: String
)
