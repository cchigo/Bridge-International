package com.bridge.androidtechnicaltest.common


import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.HttpException
import java.io.InterruptedIOException
import java.net.SocketException
import java.net.UnknownHostException

object GeneralErrorHandlerImpl {
    private const val NETWORK_ERROR_MESSAGE = "Network error, please try again"
    private const val NO_INTERNET_MESSAGE = "Looks like you have no internet connection"
    private const val SERVER_ERROR_MESSAGE = "Unknown server error, please try again later"
    const val CLIENT_ERROR_MESSAGE = "Something went wrong!"
    private const val TIMEOUT_ERROR_MESSAGE = "Request timed out"

    fun getError(throwable: Throwable): BaseResponse.Error {
        val errorResponse = when (throwable) {
            is UnknownHostException -> ErrorApiResponse(title = NO_INTERNET_MESSAGE)
            is SocketException -> ErrorApiResponse(title = NETWORK_ERROR_MESSAGE)
            is InterruptedIOException -> ErrorApiResponse(title = TIMEOUT_ERROR_MESSAGE)
            is HttpException -> {
                val parsed = convertErrorBody<ErrorApiResponse>(throwable)
                ErrorApiResponse(
                    title = parsed?.title ?: SERVER_ERROR_MESSAGE,
                    status = parsed?.status ?: throwable.code(),
                    type = parsed?.type.orEmpty(),
                    traceId = parsed?.traceId.orEmpty()
                )
            }
            else -> ErrorApiResponse(title = "Unexpected error: ${throwable.message.orEmpty()}")
        }

        return BaseResponse.Error(errorResponse)
    }


}


data class ErrorApiResponse(
    val type: String? = null,
    val title: String? = null,
    val status: Int? = null,
    val traceId: String? = null
)


inline fun <reified T> convertErrorBody(t: HttpException): T? {
    return try {
        t.response()?.errorBody()?.let {
            val type = object : TypeToken<T>() {}.type
            Gson().fromJson(it.charStream(), type)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}