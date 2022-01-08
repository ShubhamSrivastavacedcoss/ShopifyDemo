package com.shopifydemodemo.app.productsection.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.shopifydemodemo.app.MyApplication
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.basesection.activities.NewBaseActivity
import com.shopifydemodemo.app.databinding.ActivityFilterBinding
import com.shopifydemodemo.app.databinding.FilterdataLayoutBinding
import com.shopifydemodemo.app.productsection.adapters.MainFilterAdapter
import org.json.JSONArray
import org.json.JSONObject

class FilterActivity : NewBaseActivity() {
    private var activityFilterBinding: ActivityFilterBinding? = null
    private var filterData = ArrayList<String>()
    private var handel: String? = null
    private val TAG = "FilterActivity"
    lateinit var mainFilterAdapter: MainFilterAdapter
    private var selectedTags: String? = null
    var pricearr: ArrayList<String> = ArrayList<String>()

    companion object {
        var listMap: HashMap<String, String> = HashMap<String, String>()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val group = findViewById<ViewGroup>(R.id.container)
        activityFilterBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.activity_filter, group, true)
        showBackButton()
        showTittle(resources.getString(R.string.apply_filter))
        (application as MyApplication).mageNativeAppComponent!!.doFilterInjection(this)
        if (intent.hasExtra("filterData") && intent.hasExtra("handle")) {
            filterData = intent.getStringArrayListExtra("filterData") as ArrayList<String>
            handel = intent.getStringExtra("handle")
            loadFilterData(filterData)
        }
        activityFilterBinding?.scrollview?.isNestedScrollingEnabled = false
        activityFilterBinding?.applyFilter?.setOnClickListener {
            var intent = Intent()

            Log.i("selectedTagslog", "" + selectedTags)
            intent.putExtra(
                "result", selectedTags!!.replace("[", "")
                    .replace("]", "")
            )

            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        activityFilterBinding?.clearFilter?.setOnClickListener {

            var intent = Intent()
            intent.putExtra("result", "")
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun loadFilterData(filterData: ArrayList<String>) {
        var listData = HashMap<String, ArrayList<String>>()
        //var valvet = ArrayList<String>()
        val temp_keys = ArrayList<String>()
        val uniqueobj = JSONObject()
        var jsonArray = JSONArray()
        for (x in 0 until filterData.size) {
            val key = filterData.get(x).split("_").get(0)
            val value = filterData.get(x).split("_").get(1)
            if (temp_keys.contains(key)) {
                uniqueobj.put(key, jsonArray.put(value))
            } else {
                jsonArray = JSONArray()
                uniqueobj.put(key, jsonArray.put(value))
            }
            temp_keys.add(key)
        }
        Log.i("temp_keys", "" + uniqueobj)
        for (i in 0 until uniqueobj.length()) {
            var view: FilterdataLayoutBinding? = null
            view = DataBindingUtil.inflate(layoutInflater, R.layout.filterdata_layout, null, false)
            view?.filterTitle?.text = uniqueobj.names().get(i).toString()
            var display: String = uniqueobj.names().get(i).toString()
            var valvet = ArrayList<String>()
            var subArr: JSONArray = uniqueobj.getJSONArray(uniqueobj.names().get(i).toString())
            listData = HashMap<String, ArrayList<String>>()
            for (j in 0 until subArr.length()) {
                var label = subArr.get(j).toString()
                valvet.add(label)
                listData.put(display, valvet)
            }
            listData.forEach { (key, value) ->
                mainFilterAdapter = MainFilterAdapter(value, key,
                    tagSelectionCallBack = object : MainFilterAdapter.TagSelectionCallBack {
                        override fun tagCallback(list: String) {
                            selectedTags = list
                        }
                    })
                view?.filterItemList?.adapter = mainFilterAdapter
            }
            activityFilterBinding?.filterList?.addView(view?.root)
        }
    }
}
