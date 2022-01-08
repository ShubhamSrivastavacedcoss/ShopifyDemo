package com.shopifydemodemo.app.homesection.models

import android.content.Intent
import android.view.View

import com.shopifydemodemo.app.basesection.models.ListData
import com.shopifydemodemo.app.productsection.activities.ProductView
import com.shopifydemodemo.app.utils.Constant

class Product {
    fun productClick(view: View, data: ListData) {
        val productintent = Intent(view.context, ProductView::class.java)
        productintent.putExtra("ID", data.product!!.id.toString())
        productintent.putExtra("tittle", data.textdata)
        productintent.putExtra("product", data.product)
        view.context.startActivity(productintent)
        Constant.activityTransition(view.context)
    }
}
