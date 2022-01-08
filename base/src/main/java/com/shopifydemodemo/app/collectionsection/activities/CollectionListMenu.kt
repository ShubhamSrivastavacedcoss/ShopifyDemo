package com.shopifydemodemo.app.collectionsection.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.JsonElement
import com.shopifydemodemo.app.MyApplication
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.basesection.activities.NewBaseActivity
import com.shopifydemodemo.app.basesection.fragments.LeftMenu
import com.shopifydemodemo.app.basesection.models.MenuData
import com.shopifydemodemo.app.collectionsection.viewmodels.CollectionMenuViewModel
import com.shopifydemodemo.app.databinding.ActivityCollectionListMenuBinding
import com.shopifydemodemo.app.databinding.CollectionMenuItemBinding
import com.shopifydemodemo.app.databinding.MDynamicmenuBinding
import com.shopifydemodemo.app.searchsection.activities.AutoSearch
import com.shopifydemodemo.app.utils.ApiResponse
import com.shopifydemodemo.app.utils.Constant
import com.shopifydemodemo.app.utils.Status
import com.shopifydemodemo.app.utils.ViewModelFactory
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

class CollectionListMenu : NewBaseActivity() {
    var binding: ActivityCollectionListMenuBinding? = null

    @Inject
    lateinit var factory: ViewModelFactory
    private var model: CollectionMenuViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val group = findViewById<ViewGroup>(R.id.container)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_collection_list_menu, group, true)
        showBackButton()
        showTittle(resources.getString(R.string.collection))
        (application as MyApplication).mageNativeAppComponent!!.doCollectionInjection(this)
        model = ViewModelProvider(this, factory).get(CollectionMenuViewModel::class.java)
        model?.context = this
        model?.Response()?.observe(this, Observer { consumeMenuResponse(it) })
        binding?.clickHandler = this
    }

    fun clickSearch(view: View) {
        val searchpage = Intent(this, AutoSearch::class.java)
        startActivity(searchpage)
        Constant.activityTransition(this)
    }

    private fun consumeMenuResponse(reponse: ApiResponse?) {
        when (reponse?.status) {
            Status.SUCCESS -> renderSuccessResponse(reponse.data!!)
            Status.ERROR -> {
                reponse.error!!.printStackTrace()
                showToast(resources.getString(R.string.errorString))
            }
            else -> {
            }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    fun renderSuccessResponse(data: JsonElement) {
        Log.i("MageNative:", "MenuData$data")
        if (binding!!.menulist.childCount == 0) {
            val handler = Handler()
            val runnable = Runnable {
                try {
                    val `object` = JSONObject(data.toString())
                    if (`object`.getBoolean("success")) {
                        if (`object`.has("data")) {
                            val array = `object`.getJSONArray("data")
                            if (array.length() > 0) {
                                for (i in 0 until array.length()) {
                                    handler.post {
                                        try {
                                            // Log.i("MageNative","CurrentContext :"+currentcontext)
                                            val menuBinding: CollectionMenuItemBinding = DataBindingUtil.inflate(this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater, R.layout.collection_menu_item, null, false)
                                            val menuData = MenuData()
                                            if (array.getJSONObject(i).has("id")) {
                                                menuData.id = array.getJSONObject(i).getString("id")
                                            }
                                            if (array.getJSONObject(i).has("handle")) {
                                                menuData.handle = array.getJSONObject(i).getString("handle")
                                            }
                                            if (array.getJSONObject(i).has("type")) {
                                                menuData.type = array.getJSONObject(i).getString("type")
                                            }
                                            menuData.title = array.getJSONObject(i).getString("title")
                                            if (array.getJSONObject(i).has("url")) {
                                                menuData.url = array.getJSONObject(i).getString("url")
                                            }
                                            menuBinding.menudata = menuData
                                            menuBinding.clickdata = LeftMenu.ClickHandlers(this)
                                            if (array.getJSONObject(i).has("menus")) {
                                                menuBinding.root.findViewById<View>(R.id.expand_collapse).visibility = View.VISIBLE
                                                menuBinding.catname.tag = array.getJSONObject(i).getJSONArray("menus")
                                                updateMenu(array.getJSONObject(i).getJSONArray("menus"), menuBinding.root.findViewById(R.id.submenus))
                                            }
                                            //
                                            binding!!.menulist.addView(menuBinding.root)
                                        } catch (e: Exception) {
                                            Log.i("MageNative", "Error" + e.message)
                                            Log.i("MageNative", "Error" + e.cause)
                                            e.printStackTrace()
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            Thread(runnable).start()
        }
    }

    private fun updateMenu(array: JSONArray, menulist: LinearLayoutCompat) {
        val handler = Handler()
        val runnable = Runnable {
            if (array.length() > 0) {
                for (i in 0 until array.length()) {
                    handler.post {
                        try {
                            try {
                                val binding = DataBindingUtil.inflate<MDynamicmenuBinding>(this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater, R.layout.m_dynamicmenu, null, false)

                                val menuData = MenuData()
                                if (array.getJSONObject(i).has("id")) {
                                    menuData.id = array.getJSONObject(i).getString("id")
                                }
                                if (array.getJSONObject(i).has("handle")) {
                                    menuData.handle = array.getJSONObject(i).getString("handle")
                                }
                                menuData.title = array.getJSONObject(i).getString("title")

                                if (array.getJSONObject(i).has("type")) {
                                    menuData.type = array.getJSONObject(i).getString("type")
                                }
                                if (array.getJSONObject(i).has("url")) {
                                    menuData.url = array.getJSONObject(i).getString("url")
                                }
                                binding.menudata = menuData
                                binding.clickdata = LeftMenu.ClickHandlers(this)
                                if (array.getJSONObject(i).has("menus")) {
                                    binding!!.catname.tag = array.getJSONObject(i).getJSONArray("menus")
                                    updateMenu(array.getJSONObject(i).getJSONArray("menus"), binding.root.findViewById(R.id.submenus))
                                }
                                menulist.addView(binding.root)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
        Thread(runnable).start()
    }
}