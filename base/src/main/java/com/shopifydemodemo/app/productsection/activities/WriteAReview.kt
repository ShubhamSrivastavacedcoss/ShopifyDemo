package com.shopifydemodemo.app.productsection.activities

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.shopifydemodemo.app.MyApplication
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.basesection.activities.NewBaseActivity
import com.shopifydemodemo.app.basesection.viewmodels.LeftMenuViewModel
import com.shopifydemodemo.app.databinding.ActivityYotpoCreateReviewBinding
import com.shopifydemodemo.app.productsection.viewmodels.ProductViewModel
import com.shopifydemodemo.app.utils.ViewModelFactory
import javax.inject.Inject

class WriteAReview : NewBaseActivity() {
    var binding: ActivityYotpoCreateReviewBinding? = null
    private var sku: String? = null
    private var title: String? = null
    private var url: String? = null
    protected lateinit var leftmenu: LeftMenuViewModel
    @Inject
    lateinit var factory: ViewModelFactory
    private var model: ProductViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val group = findViewById<ViewGroup>(R.id.container)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_yotpo_create_review, group, true)
        (application as MyApplication).mageNativeAppComponent!!.doYotpoReviewInjection(this)
        showBackButton()
        leftmenu = ViewModelProvider(this, viewModelFactory).get(LeftMenuViewModel::class.java)
        showTittle(getString(R.string.write_a_review))
        if (intent.hasExtra("sku")) {
            sku = intent.getStringExtra("sku")
            Log.i("RECEIVEDSKU",""+sku)
        }
        if (intent.hasExtra("product_title")) {
            title = intent.getStringExtra("product_title")
            Log.i("RECEIVEDSKU",""+title)
        }
        if (intent.hasExtra("product_url")) {
            url = intent.getStringExtra("product_url")
            Log.i("RECEIVEDSKU",""+url)
        }
        model = ViewModelProvider(this, factory).get(ProductViewModel::class.java)
        binding!!.submitReview.setOnClickListener {
            if(leftmenu.isLoggedIn){

            }else{
                Toast.makeText(this,"Please Login First",Toast.LENGTH_SHORT).show()
            }
        }
    }

}
