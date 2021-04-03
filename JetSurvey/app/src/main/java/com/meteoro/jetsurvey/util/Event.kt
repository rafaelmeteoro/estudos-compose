package com.meteoro.jetsurvey.util

/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 * */
data class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not wirte

    /**
     * Returns the content and prevents its use again.
     * */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, event if it's already been handled.
     * */
    fun peekContent(): T = content
}