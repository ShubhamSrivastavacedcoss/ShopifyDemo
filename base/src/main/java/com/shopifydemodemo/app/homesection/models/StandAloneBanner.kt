package com.shopifydemodemo.app.homesection.models
import android.content.Intent
import android.util.Base64
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
class StandAloneBanner : BaseObservable() {
    @get:Bindable
    var image: String? = null
        set(image) {
            field = image
            notifyPropertyChanged(BR.image)
        }
    @get:Bindable
    var bannerlink: String? = null
        set(bannerlink) {
            field = bannerlink
            notifyPropertyChanged(BR.bannerlink)
        }
    @get:Bindable
    var bannertype: String? = null
        set(bannerlink) {
            field = bannerlink
            notifyPropertyChanged(BR.bannertype)
        }
    @get:Bindable
    var text_one: String? = null
        set(text_one) {
            field = text_one
            notifyPropertyChanged(BR.text_one)
        }
    @get:Bindable
    var text_two: String? = null
        set(text_two) {
            field = text_two
            notifyPropertyChanged(BR.text_two)
        }
    @get:Bindable
    var buttononelink: String? = null
        set(buttononelink) {
            field = buttononelink
            notifyPropertyChanged(BR.buttononelink)
        }
    @get:Bindable
    var buttononetype: String? = null
        set(buttononetype) {
            field = buttononetype
            notifyPropertyChanged(BR.buttononetype)
        }
    @get:Bindable
    var buttontwolink: String? = null
        set(buttontwolink) {
            field = buttontwolink
            notifyPropertyChanged(BR.buttontwolink)
        }
    @get:Bindable
    var buttontwotype: String? = null
        set(buttontwotype) {
            field = buttontwotype
            notifyPropertyChanged(BR.buttontwotype)
        }
    fun navigateToPage(view:View,type:String?,id:String?){
        when(type){
            "collections" -> {
                val collection = "gid://shopify/Collection/" + id
                val intent = Intent(view.context, ProductList::class.java)
                intent.putExtra("ID", getBase64Encode(collection))
                intent.putExtra("tittle", " ")
                view.context.startActivity(intent)
                Constant.activityTransition(view.context)
            }
            "products" -> {
                val product = "gid://shopify/Product/" + id
                val prod_link = Intent(view.context, ProductView::class.java)
                prod_link.putExtra("ID", getBase64Encode(product))
                view.context.startActivity(prod_link)
                Constant.activityTransition(view.context)
            }
            "web_url" -> {
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
