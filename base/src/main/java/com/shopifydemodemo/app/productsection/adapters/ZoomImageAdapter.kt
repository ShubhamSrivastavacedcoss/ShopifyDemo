package com.shopifydemodemo.app.productsection.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.basesection.models.CommanModel
import com.shopifydemodemo.app.databinding.ZoomImageItemBinding
import com.shopifydemodemo.app.productsection.activities.VideoPlayerActivity
import com.shopifydemodemo.app.productsection.models.MediaModel
import com.shopifydemodemo.app.utils.Constant
import javax.inject.Inject

class ZoomImageAdapter @Inject constructor() : PagerAdapter() {
    var imageList: MutableList<MediaModel>? = null

    fun setData(imageList: MutableList<MediaModel>?) {
        this.imageList = imageList
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var binding = DataBindingUtil.inflate<ZoomImageItemBinding>(LayoutInflater.from(container.context), R.layout.zoom_image_item, container, false)
        val model = CommanModel()
        model.imageurl = imageList?.get(position)?.previewUrl
        if (imageList?.get(position)?.typeName?.equals("Video") ?: false || imageList?.get(position)?.typeName?.equals("ExternalVideo") ?: false) {
            binding.playButton.visibility = View.VISIBLE
        } else {
            binding.playButton.visibility = View.GONE
        }
        binding.playButton.setOnClickListener {
            if (imageList?.get(position)?.mediaUrl?.contains("youtu")!!) {
                it.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(imageList?.get(position)?.mediaUrl)))
                Constant.activityTransition(it.context)
            } else {
                var intent = Intent(it.context, VideoPlayerActivity::class.java)
                intent.putExtra("videoLink", imageList?.get(position)?.mediaUrl)
                it.context.startActivity(intent)
                Constant.activityTransition(it.context)
            }
        }
        binding.commondata = model
        container.addView(binding.root)
        return binding.root
    }

    override fun getCount(): Int {
        return imageList?.size!!
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }
}