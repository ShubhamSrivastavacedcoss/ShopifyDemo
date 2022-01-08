package com.shopifydemodemo.app.yotporewards.rewarddashboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.shopifydemodemo.app.MyApplication
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.basesection.activities.NewBaseActivity
import com.shopifydemodemo.app.databinding.ActivityRewardDashboardBinding
import com.shopifydemodemo.app.utils.ApiResponse
import com.shopifydemodemo.app.utils.Constant
import com.shopifydemodemo.app.utils.ViewModelFactory
import com.shopifydemodemo.app.yotporewards.earnrewards.EarnRewardsActivity
import com.shopifydemodemo.app.yotporewards.earnrewards.FaqsActivity
import com.shopifydemodemo.app.yotporewards.getrewards.GetRewardsActivity
import com.shopifydemodemo.app.yotporewards.myrewards.MyRewardsActivity
import com.shopifydemodemo.app.yotporewards.referfriend.ReferFriendActivity
import org.json.JSONObject
import javax.inject.Inject

class RewardDashboard : NewBaseActivity(), View.OnClickListener {
    private var binding: ActivityRewardDashboardBinding? = null
    private var model: RewardDashbordViewModel? = null
    @Inject
    lateinit var facotry: ViewModelFactory


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val group = findViewById<ViewGroup>(R.id.container)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_reward_dashboard, group, true)
        (application as MyApplication).mageNativeAppComponent!!.doRewarsDashbordInjection(this)
        showBackButton()
        showTittle(getString(R.string.rewardponts))
        model = ViewModelProvider(this, facotry).get(RewardDashbordViewModel::class.java)
        model?.context = this
        model?.myrewards?.observe(this, Observer { this.consumeDashbordReward(it) })
        model?.getMyRewards()
        binding?.getrewardSection?.setOnClickListener(this)
        binding?.earnpointSection?.setOnClickListener(this)
        binding?.referfriendSection?.setOnClickListener(this)
        binding?.myrewardsSection?.setOnClickListener(this)
        binding?.faqsSection?.setOnClickListener(this)
    }

    private fun consumeDashbordReward(response: ApiResponse?) {
        if (response?.data != null) {
                var Response =JSONObject(response?.data.toString())
                binding?.pointtitle?.text = getString(R.string.your_point) + Response.getString("points_earned")
                binding?.balancetitle?.text = "Your Balance : " + Response.getString("points_balance")
                binding?.balancetitle?.textSize=14f
                binding?.pointtitle?.textSize= 14f
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding?.getrewardSection?.id -> {
                startActivity(Intent(this, GetRewardsActivity::class.java))
                Constant.activityTransition(this)
            }
            binding?.earnpointSection?.id -> {
                startActivity(Intent(this, EarnRewardsActivity::class.java))
                Constant.activityTransition(this)
            }
            binding?.referfriendSection?.id -> {
                startActivity(Intent(this, ReferFriendActivity::class.java))
                Constant.activityTransition(this)
            }
            binding?.myrewardsSection?.id -> {
                startActivity(Intent(this, MyRewardsActivity::class.java))
                Constant.activityTransition(this)
            }
            binding?.faqsSection?.id -> {
                startActivity(Intent(this, FaqsActivity::class.java))
                Constant.activityTransition(this)
            }
        }
    }
}