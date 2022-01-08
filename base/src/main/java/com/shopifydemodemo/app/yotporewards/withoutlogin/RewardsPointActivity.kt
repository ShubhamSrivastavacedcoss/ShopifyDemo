package com.shopifydemodemo.app.yotporewards.withoutlogin

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.shopifydemodemo.app.MyApplication
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.basesection.activities.NewBaseActivity
import com.shopifydemodemo.app.databinding.ActivityRewardsPointBinding
import com.shopifydemodemo.app.loginsection.activity.LoginActivity
import com.shopifydemodemo.app.loginsection.activity.RegistrationActivity
import com.shopifydemodemo.app.utils.Constant
import com.shopifydemodemo.app.yotporewards.withoutlogin.adapter.RewardsPointAdapter
import javax.inject.Inject

class RewardsPointActivity : NewBaseActivity() {
    private var binding: ActivityRewardsPointBinding? = null
    private var titleList: MutableList<String> = mutableListOf<String>()
    private var valuesList: MutableList<String> = mutableListOf<String>()

    @Inject
    lateinit var rewadrsPointAdapter: RewardsPointAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val group = findViewById<ViewGroup>(R.id.container)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_rewards_point, group, true)
        showBackButton()
        showTittle(getString(R.string.rewardponts))
        (application as MyApplication).mageNativeAppComponent!!.doRewarsPointsInjection(this)
        addListData();
        binding?.login?.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            Constant.activityTransition(this)
            finish()
        }
        binding?.signup?.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
            Constant.activityTransition(this)
            finish()
        }
    }

    private fun addListData() {
        titleList.add("₹500 Off")
        titleList.add("₹1000 Off")
        titleList.add("₹2500 Off")
        titleList.add("Spend Rs 1000")
        titleList.add("Refer a friend")

        valuesList.add("500 Points")
        valuesList.add("1000 Points")
        valuesList.add("2500 Points")
        valuesList.add("150 Points")
        valuesList.add("500 Points")
        rewadrsPointAdapter.setData(titleList, valuesList)
        binding?.redeemList?.adapter = rewadrsPointAdapter
    }
}