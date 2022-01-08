package com.shopifydemodemo.app.utils

import com.shopify.buy3.GraphCallResult
import io.reactivex.annotations.NonNull
import io.reactivex.annotations.Nullable
import com.shopifydemodemo.app.utils.Status.ERROR
import com.shopifydemodemo.app.utils.Status.SUCCESS

class GraphQLResponse private constructor(val status: Status, @param:Nullable @field:Nullable
val data: GraphCallResult.Success<*>?, @param:Nullable @field:Nullable
                                          val error: GraphCallResult.Failure?) {
    companion object {

        fun success(@NonNull data: GraphCallResult.Success<*>): GraphQLResponse {
            return GraphQLResponse(SUCCESS, data, null)
        }

        fun error(@NonNull error: GraphCallResult.Failure): GraphQLResponse {
            return GraphQLResponse(ERROR, null, error)
        }
    }

}
