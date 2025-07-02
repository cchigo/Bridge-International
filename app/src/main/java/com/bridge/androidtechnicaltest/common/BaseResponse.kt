package com.bridge.androidtechnicaltest.common


//sealed class BaseResponse<out T> {
//    object Loading : BaseResponse<Nothing>()
//    object Empty : BaseResponse<Nothing>()
//    data class Success<T>(val data: T) : BaseResponse<T>()
//    data class Error<T>(val error: T) : BaseResponse<Nothing>()
//}

sealed class BaseResponse<out T> {
    object Loading : BaseResponse<Nothing>()
    object Empty : BaseResponse<Nothing>()
    data class Success<out T>(val data: T) : BaseResponse<T>()
    data class Error(val error: ErrorApiResponse) : BaseResponse<Nothing>()
}
