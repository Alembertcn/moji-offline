package com.demo.mojioffline.core.result

sealed class AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>()
    data class Failure(val error: AppError) : AppResult<Nothing>()
}

sealed class AppError {
    data object Storage : AppError()
    data object Unexpected : AppError()
    data class Message(val value: String) : AppError()
}

inline fun <T> runCatchingApp(block: () -> T): AppResult<T> =
    try {
        AppResult.Success(block())
    } catch (_: StorageException) {
        AppResult.Failure(AppError.Storage)
    } catch (error: Throwable) {
        AppResult.Failure(AppError.Message(error.message ?: "Unexpected error"))
    }

class StorageException(message: String? = null) : Exception(message)
