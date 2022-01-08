package com.shopifydemodemo.app.productsection.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.google.gson.GsonBuilder
import com.shopifydemodemo.app.MyApplication
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.basesection.activities.NewBaseActivity
import com.shopifydemodemo.app.cartsection.activities.CartList
import com.shopifydemodemo.app.databinding.ActivityZoomBinding
import com.shopifydemodemo.app.productsection.adapters.ZoomImageAdapter
import com.shopifydemodemo.app.productsection.models.MediaModel
import com.shopifydemodemo.app.utils.Constant
import kotlinx.android.synthetic.main.m_newbaseactivity.*
import javax.inject.Inject

class ZoomActivity : NewBaseActivity() {
    private var binding: ActivityZoomBinding? = null
    private var images_list: MutableList<MediaModel>? = null

    @Inject
    lateinit var zoomImageAdapter: ZoomImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val group = findViewById<ViewGroup>(R.id.container)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_zoom, group, true)
        (application as MyApplication).mageNativeAppComponent!!.doZoomActivityInjection(this)
        showBackButton()
        nav_view.visibility = View.GONE
        showTittle(" ")
        if (intent.hasExtra("images")) {
            var mediaList = intent.getStringExtra("images")
            images_list = GsonBuilder().create().fromJson(mediaList, Array<MediaModel>::class.java).toMutableList()
            zoomImageAdapter.setData(images_list)
            binding?.imagesSlider?.adapter = zoomImageAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.m_product, menu)
        val item = menu.findItem(R.id.cart_item)
        item.setActionView(R.layout.m_count)
        val notifCount = item.actionView
        val textView = notifCount.findViewById<TextView>(R.id.count)
        textView.text = "" + cartCount
        notifCount.setOnClickListener {
            val mycartlist = Intent(this, CartList::class.java)
            startActivity(mycartlist)
            Constant.activityTransition(this)
        }
        return true
    }
}