package com.hydrogen.hydrogenpaymentsdk.data.remote.dtos

/**
 * The request to pass to perform payment via transfer.
 *
 * @property [amount] The amount to pay as a whole number integer. This is a required field
 * @property [customerName] The name of the customer. This is also a required field.
 * @property [email] The email to which the copy of the transaction receipt should be sent after payment. This is a required field as well.
 * @property [transactionRef] Alphanumeric string of not less than 8 characters which is to be used as the reference id for the transaction. This is optional as one would be automatically generated by Hydrogen if not passed.
 * @property [currency] This is the currency in which the transfer would be made. Default to NGN if not passed, other currencies available are USD and GBP.
 * @property [description] Payment Description. This is also optional.
 * @property [meta] Use this field to pass any other information you want to be recorded with the transaction.
 * */
data class PayByTransferRequest(
    val amount: Int,
    val customerName: String,
    val email: String,
    val callback: String,
    val transactionRef: String? = null,
    val currency: String? = null,
    val description: String? = null,
    val meta: String? = null
)