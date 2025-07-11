package com.example.ampmeter.domain.model

/**
 * A generic class that holds a value with its loading status.
 * @param <T> Type of the resource data
 */
sealed class Resource<out T> {
    /**
     * Represents successful data loading.
     * @param data The data
     */
    data class Success<T>(val data: T) : Resource<T>()
    
    /**
     * Represents data loading failure.
     * @param message Error message
     * @param data Optional data that may be available despite the error
     */
    data class Error<T>(val message: String, val data: T? = null) : Resource<T>()
    
    /**
     * Represents data that is still loading.
     */
    class Loading<T> : Resource<T>()
    
    /**
     * Converts successful resource to error if condition is true.
     */
    fun <R> mapSuccess(transform: (T) -> R): Resource<R> {
        return when (this) {
            is Success -> Success(transform(data))
            is Error -> Error(message, data?.let { transform(it) })
            is Loading -> Loading()
        }
    }
    
    /**
     * Executes the given block if the resource is successful.
     */
    inline fun onSuccess(block: (T) -> Unit): Resource<T> {
        if (this is Success) {
            block(data)
        }
        return this
    }
    
    /**
     * Executes the given block if the resource is an error.
     */
    inline fun onError(block: (String, T?) -> Unit): Resource<T> {
        if (this is Error) {
            block(message, data)
        }
        return this
    }
    
    /**
     * Executes the given block if the resource is loading.
     */
    inline fun onLoading(block: () -> Unit): Resource<T> {
        if (this is Loading) {
            block()
        }
        return this
    }
} 