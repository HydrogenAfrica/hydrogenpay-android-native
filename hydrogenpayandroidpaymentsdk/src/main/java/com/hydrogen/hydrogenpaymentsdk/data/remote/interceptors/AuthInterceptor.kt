package com.hydrogen.hydrogenpaymentsdk.data.remote.interceptors

import android.util.Log
import com.google.gson.Gson
import com.hydrogen.hydrogenpaymentsdk.data.annotations.AuthorisedRequest
import com.hydrogen.hydrogenpaymentsdk.data.annotations.AuthorizedRequestModeHeader
import com.hydrogen.hydrogenpaymentsdk.data.local.sharedPrefs.SessionManagerContract
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import retrofit2.Invocation

internal class AuthInterceptor(
    private val sessionManager: SessionManagerContract
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder: Request.Builder = chain.request().newBuilder()
        val allAnnotations: ArrayList<Annotation> = arrayListOf(AuthorisedRequest())
        allAnnotations.forEach { _ ->
            val annotation = grabAnnotation<Annotation>(chain.request())
            annotation?.let {
                addAppropriateTokenForEachAnnotation(requestBuilder, it)
            }
        }
        return chain.proceed(requestBuilder.build())
    }

    private inline fun <reified T> grabAnnotation(request: Request): T? {
        return request.tag(Invocation::class.java)
            ?.method()
            ?.annotations
            ?.filterIsInstance(T::class.java)
            ?.firstOrNull()
    }

    private fun addAppropriateTokenForEachAnnotation(
        requestBuilder: Request.Builder,
        annotation: Annotation,
    ): Request.Builder {
        when (annotation) {
            is AuthorisedRequest -> {
                val token = sessionManager.getToken() ?: ""
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }

            is AuthorizedRequestModeHeader -> {
                val mode = sessionManager.getSessionTransactionCredentials()

                Log.d("DATA_SAVED_A", Gson().toJson(mode))
                requestBuilder.addHeader("mode", mode!!.transactionMode)
            }

            else -> {
                /* Do nothing */
            }
        }
        return requestBuilder
    }
}