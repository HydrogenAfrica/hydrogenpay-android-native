package com.hydrogen.hydrogenpaymentsdk.domain.usecases

import com.hydrogen.hydrogenpaymentsdk.data.local.sharedPrefs.SessionManagerContract

internal class SetUpUseCase(
    private val sessionManager: SessionManagerContract
) {
    operator fun invoke(token: String) {
        sessionManager.saveToken(token)
    }
}