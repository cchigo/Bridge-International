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

    fun getError(throwable: Throwable): BaseResponse<Nothing> {
        return when (throwable) {
            is UnknownHostException -> BaseResponse.Error(ErrorApiResponse(NO_INTERNET_MESSAGE))
            is SocketException -> BaseResponse.Error(ErrorApiResponse(NETWORK_ERROR_MESSAGE))
            is InterruptedIOException -> BaseResponse.Error(ErrorApiResponse(TIMEOUT_ERROR_MESSAGE))
            is HttpException -> {
                val errorResponse = convertErrorBody<ErrorApiResponse>(throwable)
                when (errorResponse) {
                    is ErrorApiResponse -> BaseResponse.Error(
                        ErrorApiResponse(
                        title = errorResponse.title ?: SERVER_ERROR_MESSAGE,
                        status = errorResponse.status,
                        type = errorResponse.type,
                        traceId = errorResponse.traceId
                    ))
                    else -> BaseResponse.Error(ErrorApiResponse(SERVER_ERROR_MESSAGE))
                }
            }
            else -> BaseResponse.Error(ErrorApiResponse("Unexpected error: ${throwable.message ?: "Unknown"}"))
        }
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