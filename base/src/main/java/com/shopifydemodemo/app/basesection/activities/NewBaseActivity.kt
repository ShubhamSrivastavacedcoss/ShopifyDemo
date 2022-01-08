package com.shopifydemodemo.app.basesection.activities

import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.zxing.integration.android.IntentIntegrator
import com.shopify.buy3.Storefront
import com.shopifydemodemo.app.MyApplication
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.R2
import com.shopifydemodemo.app.basesection.ItemDecoration.GridSpacingItemDecoration
import com.shopifydemodemo.app.basesection.adapters.LanguageListAdapter
import com.shopifydemodemo.app.basesection.adapters.RecylerAdapter
import com.shopifydemodemo.app.basesection.fragments.BaseFragment
import com.shopifydemodemo.app.basesection.viewmodels.LeftMenuViewModel
import com.shopifydemodemo.app.basesection.viewmodels.SplashViewModel.Companion.featuresModel
import com.shopifydemodemo.app.cartsection.activities.CartList
import com.shopifydemodemo.app.customviews.MageNativeTextView
import com.shopifydemodemo.app.databinding.CurrencyListLayoutBinding
import com.shopifydemodemo.app.databinding.LanguageDialogBinding
import com.shopifydemodemo.app.dbconnection.entities.CartItemData
import com.shopifydemodemo.app.dbconnection.entities.ItemData
import com.shopifydemodemo.app.homesection.activities.HomePage
import com.shopifydemodemo.app.loginsection.activity.LoginActivity
import com.shopifydemodemo.app.searchsection.activities.AutoSearch
import com.shopifydemodemo.app.sharedprefsection.MagePrefs
import com.shopifydemodemo.app.userprofilesection.activities.UserProfile
import com.shopifydemodemo.app.utils.*
import com.shopifydemodemo.app.utils.Urls.Data.fbusername
import com.shopifydemodemo.app.utils.Urls.Data.whatsappnumber
import com.shopifydemodemo.app.wishlistsection.activities.WishList
import info.androidhive.fontawesome.FontTextView
import kotlinx.android.synthetic.main.m_newbaseactivity.*
import org.json.JSONObject
import zendesk.chat.ChatEngine
import zendesk.messaging.Engine
import zendesk.messaging.MessagingActivity
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

open class NewBaseActivity : AppCompatActivity(), BaseFragment.OnFragmentInteractionListener {

    @BindView(R2.id.toolbar)
    lateinit var toolbar: Toolbar

    @BindView(R2.id.toolimage)
    lateinit var toolimage: ImageView

    @BindView(R2.id.tooltext)
    lateinit var tooltext: MageNativeTextView

    @BindView(R2.id.searchsection)
    lateinit var searchsection: RelativeLayout

    @BindView(R2.id.drawer_layout)
    lateinit var drawer_layout: DrawerLayout

    @BindView(R2.id.search)
    lateinit var search: MageNativeTextView
    public var mDrawerToggle: ActionBarDrawerToggle? = null

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    var leftMenuViewModel: LeftMenuViewModel? = null
    var wishtextView: TextView? = null
    var textView: TextView? = null
    var wishcount_bottom: TextView? = null
    var cartcount_bottom: TextView? = null
    private val TAG = "NewBaseActivity"

    companion object {
        lateinit var themeColor: String
    }

    @Inject
    lateinit var languageListAdapter: LanguageListAdapter

    @Inject
    lateinit var recylerAdapter: RecylerAdapter
    private var listDialog: BottomSheetDialog? = null
    var cartCount: Int = 0
    lateinit var item: MenuItem
    lateinit var wishitem: MenuItem
    lateinit var cartitem: MenuItem
    private var languages: HashMap<String, String>? = null
    private var toNumber = "+916393417500"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.m_newbaseactivity)
        ButterKnife.bind(this)
        (application as MyApplication).mageNativeAppComponent!!.doBaseActivityInjection(this)
        leftMenuViewModel =
            ViewModelProvider(this, viewModelFactory).get(LeftMenuViewModel::class.java)
        leftMenuViewModel!!.context = this
//        if (this@NewBaseActivity is HomePage) {
//            leftMenuViewModel!!.Response()
//                .observe(this, Observer<ApiResponse> { this.consumeResponse(it) })
//        }
        leftMenuViewModel!!.repository.allCartItemsCount.observe(
            this,
            Observer { this.consumeCartCount(it) })
        leftMenuViewModel!!.repository.wishListDataCount.observe(
            this,
            Observer { this.consumeWishCount(it) })
        setSupportActionBar(toolbar)
        setToggle()
        Objects.requireNonNull<ActionBar>(supportActionBar).setDisplayShowTitleEnabled(false)
        tooltext!!.textSize = 14f
        showHumburger()
        if (!featuresModel.in_app_wishlist) {
            nav_view.menu.findItem(R.id.wishlist_bottom).setVisible(false)
        } else {
            nav_view.menu.findItem(R.id.wishlist_bottom).setVisible(true)
        }
        if (featuresModel.showBottomNavigation) {
            nav_view.visibility = View.VISIBLE
        } else {
            nav_view.visibility = View.GONE
        }
        nav_view.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home_bottom -> {
                    startActivity(
                        Intent(
                            this,
                            HomePage::class.java
                        ).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    )
                    Constant.activityTransition(this)
                }
                R.id.search_bottom -> {
                    startActivity(Intent(this, AutoSearch::class.java))
                    Constant.activityTransition(this)
                }
                R.id.cart_bottom -> {
                    startActivity(Intent(this, CartList::class.java))
                    Constant.activityTransition(this)
                }
                R.id.wishlist_bottom -> {
                    startActivity(Intent(this, WishList::class.java))
                    Constant.activityTransition(this)
                }
                R.id.account_bottom -> {
                    if (leftMenuViewModel?.isLoggedIn!!) {
                        startActivity(Intent(this, UserProfile::class.java))
                        Constant.activityTransition(this)
                    } else {
                        startActivity(Intent(this, LoginActivity::class.java))
                        Constant.activityTransition(this)
                    }
                }
            }
            true
        }

        val mbottomNavigationMenuView: BottomNavigationMenuView =
            nav_view.getChildAt(0) as BottomNavigationMenuView
        val cartview: View = mbottomNavigationMenuView.getChildAt(2)
        val wishview: View = mbottomNavigationMenuView.getChildAt(3)

        val cartitemView: BottomNavigationItemView = cartview as BottomNavigationItemView
        val whishitemView: BottomNavigationItemView = wishview as BottomNavigationItemView

        val cart_badge: View = LayoutInflater.from(this)
            .inflate(
                R.layout.cart_count_bottom,
                mbottomNavigationMenuView, false
            )

        val wish_badge: View = LayoutInflater.from(this)
            .inflate(
                R.layout.wish_count_bottom,
                mbottomNavigationMenuView, false
            )

        wishcount_bottom = wish_badge.findViewById(R.id.count)
        cartcount_bottom = cart_badge.findViewById(R.id.count)

        whishitemView.addView(wish_badge)
        cartitemView.addView(cart_badge)

        if (this@NewBaseActivity is HomePage) {
            nav_view.menu.findItem(R.id.home_bottom).setChecked(true)
        } else if (this@NewBaseActivity is AutoSearch) {
            nav_view.menu.findItem(R.id.search_bottom).setChecked(true)
        } else if (this@NewBaseActivity is CartList) {
            nav_view.menu.findItem(R.id.cart_bottom).setChecked(true)
        } else if (this@NewBaseActivity is WishList) {
            nav_view.menu.findItem(R.id.wishlist_bottom).setChecked(true)
        } else if (this@NewBaseActivity is UserProfile) {
            nav_view.menu.findItem(R.id.account_bottom).setChecked(true)
        }
        if (this@NewBaseActivity !is HomePage) {
            fullsearch_container.visibility = View.GONE
        }
        try {
            MyApplication?.dataBaseReference?.child("additional_info")?.child("appthemecolor")
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        var value = dataSnapshot.getValue(String::class.java)!!
                        if (!value.contains("#")) {
                            value = "#" + value
                        }
                        if (this@NewBaseActivity !is HomePage) {
                            toolbar.setBackgroundColor(Color.parseColor(value))
                        }
                        themeColor = value
                        val iconColorStates = ColorStateList(
                            arrayOf(
                                intArrayOf(-android.R.attr.state_checked),
                                intArrayOf(android.R.attr.state_checked)
                            ), intArrayOf(
                                Color.parseColor("#000000"),
                                Color.parseColor(value)
                            )
                        )
                        nav_view.setItemIconTintList(iconColorStates)
                        nav_view.setItemTextColor(iconColorStates)
                        //   nav_view.menu.findItem(R.id.home_bottom).iconTintList = ColorStateList.valueOf(Color.parseColor(value))
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.i("DBConnectionError", "" + databaseError.details)
                        Log.i("DBConnectionError", "" + databaseError.message)
                        Log.i("DBConnectionError", "" + databaseError.code)
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }

        /********************************* Chat Options **************************************/


        if (this@NewBaseActivity !is HomePage) {
            chat_but.visibility = View.GONE
        }

        val chatEngine: Engine? = ChatEngine.engine()
        chat_but.setOnClickListener {
            MessagingActivity.builder()
                .withEngines(chatEngine)
                .show(this@NewBaseActivity)
        }

        whatsappchat.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("http://api.whatsapp.com/send?phone=$whatsappnumber")
            startActivity(intent)
        }

        messengerchat.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.setPackage("com.facebook.orca")
            intent.data = Uri.parse(fbusername)
            startActivity(intent)
        }

        /******************************************************************************/
    }

    fun consumeWishCount(it: List<ItemData>?) {
        wishcount_bottom?.text = "" + it?.size
        leftMenuViewModel!!.wishListcount = it?.size!!
        invalidateOptionsMenu()
    }

    fun consumeCartCount(it: List<CartItemData>?) {
        cartcount_bottom?.text = "" + it?.size
        cartCount = it?.size!!
        invalidateOptionsMenu()
    }

    init {
        updateConfig(this)
    }

    fun updateConfig(wrapper: ContextThemeWrapper) {
        var dLocale = Locale(MagePrefs.getLanguage())
        Locale.setDefault(dLocale)
        val configuration = Configuration()
        configuration.setLocale(dLocale)
        wrapper.applyOverrideConfiguration(configuration)
    }

    fun setToggle() {
        mDrawerToggle = object : ActionBarDrawerToggle(
            this@NewBaseActivity,
            drawer_layout,
            toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        ) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                invalidateOptionsMenu()
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                invalidateOptionsMenu()
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
            }
        }
        mDrawerToggle!!.syncState()
    }

    protected fun showBackButton() {
        mDrawerToggle!!.isDrawerIndicatorEnabled = false
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        mDrawerToggle!!.toolbarNavigationClickListener = View.OnClickListener { onBackPressed() }
        drawer_layout!!.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        mDrawerToggle!!.onDrawerStateChanged(DrawerLayout.STATE_IDLE)
        mDrawerToggle!!.isDrawerIndicatorEnabled = false
        mDrawerToggle!!.syncState()
    }

    fun showHumburger() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        mDrawerToggle!!.isDrawerIndicatorEnabled = true
        mDrawerToggle!!.toolbarNavigationClickListener = null
    }

    protected fun showTittle(tittle: String) {
        Objects.requireNonNull<MageNativeTextView>(tooltext).visibility = View.VISIBLE
        Objects.requireNonNull<ImageView>(toolimage).visibility = View.GONE
        tooltext!!.text = tittle
    }

    override fun onFragmentInteraction(view: View) {}
//    private fun consumeResponse(reponse: ApiResponse) {
//        when (reponse.status) {
//            Status.SUCCESS -> LeftMenu.renderSuccessResponse(reponse.data!!)
//            Status.ERROR -> {
//                reponse.error!!.printStackTrace()
//                //  showToast(resources.getString(R.string.errorString))
//            }
//            else -> {
//            }
//        }
//    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    fun getCurrency() {
        leftMenuViewModel!!.currencyResponse()
            .observe(this, Observer<List<Storefront.CurrencyCode>> { this.preparePopUp(it) })
        leftMenuViewModel!!.message.observe(this, Observer<String> { this.showToast(it) })
    }

    fun showLanguageDialog() {
        languages = hashMapOf()
        languages?.put("English", "en")
        languages?.put("Arabic", "ar")
        val dialog = Dialog(this, R.style.WideDialog)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        val binding = DataBindingUtil.inflate<LanguageDialogBinding>(
            layoutInflater,
            R.layout.language_dialog,
            null,
            false
        )
        languageListAdapter.setData(
            languages?.keys?.toMutableList(),
            object : LanguageListAdapter.LanguageCallback {
                override fun selectedLanguage(language: String) {
                    MagePrefs.setLanguage(languages?.get(language)!!)
                    dialog.dismiss()
                    val intent = Intent(this@NewBaseActivity, Splash::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    Constant.activityTransition(this@NewBaseActivity)
                }
            })
        binding?.backButton?.setOnClickListener {
            dialog.dismiss()
        }
        binding.languageList.adapter = languageListAdapter
        dialog.setContentView(binding.root)
        drawer_layout.closeDrawers()
        dialog.show()
    }

    private fun preparePopUp(currencyCodes: List<Storefront.CurrencyCode>) {
        if (listDialog == null) {
            showPopUp(currencyCodes)
        } else {
            if (!listDialog!!.isShowing) {
                showPopUp(currencyCodes)
            }
        }
    }

    private fun showPopUp(enabledPresentmentCurrencies: List<Storefront.CurrencyCode>) {
        try {
            listDialog = BottomSheetDialog(this, R.style.WideDialogFull)
            var currencyBinding = DataBindingUtil.inflate<CurrencyListLayoutBinding>(
                LayoutInflater.from(this),
                R.layout.currency_list_layout,
                null,
                false
            )
            listDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
            listDialog?.setContentView(currencyBinding.root)
            currencyBinding.currencyList.layoutManager = LinearLayoutManager(this)
            recylerAdapter!!.setData(enabledPresentmentCurrencies, this@NewBaseActivity)
            currencyBinding.currencyList.adapter = recylerAdapter
            currencyBinding.closeBut.setOnClickListener {
                listDialog?.dismiss()
            }
            drawer_layout.closeDrawers()
            listDialog?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setLayout(view: RecyclerView, orientation: String): RecyclerView {
        view.setHasFixedSize(true)
        view.isNestedScrollingEnabled = false
        view.itemAnimator = DefaultItemAnimator()
        val manager = LinearLayoutManager(this)
        when (orientation) {
            "horizontal" -> {
                manager.orientation = RecyclerView.HORIZONTAL
                view.layoutManager = manager
                if (view.itemDecorationCount == 0) {
                    view.addItemDecoration(GridSpacingItemDecoration(1, dpToPx(1), true))
                }
            }
            "vertical" -> {
                manager.orientation = RecyclerView.VERTICAL
                view.layoutManager = manager
                if (view.itemDecorationCount == 0) {
                    view.addItemDecoration(GridSpacingItemDecoration(1, dpToPx(2), true))
                }
            }
            "grid" -> {
                view.layoutManager = GridLayoutManager(this, 2)
                if (view.itemDecorationCount == 0) {
                    view.addItemDecoration(GridSpacingItemDecoration(2, dpToPx(0), true))
                }
            }
            "3grid" -> {
                view.layoutManager = GridLayoutManager(this, 3)
                if (view.itemDecorationCount == 0) {
                    view.addItemDecoration(GridSpacingItemDecoration(3, dpToPx(4), true))
                }
            }
            "4grid" -> {
                view.layoutManager = GridLayoutManager(this, 4)
                if (view.itemDecorationCount == 0) {
                    view.addItemDecoration(GridSpacingItemDecoration(4, dpToPx(6), true))
                }
            }
            "customisablegrid" -> {
                view.layoutManager = GridLayoutManager(this, 3)
//                if (view.itemDecorationCount == 0) {
//                    view.addItemDecoration(GridSpacingItemDecoration(3, dpToPx(4), true))
//                }
            }
        }
        return view
    }

    fun closePopUp() {
        listDialog!!.dismiss()
    }

    private fun dpToPx(dp: Int): Int {
        val r = resources
        return Math.round(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp.toFloat(),
                r.displayMetrics
            )
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.m_search, menu)
        item = menu.findItem(R.id.search_item)
        wishitem = menu.findItem(R.id.wish_item)
        cartitem = menu.findItem(R.id.cart_item)
        item?.setActionView(R.layout.m_search)
        wishitem.setActionView(R.layout.m_wishcount)
        cartitem.setActionView(R.layout.m_count)
        val search = item?.actionView
        if (this@NewBaseActivity !is HomePage) {
            search.visibility = View.GONE
            item.isVisible = false
        }
        search?.setOnClickListener {
            val searchpage = Intent(this@NewBaseActivity, AutoSearch::class.java)
            startActivity(searchpage)
            Constant.activityTransition(this)
        }
        val notifCount = cartitem.actionView
        textView = notifCount.findViewById<TextView>(R.id.count)
        textView!!.text = "" + cartCount
        notifCount.setOnClickListener {
            val mycartlist = Intent(this, CartList::class.java)
            startActivity(mycartlist)
            Constant.activityTransition(this)
        }
        val wishcount = wishitem.actionView
        wishtextView = wishcount.findViewById<TextView>(R.id.count)
        wishtextView!!.text = "" + leftMenuViewModel!!.wishListcount
        wishcount.setOnClickListener {
            val wishlist = Intent(this, WishList::class.java)
            startActivity(wishlist)
            Constant.activityTransition(this)
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        cartCount = leftMenuViewModel!!.cartCount
        MyApplication.dataBaseReference = MyApplication.getmFirebaseSecondanyInstance()
            .getReference(Urls(MyApplication.context).shopdomain.replace(".myshopify.com", ""))
    }

    fun setSearchOption(type: String, placeholder: String) {
        when (type) {
            "middle-width-search" -> {
                item?.setVisible(false)
                toolimage.visibility = View.GONE
                searchsection.visibility = View.VISIBLE
                search.text = placeholder
                search.setOnClickListener {
                    val searchpage = Intent(this@NewBaseActivity, AutoSearch::class.java)
                    startActivity(searchpage)
                    Constant.activityTransition(this)
                }
            }
            "full-width-search" -> {
                item?.setVisible(false)
                toolimage.visibility = View.VISIBLE
                searchsection.visibility = View.GONE
            }
            else -> {
                item?.setVisible(true)
                toolimage.visibility = View.VISIBLE
                searchsection.visibility = View.GONE
            }
        }
    }

    fun setWishList(visiblity: String) {
        when (visiblity) {
            "1" -> {
                wishitem.setVisible(true)
            }
            else -> {
                wishitem.setVisible(false)
            }
        }
    }

    fun setLogoImage(url: String) {
        if (!this.isDestroyed) {
            Log.i("MageNative", "Image URL" + url)
            Glide.with(this)
                .load(url)
                .thumbnail(0.5f)
                .apply(
                    RequestOptions().placeholder(R.drawable.image_placeholder)
                        .error(R.drawable.image_placeholder).dontTransform()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                )
                .into(toolimage)
        }
    }

    fun setPanelBackgroundColor(color: String) {
        toolbar.setBackgroundColor(Color.parseColor(color.toUpperCase()))
    }

    fun setIconColors(countback: String, counttext: String, iconcolor: String) {
        val wishview = wishitem.actionView
        val cartview = cartitem.actionView
        val searchview = item?.actionView
        val wishrelative = wishview.findViewById<RelativeLayout>(R.id.back)
        val wishtext = wishview.findViewById<TextView>(R.id.count)
        val wishicon = wishview.findViewById<FontTextView>(R.id.cart_icon)
        val cartrelative = cartview.findViewById<RelativeLayout>(R.id.back)
        val carttext = cartview.findViewById<TextView>(R.id.count)
        val carticon = cartview.findViewById<FontTextView>(R.id.cart_icon)
        wishrelative.backgroundTintList = ColorStateList.valueOf(Color.parseColor(countback))
        cartrelative.backgroundTintList = ColorStateList.valueOf(Color.parseColor(countback))
        wishtext.setTextColor(Color.parseColor(counttext))
        carttext.setTextColor(Color.parseColor(counttext))
        wishicon.setTextColor(Color.parseColor(iconcolor))
        carticon.setTextColor(Color.parseColor(iconcolor))
        val searchicon = searchview?.findViewById<FontTextView>(R.id.search_icon)
        searchicon?.setTextColor(Color.parseColor(iconcolor))
        mDrawerToggle!!.getDrawerArrowDrawable().setColor(Color.parseColor(iconcolor));
    }

    fun setSearchOptions(searchback: String, searchtext: String, searhcborder: String) {
        var draw: GradientDrawable = search.background as GradientDrawable
        draw.setColor(Color.parseColor(searchback))
        search.setTextColor(Color.parseColor(searchtext))
        search.setHintTextColor(Color.parseColor(searchtext))
        draw.setStroke(2, Color.parseColor(searhcborder));
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(
                    this,
                    "" + resources.getString(R.string.noresultfound),
                    Toast.LENGTH_LONG
                ).show()
                finish()
            } else {
                when (result.formatName) {
                    "QR_CODE" -> {
                        try {
                            AESEnDecryption().data()
                            var json = JSONObject(result.contents)

                            if (json.has("mid")) {
                                Log.i("MageNative", "Barcode" + result)
                                Log.i("MageNative", "Barcode" + result.contents)
                                leftMenuViewModel!!.deletLocal()
                                leftMenuViewModel!!.insertPreviewData(json)
                                leftMenuViewModel!!.logOut()
                                var intent = Intent(this, Splash::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                Constant.activityTransition(this)
                            }
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
