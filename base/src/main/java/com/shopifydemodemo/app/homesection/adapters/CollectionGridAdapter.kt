package com.shopifydemodemo.app.homesection.adapters

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonElement
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.databinding.MCategorygriditemBinding
import com.shopifydemodemo.app.basesection.models.CommanModel
import com.shopifydemodemo.app.collectionsection.models.Collection
import com.shopifydemodemo.app.collectionsection.viewholders.CollectionItem
import org.json.JSONObject
import javax.inject.Inject


class CollectionGridAdapter @Inject
constructor() : RecyclerView.Adapter<CollectionItem>() {
    private var layoutInflater: LayoutInflater? = null
    lateinit var collectionEdges: List<JsonElement>
    lateinit var jsonObject: JSONObject
    var activity: Activity? = null
        private set

    fun setData(collectionEdges: List<JsonElement>, activity: Activity, jsonObject: JSONObject) {
        this.collectionEdges = collectionEdges
        this.activity = activity
        this.jsonObject = jsonObject
    }

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionItem {
        val binding = DataBindingUtil.inflate<MCategorygriditemBinding>(LayoutInflater.from(parent.context), R.layout.m_categorygriditem, parent, false)
        try {

//            if (jsonObject.getString("item_shape").equals("rounded")) {
//                binding.card.cardElevation = 3f
//                binding.card.radius = 10f
//            } else {
//                binding.card.cardElevation = 0f
//                binding.card.radius = 0f
//            }
            when (jsonObject.getString("item_text_alignment")) {
                "center" -> {
                    binding.name.gravity = Gravity.CENTER
                }
                "right" -> {
                    binding.name.gravity = Gravity.END
                    binding.name.gravity = Gravity.CENTER_VERTICAL
                }
            }
            if (jsonObject.getString("item_title").equals("0")) {
                binding.name.visibility = View.GONE
                (binding.card.layoutParams as ConstraintLayout.LayoutParams).dimensionRatio = "1:1"
            } else {
                var background = JSONObject(jsonObject.getString("item_title_color"))
                binding.name.setTextColor(Color.parseColor(background.getString("color")))
            }
            if (jsonObject.getString("item_border").equals("1")) {
                // binding.main.setPadding(1, 1, 1, 1)
                if (!jsonObject.getString("item_shape").equals("rounded")) {
                    var drawable = GradientDrawable()
                    var background = JSONObject(jsonObject.getString("item_border_color"))
                    drawable.shape = GradientDrawable.RECTANGLE
                    drawable.setStroke(1, Color.parseColor(background.getString("color")))
                    binding.main.background = drawable
                } else if (jsonObject.getString("item_shape").equals("rounded")) {
                    binding.card.cardElevation = 0f
                    binding.card.radius = 10f
                    var background = JSONObject(jsonObject.getString("item_border_color"))
                    var drawable = GradientDrawable()
                    drawable.shape = GradientDrawable.RECTANGLE
                    drawable.cornerRadius = 10f
                    drawable.setStroke(2, Color.parseColor(background.getString("color")))
                    binding.main.background = drawable
                }
            } else if (jsonObject.getString("item_border").equals("0")) {
                if (!jsonObject.getString("item_shape").equals("rounded")) {
                    binding.card.cardElevation = 0f
                    binding.card.radius = 0f
                } else if (jsonObject.getString("item_shape").equals("rounded")) {
                    binding.card.radius = 10f
                    binding.card.cardElevation = 0f
                }
            }
            var background = JSONObject(jsonObject.getString("cell_background_color"))
            binding.name.setBackgroundColor(Color.parseColor(background.getString("color")))
            if (jsonObject.getString("item_font_weight").equals("bold")) {
                val face = Typeface.createFromAsset(activity!!.assets, "fonts/cairobold.ttf");
                binding.name.setTypeface(face)
            }
            if (jsonObject.getString("item_font_style").equals("italic")) {
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
            if (collectionEdges.get(position) != null) {
                val model = CommanModel()
                if (collectionEdges.get(position).asJsonObject.has("image_url")) {
                    model.imageurl = collectionEdges?.get(position)?.asJsonObject?.get("image_url")?.asString
                    holder.gridbinding.commondata = model
                }
            }
            val collection = Collection()
            if (collectionEdges.get(position).asJsonObject.has("title")) {
                val name = collectionEdges.get(position).asJsonObject.get("title").asString
                collection.category_name = name
            }
            if (collectionEdges.get(position).asJsonObject.has("link_type")) {
                collection.type = collectionEdges.get(position).asJsonObject.get("link_type").asString
            }
            if (collectionEdges.get(position).asJsonObject.has("link_value")) {
                collection.value = collectionEdges.get(position).asJsonObject.get("link_value").asString
            }
            holder.gridbinding.categorydata = collection
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        Log.i("TEST", "" + collectionEdges.size)
        return collectionEdges.size

    }

}
