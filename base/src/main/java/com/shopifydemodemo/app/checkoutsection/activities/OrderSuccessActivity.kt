package com.shopifydemodemo.app.checkoutsection.activities

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.shopifydemodemo.app.MyApplication
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.basesection.activities.NewBaseActivity
import com.shopifydemodemo.app.basesection.viewmodels.SplashViewModel
import com.shopifydemodemo.app.databinding.ActivityOrderSuccessBinding
import com.shopifydemodemo.app.homesection.activities.HomePage
import com.shopifydemodemo.app.sharedprefsection.MagePrefs
import com.shopifydemodemo.app.utils.Constant

class OrderSuccessActivity : NewBaseActivity() {
    lateinit var binding: ActivityOrderSuccessBinding
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val content = findViewById<ViewGroup>(R.id.container)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_order_success, content, true)
        (application as MyApplication).mageNativeAppComponent!!.orderSuccessInjection(this)
        firebaseAnalytics = Firebase.analytics
        showTittle(resources.getString(R.string.checkout))
        showBackButton()
        if (SplashViewModel.featuresModel.firebaseEvents) {
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.PURCHASE) {
                param(FirebaseAnalytics.Param.CURRENCY, MagePrefs.getCurrency() ?: "")
                param(FirebaseAnalytics.Param.PRICE, MagePrefs.getGrandTotal() ?: "")
            }
        }
        binding.continueShopping.setOnClickListener {
            startActivity(Intent(this, HomePage::class.java))
            Constant.activityTransition(this)
            finish()
        }
    }
}