package com.shopifydemodemo.app.homesection.models

import android.content.Intent
import android.util.Base64
import android.view.Gravity
import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.shopifydemodemo.app.basesection.activities.Weblink
import com.shopifydemodemo.app.productsection.activities.ProductList
import com.shopifydemodemo.app.productsection.activities.ProductView
import com.shopifydemodemo.app.utils.Constant
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

class ProductSlider : BaseObservable() {
    var action_id: String? = null

    @get:Bindable
    var headertext: String? = null
        set(headertext) {
            field = headertext
            notifyPropertyChanged(BR.headertext)
        }

    @get:Bindable
    var headertextvisibility: Int? = null
        set(headertextvisibility) {
            field = headertextvisibility
            notifyPropertyChanged(BR.headertextvisibility)
        }

    @get:Bindable
    var actiontext: String? = null
        set(actiontext) {
            field = actiontext
            notifyPropertyChanged(BR.actiontext)
        }

    @get:Bindable
    var actiontextvisibity: Int? = null
        set(actiontextvisibity) {
            field = actiontextvisibity
            notifyPropertyChanged(BR.actiontextvisibity)
        }

    @get:Bindable
    var subheadertext: String? = null
        set(subheadertext) {
            field = subheadertext
            notifyPropertyChanged(BR.subheadertext)
        }

    @get:Bindable
    var subheadertextvisibity: Int? = null
        set(subheadertextvisibity) {
            field = subheadertextvisibity
            notifyPropertyChanged(BR.subheadertextvisibity)
        }

    @get:Bindable
    var timericon: Int? = null
        set(timericon) {
            field = timericon
            notifyPropertyChanged(BR.timericon)
        }

    @get:Bindable
    var timertext: String? = null
        set(timertext) {
            field = timertext
            notifyPropertyChanged(BR.timertext)
        }

    @get:Bindable
    var timertextmessage: String? = null
        set(timertextmessage) {
            if (timertextmessage.equals("{deal-time}")) {
                field = ""
            } else if (timertextmessage?.contains("{")!!) {
                field = timertextmessage.split("{").get(1).split("}").get(1)
            } else {
                field = timertextmessage
            }
            notifyPropertyChanged(BR.timertextmessage)
        }

    fun moreAction(view: View, category: ProductSlider) {
        var resultIntent: Intent = Intent(view.context, ProductList::class.java)
        resultIntent.putExtra("ID", category.action_id)
        resultIntent.putExtra("tittle", category.headertext)
        view.context.startActivity(resultIntent)
        Constant.activityTransition(view.context)
    }

    @get:Bindable
    var hvimageone: String? = null
        set(hvimageone) {
            field = hvimageone
            notifyPropertyChanged(BR.hvimageone)
        }

    @get:Bindable
    var hvnameone: String? = null
        set(hvnameone) {
            field = hvnameone
            notifyPropertyChanged(BR.hvnameone)
        }

    @get:Bindable
    var hvtypeone: String? = null
        set(hvtypeone) {
            field = hvtypeone
            notifyPropertyChanged(BR.hvtypeone)
        }

    @get:Bindable
    var hvvalueone: String? = null
        set(hvvalueone) {
            field = hvvalueone
            notifyPropertyChanged(BR.hvvalueone)
        }

    @get:Bindable
    var hvimagetwo: String? = null
        set(hvimagetwo) {
            field = hvimagetwo
            notifyPropertyChanged(BR.hvimagetwo)
        }

    @get:Bindable
    var hvnametwo: String? = null
        set(hvnametwo) {
            field = hvnametwo
            notifyPropertyChanged(BR.hvnametwo)
        }

    @get:Bindable
    var hvtypetwo: String? = null
        set(hvtypetwo) {
            field = hvtypetwo
            notifyPropertyChanged(BR.hvtypetwo)
        }

    @get:Bindable
    var hvvaluetwo: String? = null
        set(hvvaluetwo) {
            field = hvvaluetwo
            notifyPropertyChanged(BR.hvvaluetwo)
        }

    @get:Bindable
    var hvimagethree: String? = null
        set(hvimagethree) {
            field = hvimagethree
            notifyPropertyChanged(BR.hvimagethree)
        }

    @get:Bindable
    var hvnamethree: String? = null
        set(hvnamethree) {
            field = hvnamethree
            notifyPropertyChanged(BR.hvnamethree)
        }

    @get:Bindable
    var hvtypethree: String? = null
        set(hvtypethree) {
            field = hvtypethree
            notifyPropertyChanged(BR.hvtypethree)
        }

    @get:Bindable
    var hvvaluethree: String? = null
        set(hvvaluethree) {
            field = hvvaluethree

            notifyPropertyChanged(BR.hvvaluethree)
        }

    @get:Bindable
    var textaligment: Int = Gravity.CENTER
        set(textaligment) {
            field = textaligment
            notifyPropertyChanged(BR.textaligment)
        }

    fun navigateToPage(view: View, type: String?, id: String?) {
        when (type) {
            "collections" -> {
                val collection = "gid://shopify/Collection/" + id
                val intent = Intent(view.context, ProductList::class.java)
                intent.putExtra("ID", getBase64Encode(collection))
                intent.putExtra("tittle", " ")
                view.context.startActivity(intent)
                Constant.activityTransition(view.context)
            }
            "product" -> {
                val product = "gid://shopify/Product/" + id
                val prod_link = Intent(view.context, ProductView::class.java)
                prod_link.putExtra("ID", getBase64Encode(product))
                view.context.startActivity(prod_link)
                Constant.activityTransition(view.context)
            }
            "web_address" -> {
                val weblink = Intent(view.context, Weblink::class.java)
                weblink.putExtra("link", id)
                weblink.putExtra("name", " ")
                view.context.startActivity(weblink)
                Constant.activityTransition(view.context)
            }
        }
    }

    private fun getBase64Encode(id: String): String {
        var id = id
        val data = Base64.encode(id.toByteArray(), Base64.DEFAULT)
        try {
            id = String(data, Charset.defaultCharset()).trim { it <= ' ' }
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        return id
    }
}
