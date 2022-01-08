package com.shopifydemodemo.app.yotporewards.myrewards

import android.os.Bundle
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.shopifydemodemo.app.MyApplication
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.basesection.activities.NewBaseActivity
import com.shopifydemodemo.app.databinding.ActivityMyRewardsBinding
import com.shopifydemodemo.app.utils.ApiResponse
import com.shopifydemodemo.app.utils.ViewModelFactory
import com.shopifydemodemo.app.yotporewards.myrewards.adapter.MyRewardAdapter
import com.shopifydemodemo.app.yotporewards.myrewards.model.MyRewardModel
import com.google.gson.Gson
import javax.inject.Inject

class MyRewardsActivity : NewBaseActivity() {
    private var binding: ActivityMyRewardsBinding? = null
    private var myRewardsViewModel: MyRewardsViewModel? = null

    @Inject
    lateinit var factory: ViewModelFactory

    @Inject
    lateinit var myRewardAdapter: MyRewardAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val group = findViewById<ViewGroup>(R.id.container)
        (application as MyApplication).mageNativeAppComponent!!.doMyRewardInjection(this)
        showBackButton()
        showTittle(getString(R.string.my_rewads))
        myRewardsViewModel = ViewModelProvider(this, factory).get(MyRewardsViewModel::class.java)
        myRewardsViewModel?.context = this
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_my_rewards, group, true)
        myRewardsViewModel?.myrewards?.observe(this, Observer { this.consumeMyRewards(it) })
        myRewardsViewModel?.getMyRewards()

    }

    private fun consumeMyRewards(response: ApiResponse?) {
        if (response?.data != null) {
            var myrewardmodel = Gson().fromJson<String>(response?.data.toString(), MyRewardModel::class.java) as MyRewardModel
            if (myrewardmodel?.historyItems?.size!! > 0) {
                myRewardAdapter.setData(myrewardmodel.historyItems!!)
                binding?.myrewardlist?.adapter = myRewardAdapter
            }

        }
    }
}