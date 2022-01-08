package com.shopifydemodemo.app.utils
import com.google.gson.JsonElement
import io.reactivex.annotations.NonNull
import com.shopifydemodemo.app.utils.Status.ERROR
import com.shopifydemodemo.app.utils.Status.LOADING
import com.shopifydemodemo.app.utils.Status.SUCCESS
class ApiResponse private constructor(val status: Status, val data: JsonElement?,val error: Throwable?) {
    companion object {
        fun loading(): ApiResponse {
            return ApiResponse(LOADING, null, null)
        }

        fun success(@NonNull data: JsonElement): ApiResponse {
            return ApiResponse(SUCCESS, data, null)
        }

        fun error(@NonNull error: Throwable): ApiResponse {
            return ApiResponse(ERROR, null, error)
        }
    }
}
