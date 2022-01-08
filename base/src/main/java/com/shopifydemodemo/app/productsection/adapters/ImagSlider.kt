package com.shopifydemodemo.app.productsection.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.gson.Gson

import com.shopifydemodemo.app.productsection.fragments.ImageFragment
import com.shopifydemodemo.app.productsection.models.MediaModel

class ImagSlider(fm: FragmentManager, behavior: Int) : FragmentStatePagerAdapter(fm, behavior) {
    private var mediaList: MutableList<MediaModel>? = null
    fun setData(mediaList: MutableList<MediaModel>) {
        this.mediaList = mediaList
    }

    override fun getItem(position: Int): Fragment {
        var fragment: ImageFragment? = null
        try {
            fragment = ImageFragment()
            val bundle = Bundle()
            bundle.putSerializable("mediaModel", mediaList?.get(position))
            bundle.putString("mediaList",Gson().toJson(mediaList))
            fragment.arguments = bundle
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return fragment!!
    }

    override fun getCount(): Int {
        return mediaList?.size ?: 0
    }
}
