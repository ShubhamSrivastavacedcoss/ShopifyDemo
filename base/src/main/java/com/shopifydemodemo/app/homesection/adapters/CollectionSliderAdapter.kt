package com.shopifydemodemo.app.homesection.adapters

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.databinding.MCollectionItemBinding
import com.shopifydemodemo.app.basesection.models.CommanModel
import com.shopifydemodemo.app.collectionsection.models.Collection
import com.shopifydemodemo.app.collectionsection.viewholders.CollectionItem
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject


class CollectionSliderAdapter @Inject
constructor() : RecyclerView.Adapter<CollectionItem>() {
    private var layoutInflater: LayoutInflater? = null
    lateinit var collectionEdges: JSONArray
    lateinit var jsonObject: JSONObject
    var activity: Activity? = null
        private set

    fun setData(collectionEdges: JSONArray, activity: Activity, jsonObject: JSONObject) {
        this.collectionEdges = collectionEdges
        this.activity = activity
        this.jsonObject = jsonObject
    }

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionItem {
        val binding = DataBindingUtil.inflate<MCollectionItemBinding>(LayoutInflater.from(parent.context), R.layout.m_collection_item, parent, false)
        try {
            if (jsonObject.getString("item_shape").equals("rounded")) {
                binding.card.radius = 15f
            } else {
                binding.card.cardElevation = 0f
                binding.card.radius = 0f
            }
            when (jsonObject.getString("item_text_alignment")) {
                "left" -> {
                    binding.name.gravity = Gravity.START
                    binding.name.gravity = Gravity.CENTER_VERTICAL
                }
                "right" -> {
                    binding.name.gravity = Gravity.END
                    binding.name.gravity = Gravity.CENTER_VERTICAL
                }
            }
            if (jsonObject.getString("item_border").equals("1")) {
                if (!jsonObject.getString("item_shape").equals("rounded")) {
                    var drawable = GradientDrawable()
                    var background = JSONObject(jsonObject!!.getString("item_border_color"))
                    drawable.shape = GradientDrawable.RECTANGLE
                    drawable.setStroke(1, Color.parseColor(background.getString("color")))
                    binding.main.background = drawable
                }
            }
            if (jsonObject.getString("item_title_font_weight").equals("bold")) {
                val face = Typeface.createFromAsset(activity!!.assets, "fonts/Cairobold.ttf");
                binding.name.setTypeface(face)
            }
            if (jsonObject.getString("item_title_font_style").equals("italic")) {
                binding.name.setTypeface(binding.name.getTypeface(), Typeface.ITALIC);
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return CollectionItem(binding)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: CollectionItem, position: Int) {
        try {
            if (collectionEdges.getJSONObject(position) != null) {
                val model = CommanModel()
                if (collectionEdges.getJSONObject(position).has("image_url")) {
                    model.imageurl = collectionEdges?.getJSONObject(position)?.getString("image_url")
                    // model.imageurl = "https://images.unsplash.com/photo-1580748141549-71748dbe0bdc?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=334&q=80"
                    holder.collectionbinding.commondata = model
                }
            }
            val collection = Collection()
            if (collectionEdges.getJSONObject(position).has("title")) {
                val name = collectionEdges.getJSONObject(position).getString("title")
                collection.category_name = name
            }
            if (collectionEdges.getJSONObject(position).has("link_type")) {
                collection.type = collectionEdges.getJSONObject(position).getString("link_type")
            }
            if (collectionEdges.getJSONObject(position).has("link_value")) {
                collection.value = collectionEdges.getJSONObject(position).getString("link_value")
            }
            holder.collectionbinding.categorydata = collection
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        Log.i("MageNative", "GridSize" + collectionEdges.length())
        return collectionEdges.length()
    }

}
