package com.shopifydemodemo.app.productsection.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.basesection.models.CommanModel
import com.shopifydemodemo.app.databinding.ArimagesItemBinding
import com.shopifydemodemo.app.productsection.models.MediaModel
import com.shopifydemodemo.app.utils.Constant
import javax.inject.Inject

class ArImagesAdapter @Inject constructor() : RecyclerView.Adapter<ArImagesAdapter.ArImagesViewHolder>() {
    private var arImagesList: MutableList<MediaModel>? = null
    fun setData(arImagesList: MutableList<MediaModel>) {
        this.arImagesList = arImagesList
    }

    class ArImagesViewHolder(itemView: ArimagesItemBinding) : RecyclerView.ViewHolder(itemView.root) {
        var binding: ArimagesItemBinding = itemView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArImagesViewHolder {
        var view = DataBindingUtil.inflate<ArimagesItemBinding>(LayoutInflater.from(parent.context), R.layout.arimages_item, parent, false)
        return ArImagesViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArImagesViewHolder, position: Int) {
        val model = CommanModel()
        model.imageurl = arImagesList?.get(position)?.previewUrl
        holder.binding.commondata = model
        holder.binding.arImage.setOnClickListener {
            try {
                val sceneViewerIntent = Intent(Intent.ACTION_VIEW)
                val intentUri: Uri =
                        Uri.parse("https://arvr.google.com/scene-viewer/1.1").buildUpon()
                                .appendQueryParameter("file", arImagesList?.get(position)?.previewUrl)
                                .build()
                sceneViewerIntent.setData(intentUri)
                sceneViewerIntent.setPackage("com.google.ar.core")
                it.context.startActivity(sceneViewerIntent)
                Constant.activityTransition(it.context)
            } catch (e: Exception) {
                Toast.makeText(it.context, it.context.getString(R.string.ar_error_text), Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun getItemCount(): Int {
        return arImagesList?.size ?: 0
    }
}