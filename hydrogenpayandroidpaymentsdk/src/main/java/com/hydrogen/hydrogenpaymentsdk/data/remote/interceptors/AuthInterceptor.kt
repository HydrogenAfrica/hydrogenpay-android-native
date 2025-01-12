package com.hydrogen.hydrogenpaymentsdk.data.remote.interceptors

import com.hydrogen.hydrogenpaymentsdk.data.annotations.AuthorisedRequest
import com.hydrogen.hydrogenpaymentsdk.utils.AppConstants.AUTH_TOKEN
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import retrofit2.Invocation

internal class AuthInterceptor(
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
                requestBuilder.addHeader("Authorization", "Bearer $AUTH_TOKEN")
            }

            else -> {
                /* Do nothing */
            }
        }
        return requestBuilder
    }
}