package com.hydrogen.hydrogenpaymentsdk.presentation.viewStates

open class UIEvent<out T>(private val content: T) {
    var hasBeenHandled = false
        private set // Allows only external read but prevents set

    /**
     * Returns the content and prevents it's use again
     * */
    fun getContentIfNotHandled(): T? =
        if (hasBeenHandled) null else {
            hasBeenHandled = true
            content
        }

    /**
     * Returns the content
     * */
    fun getContentIfNotHandledButDoNotSetHasBeenHandledToTrue(): T? =
        if (hasBeenHandled) null else content

    /**
     * Returns the content, even if it has already been handled
     * */
    fun peekContent(): T = content
}