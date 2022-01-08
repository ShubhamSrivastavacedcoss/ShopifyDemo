package com.shopifydemodemo.app.homesection.models
import android.content.Context
import android.content.Intent
import android.util.Base64
import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.shopifydemodemo.app.basesection.activities.Weblink
import com.shopifydemodemo.app.collectionsection.activities.CollectionList
import com.shopifydemodemo.app.productsection.activities.ProductList
import com.shopifydemodemo.app.productsection.activities.ProductView
import com.shopifydemodemo.app.utils.Constant
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

class CategoryCircle : BaseObservable() {
    @get:Bindable
    var cat_image_one: String? = null
        set(cat_image_one) {
            field = cat_image_one
            notifyPropertyChanged(BR.cat_image_one)
        }
    @get:Bindable
    var cat_text_one:String?=null
        set(cat_text_one){
            field=cat_text_one
            notifyPropertyChanged(BR.cat_text_one)
        }
    @get:Bindable
    var cat_value_one:String?=null
        set(cat_value_one){
            field=cat_value_one
            notifyPropertyChanged(BR.cat_value_one)
        }
    @get:Bindable
    var cat_link_one:String?=null
        set(cat_link_one){
            field=cat_link_one
            notifyPropertyChanged(BR.cat_link_one)
        }
    @get:Bindable
    var cat_image_two: String? = null
        set(cat_image_two) {
            field = cat_image_two
            notifyPropertyChanged(BR.cat_image_two)
        }
    @get:Bindable
    var cat_text_two:String?=null
        set(cat_text_two){
            field=cat_text_two
            notifyPropertyChanged(BR.cat_text_two)
        }
    @get:Bindable
    var cat_value_two:String?=null
        set(cat_value_two){
            field=cat_value_two
            notifyPropertyChanged(BR.cat_value_two)
        }
    @get:Bindable
    var cat_link_two:String?=null
        set(cat_link_two){
            field=cat_link_two
            notifyPropertyChanged(BR.cat_link_two)
        }
    @get:Bindable
    var cat_image_three: String? = null
        set(cat_image_three) {
            field = cat_image_three
            notifyPropertyChanged(BR.cat_image_three)
        }
    @get:Bindable
    var cat_text_three:String?=null
        set(cat_text_three){
            field=cat_text_three
            notifyPropertyChanged(BR.cat_text_three)
        }
    @get:Bindable
    var cat_value_three:String?=null
        set(cat_value_three){
            field=cat_value_three
            notifyPropertyChanged(BR.cat_value_three)
        }
    @get:Bindable
    var cat_link_three:String?=null
        set(cat_link_three){
            field=cat_link_three
            notifyPropertyChanged(BR.cat_link_three)
        }
    @get:Bindable
    var cat_image_four: String? = null
        set(cat_image_four) {
            field = cat_image_four
            notifyPropertyChanged(BR.cat_image_four)
        }
    @get:Bindable
    var cat_text_four:String?=null
        set(cat_text_four){
            field=cat_text_four
            notifyPropertyChanged(BR.cat_text_four)
        }
    @get:Bindable
    var cat_value_four:String?=null
        set(cat_value_four){
            field=cat_value_four
            notifyPropertyChanged(BR.cat_value_four)
        }
    @get:Bindable
    var cat_link_four:String?=null
        set(cat_link_four){
            field=cat_link_four
            notifyPropertyChanged(BR.cat_link_four)
        }
    @get:Bindable
    var cat_image_five: String? = null
        set(cat_image_five) {
            field = cat_image_five
            notifyPropertyChanged(BR.cat_image_five)
        }
    @get:Bindable
    var cat_text_five:String?=null
        set(cat_text_five){
            field=cat_text_five
            notifyPropertyChanged(BR.cat_text_five)
        }
    @get:Bindable
    var cat_value_five:String?=null
        set(cat_value_five){
            field=cat_value_five
            notifyPropertyChanged(BR.cat_value_five)
        }
    @get:Bindable
    var cat_link_five:String?=null
        set(cat_link_five){
            field=cat_link_five
            notifyPropertyChanged(BR.cat_link_five)
        }
    @get:Bindable
    var radius:String?=null
        set(radius){
            field=radius
            notifyPropertyChanged(BR.radius)
        }
    fun catClick(view:View,category: CategoryCircle){
        when(view.tag.toString()){
            "cat_one"->{
                navigateToPage(view.context,category.cat_link_one,category.cat_value_one)
            }
            "cat_two"->{
                navigateToPage(view.context,category.cat_link_two,category.cat_value_two)
            }
            "cat_three"->{
                navigateToPage(view.context,category.cat_link_three,category.cat_value_three)
            }
            "cat_four"->{
                navigateToPage(view.context,category.cat_link_four,category.cat_value_four)
            }
            "cat_five"->{
                navigateToPage(view.context,category.cat_link_five,category.cat_value_five)
            }
        }
    }
    private fun navigateToPage(context:Context,type:String?,id:String?){
        when(type){
            "collections" -> {
                val collection = "gid://shopify/Collection/" + id
                val intent = Intent(context, ProductList::class.java)
                intent.putExtra("ID", getBase64Encode(collection))
                intent.putExtra("tittle", " ")
                context.startActivity(intent)
                Constant.activityTransition(context)
            }
            "product" -> {
                val product = "gid://shopify/Product/" + id
                val prod_link = Intent(context, ProductView::class.java)
                prod_link.putExtra("ID", getBase64Encode(product))
                context.startActivity(prod_link)
                Constant.activityTransition(context)
            }
            "web_url" -> {
                val weblink = Intent(context, Weblink::class.java)
                weblink.putExtra("link", id)
                weblink.putExtra("name", " ")
                context.startActivity(weblink)
                Constant.activityTransition(context)
            }
            "list_collection" ->{
                val weblink = Intent(context, CollectionList::class.java)
                context.startActivity(weblink)
                Constant.activityTransition(context)
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
