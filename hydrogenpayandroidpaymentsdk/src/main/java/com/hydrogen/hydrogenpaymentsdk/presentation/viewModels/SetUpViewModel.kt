package com.hydrogen.hydrogenpaymentsdk.presentation.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hydrogen.hydrogenpaymentsdk.domain.usecases.SetUpUseCase
import com.hydrogen.hydrogenpaymentsdk.utils.AppConstants.STRING_TEST_TRANSACTION_KEY_IDENTIFIER_TAG

internal class SetUpViewModel(
    private val setUpUseCase: SetUpUseCase
) : ViewModel() {
    private val _isSandBoxTransaction: MutableLiveData<Boolean> = MutableLiveData(false)
    val isSandBoxTransaction: LiveData<Boolean> get() = _isSandBoxTransaction
    fun setUp(clientToken: String) {
        if (clientToken.contains(STRING_TEST_TRANSACTION_KEY_IDENTIFIER_TAG, true)) {
            _isSandBoxTransaction.value = true
        }
        setUpUseCase.invoke(clientToken)
    }
}