package com.shopifydemodemo.app.utils

import com.shopifydemodemo.app.dbconnection.entities.AppLocalData

import io.reactivex.annotations.NonNull
import io.reactivex.annotations.Nullable

import com.shopifydemodemo.app.utils.Status.ERROR
import com.shopifydemodemo.app.utils.Status.SUCCESS

class LocalDbResponse private constructor(val status: Status, @param:Nullable @field:Nullable
val data: AppLocalData?, @param:Nullable @field:Nullable
                                          val error: Throwable?) {
    companion object {

        fun success(@NonNull data: AppLocalData): LocalDbResponse {
            return LocalDbResponse(SUCCESS, data, null)
        }

        fun error(@NonNull error: Throwable): LocalDbResponse {
            return LocalDbResponse(ERROR, null, error)
        }
    }

}
