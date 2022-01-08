package com.shopifydemodemo.app.homesection.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.PagerAdapter
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.basesection.models.CommanModel
import com.shopifydemodemo.app.databinding.MBannerlayoutBinding
import com.shopifydemodemo.app.homesection.models.Home
import org.json.JSONArray

/*class HomePageBanner(fm: FragmentManager, context: Context, private var items: JSONArray) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {


        val bundle = Bundle()
        try {
            Log.i("MageNative-items","url---"+items.getJSONObject(position).getString("image_url"))
            bundle.putString("banner_image", items.getJSONObject(position).getString("image_url"))
            Log.i("MageNative-items","link_type---"+items.getJSONObject(position).getString("link_type"))
            bundle.putString("link_to", items.getJSONObject(position).getString("link_type"))
            Log.i("MageNative-items","link_value---"+items.getJSONObject(position).getString("link_value"))
            bundle.putString("id", items.getJSONObject(position).getString("link_value"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val f1 = BannerFragment()
        f1.arguments = bundle
        return f1
    }

    override fun getCount(): Int {
        Log.i("MageNative-items","size---"+items)
        Log.i("MageNative-items","size---"+items.length())
        return items.length()
    }
}*/
class HomePageBanner(fm: FragmentManager, var context: Context, private var items: JSONArray):PagerAdapter()
{
    private var binding: MBannerlayoutBinding? = null
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`

    }
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }
    override fun getCount(): Int {
        return items.length()
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.m_bannerlayout, null, false)
        val model = CommanModel()
        Log.i("MageNative-Banner","Banner"+items.getJSONObject(position).getString("image_url")!!)
        model.imageurl = items?.getJSONObject(position)?.getString("image_url")!!
        val home = Home()
        Log.i("MageNative-Banner","id"+items.getJSONObject(position).getString("link_value")!!)
        home.id = items.getJSONObject(position).getString("link_value")
        Log.i("MageNative-Banner","link_to"+items.getJSONObject(position).getString("link_type")!!)
        home.link_to = items.getJSONObject(position).getString("link_type")
        binding!!.common = model
        binding!!.home = home
        container.addView(binding!!.root)
        return binding!!.root
    }
}
