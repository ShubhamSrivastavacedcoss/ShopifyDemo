package com.shopifydemodemo.app.collectionsection.models

import android.content.Intent
import android.util.Base64
import android.view.View

import com.shopify.graphql.support.ID
import com.shopifydemodemo.app.basesection.activities.Weblink
import com.shopifydemodemo.app.productsection.activities.ProductList
import com.shopifydemodemo.app.utils.Constant
import java.nio.charset.Charset

class Collection {

    var category_name: String? = null

    var id: ID? = null

    fun blockClick(view: View, collection: Collection) {
        val intent = Intent(view.context, ProductList::class.java)
        intent.putExtra("tittle", collection.category_name)
        intent.putExtra("ID", collection.id!!.toString())
        view.context.startActivity(intent)
        Constant.activityTransition(view.context)
    }

    fun gridClick(view: View, collection: Collection) {
        when (collection.type) {
            "collections" -> {
                val intent = Intent(view.context, ProductList::class.java)
                intent.putExtra("tittle", collection.category_name)
                intent.putExtra("ID", getcategoryID(collection.value))
                view.context.startActivity(intent)
                Constant.activityTransition(view.context)
            }
            else -> {
                val intent = Intent(view.context, Weblink::class.java)
                intent.putExtra("name", collection.category_name)
                intent.putExtra("link", collection.value)
                view.context.startActivity(intent)
                Constant.activityTransition(view.context)
            }
        }

    }

    var type: String? = null
    var value: String? = null
    private fun getcategoryID(id: String?): String? {
        var cat_id: String? = null
        try {
            val data = Base64.encode("gid://shopify/Collection/$id".toByteArray(), Base64.DEFAULT)
            cat_id = String(data, Charset.defaultCharset()).trim { it <= ' ' }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return cat_id
    }
}

