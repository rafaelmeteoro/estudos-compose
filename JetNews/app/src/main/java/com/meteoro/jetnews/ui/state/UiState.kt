package com.meteoro.jetnews.ui.state

import com.meteoro.jetnews.data.Result

/**
 * Immutable data class that allows for loading, data, and exception to be managed independently.
 * */
data class UiState<T>(
    val loading: Boolean = false,
    val exception: Exception? = null,
    val data: T? = null
) {
    /**
     * True if this contains an error
     * */
    val hasError: Boolean
        get() = exception != null

    /**
     * True if this represents a first load
     * */
    val initialLoad: Boolean
        get() = data == null && loading && !hasError
}

/**
 * Copy a UiState<T> based on a Result<T>.
 *
 * Result.Success will set all fields.
 * Result.Error will reset loading and exception only
 * */
fun <T> UiState<T>.copyWithResult(value: Result<T>): UiState<T> {
    return when (value) {
        is Result.Success -> copy(loading = false, exception = null, data = value.data)
        is Result.Error -> copy(loading = false, exception = value.exception)
    }
}