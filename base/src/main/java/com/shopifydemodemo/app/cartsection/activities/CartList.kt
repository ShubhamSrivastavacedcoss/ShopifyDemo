package com.shopifydemodemo.app.cartsection.activities

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.*
import androidx.constraintlayout.widget.Constraints
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.shopify.buy3.Storefront
import com.shopifydemodemo.app.MyApplication
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.basesection.activities.NewBaseActivity
import com.shopifydemodemo.app.basesection.viewmodels.SplashViewModel
import com.shopifydemodemo.app.cartsection.adapters.CartListAdapter
import com.shopifydemodemo.app.cartsection.adapters.LocationListAdapter
import com.shopifydemodemo.app.cartsection.models.CartBottomData
import com.shopifydemodemo.app.cartsection.viewmodels.CartListViewModel
import com.shopifydemodemo.app.checkoutsection.activities.CheckoutWeblink
import com.shopifydemodemo.app.customviews.MageNativeButton
import com.shopifydemodemo.app.databinding.DiscountCodeLayoutBinding
import com.shopifydemodemo.app.databinding.MCartlistBinding
import com.shopifydemodemo.app.personalised.adapters.PersonalisedAdapter
import com.shopifydemodemo.app.personalised.viewmodels.PersonalisedViewModel
import com.shopifydemodemo.app.sharedprefsection.MagePrefs
import com.shopifydemodemo.app.utils.*
import com.shopifydemodemo.app.wishlistsection.activities.WishList
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.android.synthetic.main.m_cartlist.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class CartList : NewBaseActivity(), DatePickerDialog.OnDateSetListener, OnMapReadyCallback {
    @Inject
    lateinit var factory: ViewModelFactory
    private var cartlist: RecyclerView? = null
    private var model: CartListViewModel? = null
    private var personamodel: PersonalisedViewModel? = null
    private var count: Int = 1
    private val TAG = "CartList"
    lateinit var delivery_param: HashMap<String, String>
    lateinit var response_data: Storefront.Checkout
    private var marker: Marker? = null
    val mincalender = Calendar.getInstance()
    val maxcalender = Calendar.getInstance()
    var calender = Calendar.getInstance()
    val year = calender.get(Calendar.YEAR)
    val month = calender.get(Calendar.MONTH)
    val day = calender.get(Calendar.DAY_OF_MONTH)
    lateinit var dpd: DatePickerDialog
    var simpleDateFormat: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
    var dayFormat: SimpleDateFormat = SimpleDateFormat("EEEE", Locale.ENGLISH)
    var disabledates: ArrayList<Calendar>? = null
    lateinit var slots: JsonArray
    lateinit var locations: JsonArray
    lateinit var daysOfWeek: JsonObject
    lateinit var localdelivery_slots: ArrayList<String>
    var interval: Int = 0
    var selected_delivery: String = "delivery"
    var selected_slot: String? = null
    private lateinit var mMap: GoogleMap
    private var custom_attribute: JSONObject = JSONObject()
    private var grandTotal: String? = null
    private var cartWarning: HashMap<String, Boolean>? = hashMapOf()

    @Inject
    lateinit var locationAdapter: LocationListAdapter

    @Inject
    lateinit var adapter: CartListAdapter

    @Inject
    lateinit var personalisedadapter: PersonalisedAdapter

    @Inject
    lateinit var padapter: PersonalisedAdapter
    private var binding: MCartlistBinding? = null
    var discountcode: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val group = findViewById<ViewGroup>(R.id.container)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.m_cartlist, group, true)
        cartlist = setLayout(binding!!.cartlist, "vertical")
        cartlist!!.isNestedScrollingEnabled = false
        showTittle(resources.getString(R.string.yourcart))
        showBackButton()
        (application as MyApplication).mageNativeAppComponent!!.doCartListActivityInjection(this)
        model = ViewModelProvider(this, factory).get(CartListViewModel::class.java)
        model!!.context = this
        personamodel = ViewModelProvider(this, factory).get(PersonalisedViewModel::class.java)
        personamodel?.activity = this
        model!!.Response().observe(this, Observer<Storefront.Checkout> { this.consumeResponse(it) })
        binding!!.locationList.layoutManager = LinearLayoutManager(this)
        if (SplashViewModel.featuresModel.ai_product_reccomendaton) {
            if (Constant.ispersonalisedEnable) {
                model!!.getApiResponse()
                    .observe(this, Observer<ApiResponse> { this.consumeResponse(it) })
                model!!.getYouMAyAPiResponse()
                    .observe(this, Observer<ApiResponse> { this.Response(it) })
            }
        }
        model!!.DeliveryStatus(Urls(application as MyApplication).mid)
            .observe(this, Observer { this.DeliveryStatus(it) })
        model!!.message.observe(this, Observer<String> { this.showToast(it) })
        model!!.getGiftCard()
            .observe(this, Observer<Storefront.Mutation> { this.consumeResponseGift(it) })
        model!!.getGiftCardRemove()
            .observe(this, Observer<Storefront.Mutation> { this.consumeResponseGiftRemove(it) })
        model!!.getDiscount()
            .observe(this, Observer<Storefront.Mutation> { this.consumeResponseDiscount(it) })


        binding!!.subtotaltext.textSize = 12f
        binding!!.subtotal.textSize = 12f
        binding!!.taxtext.textSize = 12f
        binding!!.tax.textSize = 12f
        binding!!.proceedtocheck.textSize = 13f
        binding!!.handler = ClickHandler()
    }


    private fun consumeResponseDiscount(it: Storefront.Mutation?) {
        Log.d(TAG, "consumeResponseDiscount: " + it!!.checkoutDiscountCodeApplyV2)
        try {
            val bottomData = CartBottomData()
            bottomData.checkoutId = it.checkoutDiscountCodeApplyV2.checkout.id
            Log.d(TAG, "setBottomData: " + bottomData.checkoutId)
            bottomData.subtotaltext =
                resources.getString(R.string.subtotaltext) + " ( " + model!!.cartCount + " items )"
            bottomData.subtotal = CurrencyFormatter.setsymbol(
                it.checkoutDiscountCodeApplyV2.checkout.subtotalPriceV2.amount,
                it.checkoutDiscountCodeApplyV2.checkout.subtotalPriceV2.currencyCode.toString()
            )
            if (it.checkoutDiscountCodeApplyV2.checkout.taxExempt!!) {
                binding!!.taxtext.visibility = View.VISIBLE
                binding!!.tax.visibility = View.VISIBLE
                bottomData.tax = CurrencyFormatter.setsymbol(
                    it.checkoutDiscountCodeApplyV2.checkout.totalTaxV2.amount,
                    it.checkoutDiscountCodeApplyV2.checkout.totalTaxV2.currencyCode.toString()
                )
            }
            bottomData.grandtotal = CurrencyFormatter.setsymbol(
                it.checkoutDiscountCodeApplyV2.checkout.totalPriceV2.amount,
                it.checkoutDiscountCodeApplyV2.checkout.totalPriceV2.currencyCode.toString()
            )
            bottomData.checkouturl = it.checkoutDiscountCodeApplyV2.checkout.webUrl
            binding!!.bottomdata = bottomData
            binding!!.root.visibility = View.VISIBLE
            try {
                if (model!!.isLoggedIn) {
                    Log.i("herer", " " + bottomData.checkoutId)
                    Log.i("herer", "token : " + model?.customeraccessToken?.customerAccessToken)
                    model?.associatecheckout(
                        bottomData.checkoutId,
                        model!!.customeraccessToken.customerAccessToken
                    )
                    model?.getassociatecheckoutResponse()
                        ?.observe(this@CartList, Observer { ClickHandler().getResonse(it) })
                } else {

                    val intent = Intent(this, CheckoutWeblink::class.java)
                    intent.putExtra("link", bottomData.checkouturl)
                    intent.putExtra("id", bottomData.checkoutId)
                    startActivity(intent)
                    Constant.activityTransition(this)
                    //    ClickHandler().showPopUp(bottomData)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun consumeResponseGiftRemove(it: Storefront.Mutation?) {
        binding!!.applyGiftBut.text = getString(R.string.apply)
        val bottomData = CartBottomData()
        bottomData.checkoutId = it!!.checkoutGiftCardRemoveV2.checkout.id
        Log.d(TAG, "setBottomData: " + bottomData.checkoutId)
        bottomData.subtotaltext =
            resources.getString(R.string.subtotaltext) + " ( " + model!!.cartCount + " items )"
        bottomData.subtotal = CurrencyFormatter.setsymbol(
            it.checkoutGiftCardRemoveV2.checkout.subtotalPriceV2.amount,
            it.checkoutGiftCardRemoveV2.checkout.subtotalPriceV2.currencyCode.toString()
        )
        if (it.checkoutGiftCardRemoveV2.checkout.taxExempt!!) {
            binding!!.taxtext.visibility = View.VISIBLE
            binding!!.tax.visibility = View.VISIBLE
            bottomData.tax = CurrencyFormatter.setsymbol(
                it.checkoutGiftCardRemoveV2.checkout.totalTaxV2.amount,
                it.checkoutGiftCardRemoveV2.checkout.totalTaxV2.currencyCode.toString()
            )
        }
        bottomData.grandtotal = CurrencyFormatter.setsymbol(
            it.checkoutGiftCardRemoveV2.checkout.totalPriceV2.amount,
            it.checkoutGiftCardRemoveV2.checkout.totalPriceV2.currencyCode.toString()
        )
        bottomData.checkouturl = it.checkoutGiftCardRemoveV2.checkout.webUrl
        binding!!.bottomdata = bottomData
        binding!!.root.visibility = View.VISIBLE
        showToast(getString(R.string.gift_remove))
    }

    private fun consumeResponseGift(it: Storefront.Mutation?) {
        binding!!.applyGiftBut.text = getString(R.string.remove)
        val bottomData = CartBottomData()
        bottomData.giftcardID = it!!.checkoutGiftCardsAppend.checkout.appliedGiftCards[0].id
        bottomData.checkoutId = it.checkoutGiftCardsAppend.checkout.id
        Log.d(TAG, "setBottomData: " + bottomData.checkoutId)
        bottomData.subtotaltext =
            resources.getString(R.string.subtotaltext) + " ( " + model!!.cartCount + " items )"
        bottomData.subtotal = CurrencyFormatter.setsymbol(
            (it.checkoutGiftCardsAppend.checkout.subtotalPriceV2.amount.toDouble() - it.checkoutGiftCardsAppend.checkout.appliedGiftCards[0].amountUsedV2.amount.toDouble()).toString(),
            it.checkoutGiftCardsAppend.checkout.subtotalPriceV2.currencyCode.toString()
        )
        if (it.checkoutGiftCardsAppend.checkout.taxExempt!!) {
            binding!!.taxtext.visibility = View.VISIBLE
            binding!!.tax.visibility = View.VISIBLE
            bottomData.tax = CurrencyFormatter.setsymbol(
                it.checkoutGiftCardsAppend.checkout.totalTaxV2.amount,
                it.checkoutGiftCardsAppend.checkout.totalTaxV2.currencyCode.toString()
            )
        }
        bottomData.grandtotal = CurrencyFormatter.setsymbol(
            (it.checkoutGiftCardsAppend.checkout.totalPriceV2.amount.toDouble() - it.checkoutGiftCardsAppend.checkout.appliedGiftCards[0].amountUsedV2.amount.toDouble()).toString(),
            it.checkoutGiftCardsAppend.checkout.totalPriceV2.currencyCode.toString()
        )
        bottomData.checkouturl = it.checkoutGiftCardsAppend.checkout.webUrl
        binding!!.bottomdata = bottomData
        binding!!.root.visibility = View.VISIBLE
        showToast(getString(R.string.gift_success))
    }

    private fun showToast(msg: String) {
        Toast.makeText(this@CartList, msg, Toast.LENGTH_LONG).show()
    }

    private fun consumeResponse(reponse: Storefront.Checkout) {
        if (reponse.lineItems.edges.size > 0) {
            showTittle(resources.getString(R.string.yourcart) + " ( " + reponse.lineItems.edges.size + " items )")
            if (adapter.data != null) {
                adapter.data = reponse.lineItems.edges
                adapter.notifyDataSetChanged()
            } else {
                adapter.setData(
                    reponse.lineItems.edges,
                    model,
                    this,
                    object : CartListAdapter.StockCallback {
                        override fun cartWarning(warning: HashMap<String, Boolean>) {
                            cartWarning = warning
                        }
                    })
                cartlist!!.adapter = adapter
            }
            setBottomData(reponse)
            delivery_param = model!!.fillDeliveryParam(reponse.lineItems.edges)
            response_data = reponse
            if (SplashViewModel.featuresModel.zapietEnable) {

                binding!!.zepietSection.visibility = View.VISIBLE

            } else {
                binding!!.zepietSection.visibility = View.GONE
            }

            invalidateOptionsMenu()
        } else {
            showToast(resources.getString(R.string.emptycart))
            finish()
        }
    }

    private fun hideload(pinalertDialog: SweetAlertDialog) {
        Handler().postDelayed({
            pinalertDialog.dismiss()
        }, 4000)
    }

    private fun showload(view: View) {
        var pinalertDialog = SweetAlertDialog(this@CartList, SweetAlertDialog.NORMAL_TYPE)
        pinalertDialog.titleText = view.context?.getString(R.string.note)
        pinalertDialog.contentText = view.context?.getString(R.string.loadings)
        pinalertDialog.show()
        hideload(pinalertDialog)
    }

    private fun checkzip() {
        if (!zipcodes.text.toString().isEmpty()) {

            model!!.validateDelivery(delivery_param).observe(
                this@CartList,
                Observer { this@CartList.validate_delivery(it, response_data.lineItems.edges) })

        }
    }

    private fun validate_delivery(
        response: ApiResponse?,
        edges: List<Storefront.CheckoutLineItemEdge>
    ) {
        try {
            Log.d(TAG, "validate_delivery: " + response!!.data)
            if (response.data != null) {
                var res = response.data
                if (res?.asJsonObject!!.has("productsEligible")) {
                    if (res.asJsonObject.get("success").asBoolean && res.asJsonObject.get("productsEligible").asBoolean) {
                        binding!!.zepietSection.visibility = View.VISIBLE
                        var local_delivery_param =
                            model!!.fillLocalDeliveryParam(edges, binding!!.zipcodes)
                        Log.d(TAG, "validate_delivery: " + local_delivery_param)

                        model!!.localDeliveryy(local_delivery_param)
                            .observe(this, Observer { this.localDelivery(it) })
                    } else {
                        binding!!.zepietSection.visibility = View.GONE
                        binding!!.bottomsection.visibility = View.VISIBLE
                    }
                } else {
                    showToast(resources.getString(R.string.noeligibility))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun storeDelivery(it: ApiResponse?) {

        /*if (customLoader!!.isShowing) {
            customLoader!!.dismiss()
        }*/
        try {
            if (it!!.data != null) {
                binding!!.deliveryDateTxt.visibility = View.VISIBLE
                var res = it.data
                if (res!!.asJsonObject.get("success").asBoolean == true) {
                    var calendar = res.asJsonObject.getAsJsonObject("calendar")
                    var disabled = calendar.getAsJsonArray("disabled")
                    locations = res.asJsonObject.getAsJsonArray("locations")
                    if (locations.size() > 0) {
                        custom_attribute.put(
                            "Pickup-Location-Id",
                            locations.get(0).asJsonObject.get("id").asString
                        )
                        custom_attribute.put(
                            "Pickup-Location-Company",
                            locations.get(0).asJsonObject.get("company_name").asString
                        )
                        custom_attribute.put(
                            "Pickup-Location-Address-Line-1",
                            locations.get(0).asJsonObject.get("address_line_1").asString
                        )
                        custom_attribute.put(
                            "Pickup-Location-Address-Line-2",
                            locations.get(0).asJsonObject.get("address_line_2").asString
                        )
                        custom_attribute.put(
                            "Pickup-Location-City",
                            locations.get(0).asJsonObject.get("city").asString
                        )
                        custom_attribute.put(
                            "Pickup-Location-Region",
                            locations.get(0).asJsonObject.get("region").asString
                        )
                        custom_attribute.put(
                            "Pickup-Location-Postal-Code",
                            locations.get(0).asJsonObject.get("postal_code").asString
                        )
                        custom_attribute.put(
                            "Pickup-Location-Country",
                            locations.get(0).asJsonObject.get("country").asString
                        )

                        locationAdapter.setData(
                            locations,
                            itemClick = object : LocationListAdapter.ItemClick {
                                override fun selectLocation(location_item: JsonObject) {
                                    custom_attribute.put(
                                        "Pickup-Location-Id",
                                        location_item.get("id").asString
                                    )
                                    custom_attribute.put(
                                        "Pickup-Location-Company",
                                        location_item.get("company_name").asString
                                    )
                                    custom_attribute.put(
                                        "Pickup-Location-Address-Line-1",
                                        location_item.get("address_line_1").asString
                                    )
                                    custom_attribute.put(
                                        "Pickup-Location-Address-Line-2",
                                        location_item.get("address_line_2").asString
                                    )
                                    custom_attribute.put(
                                        "Pickup-Location-City",
                                        location_item.get("city").asString
                                    )
//
//                                    custom_attribute.put("Pickup-Location-Region", location_item.get("region").asString)
//

                                    custom_attribute.put(
                                        "Pickup-Location-Postal-Code",
                                        location_item.get("postal_code").asString
                                    )
                                    custom_attribute.put(
                                        "Pickup-Location-Country",
                                        location_item.get("country").asString
                                    )
                                    //val sydney = LatLng(location_item.get("latitude").asDouble, location_item.get("longitude").asDouble)
                                    /*if (marker == null) {
                                        var markerOptions = MarkerOptions().position(sydney)
                                            .title("I am here!")
                                            .icon(
                                                BitmapDescriptorFactory.defaultMarker(
                                                    BitmapDescriptorFactory.HUE_MAGENTA));
                                        //marker = mMap.addMarker(markerOptions);
                                    } else {
                                        marker!!.setPosition(sydney);
                                    }
                                    mMap.animateCamera(CameraUpdateFactory.newLatLng(sydney))
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18f))*/
                                }
                            })
                        binding!!.locationList.adapter = locationAdapter
                    }
                    daysOfWeek = calendar.getAsJsonObject("daysOfWeek")
                    interval = calendar.get("interval").asInt
                    loadCalendar(calendar, disabled)
                } else {
                    Toast.makeText(
                        this,
                        res.asJsonObject.get("err_msg").asString,
                        Toast.LENGTH_SHORT
                    ).show()
                    binding?.deliveryOption?.visibility = View.GONE
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun DeliveryStatus(it: ApiResponse?) {

        if (it!!.data != null) {
            val res = it.data
            if (res!!.asJsonObject.get("is_installed").asBoolean == true) {
                val note =
                    res.asJsonObject.get("data").asJsonObject.get("translations").asJsonObject.get(
                        "shipping"
                    ).asJsonObject.get("note").asString

                binding?.shippingContainer?.setOnClickListener {
                    binding?.shipnote?.text = note.toString()
                    binding?.shipnote?.visibility = View.VISIBLE
                    binding!!.deliveryTimeSpn.visibility = View.GONE
                    binding!!.deliverAreaTxt.visibility = View.GONE
                    binding!!.deliveryDateTxt.visibility = View.GONE
                    binding!!.zipcode.visibility = View.GONE
                    binding!!.locationList.visibility = View.GONE
                    binding!!.pintext.visibility = View.GONE


                }

            }

        } else {
            showToast(resources.getString(R.string.noshipping))
        }


    }

    private fun localDelivery(it: ApiResponse?) {
        try {
            /* if (customLoader!!.isShowing) {
                 customLoader!!.dismiss()
             }*/
            Log.i("ALLLLLDATAAAAA", "" + it!!.data)
            if (it.data != null) {
                binding!!.deliveryDateTxt.visibility = View.VISIBLE
                //binding!!.deliveryDateTxt.visibility = View.VISIBLE
                val res = it.data
                if (res!!.asJsonObject.get("success").asBoolean == true) {
                    binding!!.deliveryDateTxt.visibility = View.VISIBLE
                    binding!!.proceedtocheck.visibility = View.VISIBLE
                    binding!!.pintext.visibility = View.GONE
                    binding!!.pintextrue.visibility = View.VISIBLE
                    val calendar = res.asJsonObject.getAsJsonObject("calendar")
                    val disabled = calendar.getAsJsonArray("disabled")
                    slots = calendar.getAsJsonArray("slots")
                    loadCalendar(calendar, disabled)
                } else if (res.asJsonObject.get("success").asBoolean == false) {
                    binding!!.deliveryDateTxt.visibility = View.GONE
                    binding!!.pintext.visibility = View.VISIBLE
                    binding!!.proceedtocheck.visibility = View.GONE
                    binding!!.pintextrue.visibility = View.GONE
                    binding!!.deliveryTimeSpn.visibility = View.GONE
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun loadCalendar(calendar: JsonObject?, disabled: JsonArray?) {
        disabledates = ArrayList<Calendar>()
        dpd = DatePickerDialog.newInstance(
            this,
            year, // Initial year selection
            month, // Initial month selection
            day
        )
        dpd.locale = Locale.getDefault()
        dpd.isThemeDark = false
        dpd.showYearPickerFirst(false)
        dpd.version = DatePickerDialog.Version.VERSION_2
        var new_calendar: Calendar? = null
        for (j in 0..disabled!!.size() - 1) {
            if (disabled.isJsonArray) {
                if (disabled[j].toString().equals("2")) {
                    val weeks = 5
                    var i = 0
                    while (i < weeks * 7) {
                        new_calendar = Calendar.getInstance()
                        new_calendar.add(
                            Calendar.DAY_OF_YEAR,
                            (Calendar.MONDAY - new_calendar.get(Calendar.DAY_OF_WEEK) + i)
                        )
                        disabledates?.add(new_calendar)
                        i = i + 7
                    }

                } else if (disabled[j].toString().equals("3")) {
                    val weeks = 5
                    var i = 0
                    while (i < weeks * 7) {
                        new_calendar = Calendar.getInstance()
                        new_calendar.add(
                            Calendar.DAY_OF_YEAR,
                            (Calendar.TUESDAY - new_calendar.get(Calendar.DAY_OF_WEEK) + i)
                        )
                        disabledates?.add(new_calendar)
                        i = i + 7
                    }
                } else if (disabled[j].toString().equals("4")) {
                    val weeks = 5
                    var i = 0
                    while (i < weeks * 7) {
                        new_calendar = Calendar.getInstance()
                        new_calendar.add(
                            Calendar.DAY_OF_YEAR,
                            (Calendar.WEDNESDAY - new_calendar.get(Calendar.DAY_OF_WEEK) + i)
                        )
                        disabledates?.add(new_calendar)
                        i = i + 7
                    }
                } else if (disabled[j].toString().equals("5")) {
                    val weeks = 5
                    var i = 0
                    while (i < weeks * 7) {
                        new_calendar = Calendar.getInstance()
                        new_calendar.add(
                            Calendar.DAY_OF_YEAR,
                            (Calendar.THURSDAY - new_calendar.get(Calendar.DAY_OF_WEEK) + i)
                        )
                        disabledates?.add(new_calendar)
                        i = i + 7
                    }
                } else if (disabled[j].toString().equals("6")) {
                    val weeks = 5
                    var i = 0
                    while (i < weeks * 7) {
                        new_calendar = Calendar.getInstance()
                        new_calendar.add(
                            Calendar.DAY_OF_YEAR,
                            (Calendar.FRIDAY - new_calendar.get(Calendar.DAY_OF_WEEK) + i)
                        )
                        disabledates?.add(new_calendar)
                        i = i + 7
                    }
                } else if (disabled[j].toString().equals("7")) {
                    val weeks = 5
                    var i = 0
                    while (i < weeks * 7) {
                        new_calendar = Calendar.getInstance()
                        new_calendar.add(
                            Calendar.DAY_OF_YEAR,
                            (Calendar.SATURDAY - new_calendar.get(Calendar.DAY_OF_WEEK) + i)
                        )
                        disabledates?.add(new_calendar)
                        i = i + 7
                    }
                } else if (disabled[j].toString().equals("1")) {
                    val weeks = 5
                    var i = 0
                    while (i < weeks * 7) {
                        new_calendar = Calendar.getInstance()
                        new_calendar.add(
                            Calendar.DAY_OF_YEAR,
                            Calendar.SUNDAY - new_calendar.get(Calendar.DAY_OF_WEEK) + 7 + i
                        )
                        disabledates?.add(new_calendar)
                        i = i + 7
                    }
                }
            }
        }
        val maxDate = ""
        val minDate = calendar!!.get("minDate").asString?.split("-")
        val disabledDays1: Array<Calendar> =
            disabledates?.toArray(arrayOfNulls<Calendar>(disabledates?.size!!)) as Array<Calendar>
        dpd.disabledDays = disabledDays1
        mincalender.set(Calendar.YEAR, minDate!![0].toInt())
        mincalender.set(Calendar.MONTH, minDate[1].toInt() - 1)
        mincalender.set(Calendar.DAY_OF_MONTH, minDate[2].toInt())
        dpd.minDate = mincalender

        maxcalender.set(Calendar.YEAR, maxDate[0].toInt())
        maxcalender.set(Calendar.MONTH, maxDate[1].toInt() - 1)
        maxcalender.set(Calendar.DAY_OF_MONTH, maxDate[2].toInt())
        dpd.maxDate = maxcalender

    }

    private fun consumeResponse(reponse: ApiResponse) {
        when (reponse.status) {
            Status.SUCCESS -> setPersonalisedData(reponse.data!!)
            Status.ERROR -> {
                reponse.error!!.printStackTrace()
                showToast(resources.getString(R.string.errorString))
            }
        }
    }

    private fun Response(reponse: ApiResponse) {
        when (reponse.status) {
            Status.SUCCESS -> setYouMayPersonalisedData(reponse.data!!)
            Status.ERROR -> {
                reponse.error!!.printStackTrace()
                showToast(resources.getString(R.string.errorString))
            }
        }
    }

    private fun setPersonalisedData(data: JsonElement) {
        try {
            val jsondata = JSONObject(data.toString())
            if (jsondata.has("query1")) {
                binding!!.personalisedsection.visibility = View.VISIBLE
                setLayout(binding!!.personalised, "horizontal")
                personamodel!!.setPersonalisedData(
                    jsondata.getJSONObject("query1").getJSONArray("products"),
                    personalisedadapter,
                    model!!.presentCurrency,
                    binding!!.personalised
                )
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun setYouMayPersonalisedData(data: JsonElement) {
        try {
            val jsondata = JSONObject(data.toString())
            if (jsondata.has("query1")) {
                binding!!.personalisedsection2.visibility = View.VISIBLE
                setLayout(binding!!.personalised2, "horizontal")
                personamodel!!.setPersonalisedData(
                    jsondata.getJSONObject("query1").getJSONArray("products"),
                    padapter,
                    model!!.presentCurrency,
                    binding!!.personalised2
                )
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun setBottomData(checkout: Storefront.Checkout) {
        try {
            val bottomData = CartBottomData()
            bottomData.checkoutId = checkout.id
            Log.d(TAG, "setBottomData: " + bottomData.checkoutId)
            bottomData.subtotaltext =
                resources.getString(R.string.subtotaltext) + " ( " + model!!.cartCount + " items )"
            bottomData.subtotal = CurrencyFormatter.setsymbol(
                checkout.subtotalPriceV2.amount,
                checkout.subtotalPriceV2.currencyCode.toString()
            )
            if (checkout.taxExempt!!) {
                binding!!.taxtext.visibility = View.VISIBLE
                binding!!.tax.visibility = View.VISIBLE
                bottomData.tax = CurrencyFormatter.setsymbol(
                    checkout.totalTaxV2.amount,
                    checkout.totalTaxV2.currencyCode.toString()
                )
            }
            bottomData.grandtotal = CurrencyFormatter.setsymbol(
                checkout.totalPriceV2.amount,
                checkout.totalPriceV2.currencyCode.toString()
            )
            MagePrefs.setGrandTotal(bottomData.grandtotal ?: "")
            grandTotal = checkout.totalPriceV2.amount
            bottomData.checkouturl = checkout.webUrl
            binding!!.bottomdata = bottomData
            binding!!.root.visibility = View.VISIBLE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.m_wish, menu)
        val item = menu.findItem(R.id.wish_item)
        item.setActionView(R.layout.m_wishcount)
        val notifCount = item.actionView
        val textView = notifCount.findViewById<TextView>(R.id.count)
        textView.text = "" + model!!.wishListcount
        notifCount.setOnClickListener {
            val mycartlist = Intent(this@CartList, WishList::class.java)
            startActivity(mycartlist)
            Constant.activityTransition(this)
        }
        return true
    }


    override fun onResume() {
        super.onResume()
        if (model!!.cartCount > 0) {
            model!!.prepareCart()
        } else {
            showToast(resources.getString(R.string.emptycart))
            finish()
        }
        invalidateOptionsMenu()
        count = 1
    }

    inner class ClickHandler {
        fun loadCheckout(view: View, data: CartBottomData) {
            Log.d(TAG, "loadCheckout: " + cartWarning?.values)
            if (cartWarning?.values?.contains(true) == true) {
                var alertDialog = SweetAlertDialog(this@CartList, SweetAlertDialog.WARNING_TYPE)
                alertDialog.titleText = view.context?.getString(R.string.warning_message)
                alertDialog.contentText = view.context?.getString(R.string.cart_warning)
                alertDialog.confirmText = view.context?.getString(R.string.dialog_ok)
                alertDialog.setConfirmClickListener { sweetAlertDialog ->
                    sweetAlertDialog.dismissWithAnimation()
                }
                alertDialog.show()
            } else {
                showApplyCouponDialog(data)
            }
        }

        fun loadpincode(view: View) {
            showload(view)
            checkzip()
        }

        fun payWithGpay(view: View, data: CartBottomData) {
            val idempotencyKey = UUID.randomUUID().toString()
            val billingAddressInput: Storefront.MailingAddressInput =
                Storefront.MailingAddressInput()
            billingAddressInput.address1 = "3/446 Gomti Nagar Vishvash Khand Lucknow"
            billingAddressInput.address2 = "3/446 Gomti Nagar Vishvash Khand Lucknow"
            billingAddressInput.city = "Lucknow"
            billingAddressInput.company = ""
            billingAddressInput.country = "India"
            billingAddressInput.firstName = "Abhishek"
            billingAddressInput.lastName = "Dubey"
            billingAddressInput.zip = "226010"

            model?.doGooglePay(data.checkoutId, "100", idempotencyKey, billingAddressInput)
        }

        fun applyGiftCard(view: View, bottomData: CartBottomData) {
            if ((view as MageNativeButton).text == getString(R.string.apply)) {
                if (TextUtils.isEmpty(binding!!.giftcardEdt.text.toString().trim())) {
                    binding!!.giftcardEdt.error = getString(R.string.giftcard_validation)
                } else {
                    model!!.applyGiftCard(
                        binding!!.giftcardEdt.text.toString().trim(),
                        bottomData.checkoutId
                    )
                }
            } else if (view.text == getString(R.string.remove)) {
                model!!.removeGiftCard(bottomData.giftcardID, bottomData.checkoutId)
            }
        }

        fun clearCart(view: View) {
            var alertDialog = SweetAlertDialog(this@CartList, SweetAlertDialog.WARNING_TYPE)
            alertDialog.titleText = getString(R.string.warning_message)
            alertDialog.contentText = getString(R.string.delete_cart_warning)
            alertDialog.confirmText = getString(R.string.yes_delete)
            alertDialog.cancelText = getString(R.string.no)
            alertDialog.setConfirmClickListener { sweetAlertDialog ->
                sweetAlertDialog.setTitleText(getString(R.string.deleted))
                    .setContentText(getString(R.string.cart_deleted_message))
                    .setConfirmText(getString(R.string.done))
                    .showCancelButton(false)
                    .setConfirmClickListener(null)
                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                model!!.clearCartData()
            }
            alertDialog.show()
        }

        private fun showApplyCouponDialog(data: CartBottomData) {
            var listdialog = Dialog(this@CartList, R.style.WideDialog)
            listdialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            listdialog.window!!.setLayout(
                Constraints.LayoutParams.MATCH_PARENT,
                Constraints.LayoutParams.MATCH_PARENT
            )
            var discountCodeLayoutBinding = DataBindingUtil.inflate<DiscountCodeLayoutBinding>(
                layoutInflater,
                R.layout.discount_code_layout,
                null,
                false
            )
            listdialog.setContentView(discountCodeLayoutBinding.root)
            discountCodeLayoutBinding.noBut.setOnClickListener {
                try {
                    listdialog.dismiss()
                    if (model!!.isLoggedIn) {
                        Log.d(TAG, "loadCheckout: 1" + custom_attribute)
                        Log.i("herer", " " + data.checkoutId)
                        Log.i("herer", "token : " + model?.customeraccessToken?.customerAccessToken)
                        Log.i("attributeInputs", "obj " + custom_attribute)
                        val iter: Iterator<String> = custom_attribute.keys()
                        var itemInput: Storefront.AttributeInput? = null
                        val attributeInputs: MutableList<Storefront.AttributeInput> = ArrayList()
                        while (iter.hasNext()) {
                            val key = iter.next()
                            val value: String = custom_attribute.getString(key)
                            itemInput = Storefront.AttributeInput(key, value)
                            attributeInputs.add(itemInput)
                        }
                        Log.i("attributeInputs", "cart $attributeInputs")
                        if (!TextUtils.isEmpty(binding!!.orderNoteEdt.text.toString().trim())) {
                            model!!.prepareCartwithAttribute(
                                attributeInputs,
                                binding!!.orderNoteEdt.text.toString()
                            )
                        } else {
                            model!!.prepareCartwithAttribute(attributeInputs, "")
                        }
                        model!!.ResponseAtt().observe(this@CartList, Observer<Storefront.Checkout> {
                            //consumeResponse(it)
                            val bottomData = CartBottomData()
                            bottomData.checkoutId = it.id
                            Log.d(TAG, "setBottomData: " + bottomData.checkoutId)
                            bottomData.checkouturl = it.webUrl
                            binding!!.bottomdata = bottomData
                            val intent = Intent(this@CartList, CheckoutWeblink::class.java)
                            intent.putExtra("link", bottomData.checkouturl)
                            intent.putExtra("id", bottomData.checkoutId)
                            startActivity(intent)
                            Constant.activityTransition(this@CartList)
                        })

                    } else {
                        Log.d(TAG, "loadCheckout: 2" + custom_attribute)
                        val iter: Iterator<String> = custom_attribute.keys()
                        var itemInput: Storefront.AttributeInput? = null
                        val attributeInputs: MutableList<Storefront.AttributeInput> =
                            java.util.ArrayList()
                        while (iter.hasNext()) {
                            val key = iter.next()
                            val value: String = custom_attribute.getString(key)
                            itemInput = Storefront.AttributeInput(key, value)
                            attributeInputs.add(itemInput)
                        }
                        Log.i("attributeInputs", "cart $attributeInputs")
                        if (!TextUtils.isEmpty(binding!!.orderNoteEdt.text.toString().trim())) {
                            model!!.prepareCartwithAttribute(
                                attributeInputs,
                                binding!!.orderNoteEdt.text.toString()
                            )
                        } else {
                            model!!.prepareCartwithAttribute(attributeInputs, "")
                        }
                        model!!.ResponseAtt().observe(this@CartList, Observer<Storefront.Checkout> {
                            //consumeResponse(it)
                            val bottomData = CartBottomData()
                            bottomData.checkoutId = it.id
                            Log.d(TAG, "setBottomData: " + bottomData.checkoutId)
                            bottomData.checkouturl = it.webUrl
                            binding!!.bottomdata = bottomData
                            val intent = Intent(this@CartList, CheckoutWeblink::class.java)
                            intent.putExtra("link", bottomData.checkouturl)
                            intent.putExtra("id", bottomData.checkoutId)
                            startActivity(intent)
                            Constant.activityTransition(this@CartList)
                        })
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            discountCodeLayoutBinding.yesBut.setOnClickListener {
                if (TextUtils.isEmpty(
                        discountCodeLayoutBinding.discountCodeEdt.text.toString().trim()
                    )
                ) {
                    discountCodeLayoutBinding.discountCodeEdt.error =
                        getString(R.string.discount_validation)
                } else {
                    if (SplashViewModel.featuresModel.appOnlyDiscount) {
                        model?.NResponse(
                            Urls(application as MyApplication).mid,
                            discountCodeLayoutBinding.discountCodeEdt.text.toString()
                        )?.observe(
                            this@CartList,
                            Observer {
                                this.showData(
                                    it,
                                    data,
                                    discountCodeLayoutBinding.discountCodeEdt.text.toString()
                                )
                            })
                    } else {
                        model!!.applyDiscount(
                            data.checkoutId,
                            discountCodeLayoutBinding.discountCodeEdt.text.toString()
                        )
                    }

                    listdialog.dismiss()
                }
            }
            listdialog.show()
        }

        /******************************** DICOUNTCODE SECTION ***************************************/

        private fun showData(response: ApiResponse?, data: CartBottomData, discountCode: String) {
            Log.i("COUPPNCODERESPONSE", "" + response?.data)
            couponCodeData(response?.data, data, discountCode)
        }

        private fun couponCodeData(
            data: JsonElement?,
            data1: CartBottomData,
            discountCode: String
        ) {
            val jsondata = JSONObject(data.toString())
            if (jsondata.has("discount_code") && jsondata.getBoolean("success")) {
                discountcode = jsondata.getString("discount_code")
                Log.i("DICOUNTCODE", "" + discountcode)
                Log.i("CHECKOUTID", "" + data1.checkoutId)
                model!!.applyDiscount(
                    data1.checkoutId,
                    discountcode.toString()
                )
                MagePrefs.setCouponCode(discountCode)
            } else if (!jsondata.getBoolean("success")) {
                model!!.applyDiscount(
                    data1.checkoutId,
                    discountCode
                )
            }
        }

        /***********************************************************************************/

        fun getResonse(it: Storefront.Checkout?) {
            if (count == 1) {
                val intent = Intent(this@CartList, CheckoutWeblink::class.java)
                intent.putExtra("link", it?.webUrl)
                intent.putExtra("id", it?.id.toString())
                startActivity(intent)
                Constant.activityTransition(this@CartList)
                count++
            }
        }

        var sdk = android.os.Build.VERSION.SDK_INT
        fun storeDeliveryClick(view: View) {
            /*if (!customLoader!!.isShowing) {
                customLoader!!.show()
            }*/
            custom_attribute = JSONObject()

            binding!!.deliveryDateTxt.text =
                resources.getString(R.string.click_here_to_select_delivery_date)
            binding!!.orderNoteEdt.hint = resources.getString(R.string.order_note_hint)
            binding!!.deliveryTimeSpn.visibility = View.GONE
            binding!!.deliverAreaTxt.visibility = View.GONE
            binding!!.zipcode.visibility = View.GONE
            binding!!.pintext.visibility = View.GONE
            binding!!.shipnote.visibility = View.GONE
            binding!!.pintextrue.visibility = View.GONE
            binding!!.proceedtocheck.visibility = View.VISIBLE

            var store_delivery_param =
                model!!.fillStoreDeliveryParam(response_data.lineItems.edges, binding!!.zipcodes)
            model!!.storeDelivery(store_delivery_param)
                .observe(this@CartList, Observer { this@CartList.storeDelivery(it) })

            binding!!.deliverAreaTxt.text = resources.getString(R.string.withdrawal_day_and_time)

            //binding!!.mapContainer.visibility = View.GONE
            binding!!.locationList.visibility = View.VISIBLE
            selected_delivery = "pickup"
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        this@CartList,
                        R.drawable.grey_border
                    )
                )
                binding!!.localContainer.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        this@CartList,
                        R.drawable.black_border
                    )
                )
                binding!!.shippingContainer.background = ContextCompat.getDrawable(
                    this@CartList,
                    R.drawable.black_border
                )
            } else {
                view.background = ContextCompat.getDrawable(
                    this@CartList,
                    R.drawable.grey_border
                )
                binding!!.localContainer.background = ContextCompat.getDrawable(
                    this@CartList,
                    R.drawable.black_border
                )
                binding!!.shippingContainer.background = ContextCompat.getDrawable(
                    this@CartList,
                    R.drawable.black_border
                )
            }
            custom_attribute.put("Checkout-Method", selected_delivery)
        }


        fun localDeliveryClick(view: View) {
            /*if (!customLoader!!.isShowing) {
                customLoader!!.show()
            }*/
            custom_attribute = JSONObject()
            binding!!.deliveryDateTxt.text =
                resources.getString(R.string.click_here_to_select_delivery_date)
            binding!!.orderNoteEdt.hint = resources.getString(R.string.order_note_hint)
            binding!!.deliveryTimeSpn.visibility = View.GONE
            binding!!.deliveryDateTxt.visibility = View.GONE
            //model!!.validateDelivery(delivery_param).observe(this@CartList, Observer { this@CartList.validate_delivery(it, response_data.lineItems.edges) })
//            var store_delivery_param = model!!.fillStoreDeliveryParam(response_data.lineItems.edges,binding!!.zipcodes)
//            model!!.storeDelivery(store_delivery_param).observe(this@CartList, Observer { this@CartList.storeDelivery(it) })
            //binding!!.mapContainer.visibility = View.GONE

//            model!!.validateDelivery(delivery_param).observe(this@CartList, Observer { this@CartList.validate_delivery(it, response_data.lineItems.edges) })
            binding!!.locationList.visibility = View.GONE
            binding!!.zipcode.visibility = View.VISIBLE
            binding!!.deliverAreaTxt.visibility = View.VISIBLE
            binding!!.shipnote.visibility = View.GONE
            binding!!.deliverAreaTxt.text =
                resources.getString(R.string.please_enter_your_postal_code_to_find_out_if_we_deliver_to_this_area)
            selected_delivery = "delivery"
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        this@CartList,
                        R.drawable.grey_border
                    )
                )
                binding!!.storeContainer.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        this@CartList,
                        R.drawable.black_border
                    )
                )

            } else {
                view.background = ContextCompat.getDrawable(
                    this@CartList,
                    R.drawable.grey_border
                )
                binding!!.storeContainer.background = ContextCompat.getDrawable(
                    this@CartList,
                    R.drawable.black_border
                )
            }
            custom_attribute.put("Checkout-Method", selected_delivery)
        }

        fun deliveryDatePicker() {
            dpd.show(supportFragmentManager, "Datepickerdialog")
        }
    }


    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        var date = "" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year
        var new_date = simpleDateFormat.parse(date)
        var dayOfTheWeek = dayFormat.format(new_date)
        binding!!.deliveryDateTxt.text = date
        if (selected_delivery == "pickup") {
            custom_attribute.put("Pickup-Date", date.toString())
        } else {
            custom_attribute.put("Delivery-Date", date.toString())
        }
        localdelivery_slots = ArrayList()
        binding!!.deliveryTimeSpn.visibility = View.VISIBLE
        //   binding!!.orderNoteEdt.visibility = View.VISIBLE
        if (selected_delivery.equals("delivery")) {
            for (i in 0..slots.size() - 1) {
                if (slots[i].asJsonObject.get("day_of_week").asString.equals(dayOfTheWeek, true)) {
                    localdelivery_slots.add(
                        slots[i].asJsonObject.get("available_from").asString + " - " + slots[i].asJsonObject.get(
                            "available_until"
                        ).asString
                    )
                }
            }
            binding!!.deliveryTimeSpn.adapter =
                ArrayAdapter<String>(this, R.layout.spinner_item_layout, localdelivery_slots)
        } else if (selected_delivery.equals("pickup")) {
            val week_object: JsonObject = daysOfWeek.getAsJsonObject(dayOfTheWeek.toLowerCase())
            val array = JSONArray()
            val min = week_object.getAsJsonObject("min")
            val min_hour = min.get("hour").asString
            val min_minute = min.get("minute").asString
            array.put(min_hour + ":" + min_minute)
            val max = week_object.getAsJsonObject("max")
            val max_hour = max.get("hour").asString
            val max_minute = max.get("minute").asString
            Log.i("THESETIMESLOTS", "1 $array")
            val df = SimpleDateFormat("HH:mm")
            val cal = Calendar.getInstance()
            var myTime = min_hour + ":" + min_minute
            while (true) {
                val d = df.parse(myTime)
                cal.time = d
                cal.add(Calendar.MINUTE, interval)
                myTime = df.format(cal.time)


                if (myTime != max_hour + ":" + max_minute) {
                    Log.i("THESETIMESLOTS", "loop $array")
                    array.put(myTime)
                } else {
                    break
                }
            }
            array.put(max_hour + ":" + max_minute)
            val array_display_slots: ArrayList<String> = ArrayList()
            for (x in 0 until array.length()) {
                array_display_slots.add(convert(array.get(x).toString()))
            }
            binding!!.deliveryTimeSpn.adapter =
                ArrayAdapter<String>(this, R.layout.spinner_item_layout, array_display_slots)
        }
        binding!!.deliveryTimeSpn.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    binding!!.bottomsection.visibility = View.VISIBLE
                    Log.d(TAG, "onItemSelected: " + parent?.selectedItem.toString())
                    selected_slot = parent?.selectedItem.toString()
                    if (selected_delivery == "pickup") {
                        custom_attribute.put("Pickup-Time", selected_slot)
                    } else {
                        custom_attribute.put("Delivery-Time", selected_slot)
                    }
                }
            }


    }

    fun convert(time: String): String {
        var convertedTime = ""
        try {
            val displayFormat = SimpleDateFormat("hh:mm")
            val parseFormat = SimpleDateFormat("HH:mm")
            val date = parseFormat.parse(time)
            convertedTime = displayFormat.format(date)
            println("convertedTime : $convertedTime")
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return convertedTime
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}
