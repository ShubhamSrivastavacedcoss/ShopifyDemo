package com.shopifydemodemo.app.ordersection.activities

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.shopify.buy3.GraphCallResult
import com.shopify.buy3.Storefront
import com.shopify.graphql.support.Error
import com.shopifydemodemo.app.MyApplication
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.basesection.activities.NewBaseActivity
import com.shopifydemodemo.app.databinding.ActivityOrderviewBinding
import com.shopifydemodemo.app.ordersection.adapters.OrderDetailsListAdapter
import com.shopifydemodemo.app.ordersection.viewmodels.OrderDetailsViewModel
import com.shopifydemodemo.app.personalised.adapters.PersonalisedAdapter
import com.shopifydemodemo.app.utils.CurrencyFormatter
import com.shopifydemodemo.app.utils.GraphQLResponse
import com.shopifydemodemo.app.utils.Status
import com.shopifydemodemo.app.utils.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class OrderDetails : NewBaseActivity() {
    private var binding: ActivityOrderviewBinding? = null
    private var orderEdge: Storefront.Order? = null
    private val TAG = "OrderDetails"

    @Inject
    lateinit var factory: ViewModelFactory
    private var model: OrderDetailsViewModel? = null

    @Inject
    lateinit var personalisedadapter: PersonalisedAdapter

    @Inject
    lateinit var orderDetailsListAdapter: OrderDetailsListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val group = findViewById<ViewGroup>(R.id.container)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_orderview, group, true)
        (application as MyApplication).mageNativeAppComponent!!.doOrderDetailsInjection(this)
        model = ViewModelProviders.of(this, factory).get(OrderDetailsViewModel::class.java)
        model?.context = this
        model?.setPresentmentCurrencyForModel()
        showBackButton()
        showTittle(resources.getString(R.string.OrderDetails))
        model!!.recommendedLiveData.observe(this, androidx.lifecycle.Observer { this.consumeRecommended(it) })
        binding?.orderedItems?.adapter = orderDetailsListAdapter
        if (intent.hasExtra("orderData")) {
            orderEdge = intent.getSerializableExtra("orderData") as Storefront.Order
            Log.d(TAG, "onCreate: " + orderEdge?.lineItems?.edges?.get(0)?.node?.variant?.product?.id.toString())
            model?.shopifyRecommended(orderEdge?.lineItems?.edges?.get(0)?.node?.variant?.product?.id.toString())
            bindData(orderEdge)
        }
    }

    private fun consumeRecommended(response: GraphQLResponse?) {
        when (response?.status) {
            Status.SUCCESS -> {
                val result = (response?.data as GraphCallResult.Success<Storefront.QueryRoot>).response
                if (result.hasErrors) {
                    val errors = result.errors
                    val iterator = errors.iterator()
                    val errormessage = StringBuilder()
                    var error: Error? = null
                    while (iterator.hasNext()) {
                        error = iterator.next()
                        errormessage.append(error.message())
                    }
                    Toast.makeText(this, "" + errormessage, Toast.LENGTH_SHORT).show()
                } else {
                    var recommendedList = result.data!!.productRecommendations as ArrayList<Storefront.Product>?
                    if (recommendedList?.size!! > 0) {
                        binding!!.shopifyrecommendedList.visibility = View.VISIBLE
                        setLayout(binding!!.shopifyrecommendedList, "horizontal")
                        personalisedadapter = PersonalisedAdapter()
                        personalisedadapter.setData(recommendedList, this, model?.repository!!)
                        binding!!.shopifyrecommendedList.adapter = personalisedadapter
                    }
                }
            }
            Status.ERROR -> Toast.makeText(this, response.error!!.error.message, Toast.LENGTH_SHORT).show()
            else -> {
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindData(orderEdge: Storefront.Order?) {
        binding?.orderId?.text = getString(R.string.order_id) + orderEdge?.orderNumber.toString()
        val sdf2 = SimpleDateFormat("MMM dd yyyy", Locale.getDefault())
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val expiretime = sdf.parse(orderEdge?.processedAt?.toLocalDateTime().toString())
        val time = sdf2.format(expiretime!!)
        binding?.orderDate?.text = getString(R.string.placedon) + " " + time
        orderDetailsListAdapter.setData(orderEdge?.lineItems?.edges)
        binding?.orderedItemsIndicator?.setViewPager(binding?.orderedItems)
        binding?.orderedItemsIndicator?.tintIndicator(Color.parseColor(themeColor))
        binding?.customerName?.text = orderEdge?.shippingAddress?.firstName + " " + orderEdge?.shippingAddress?.lastName
        val shippingAddress = StringBuffer()
        shippingAddress.append(orderEdge?.shippingAddress?.address1)
        shippingAddress.append("\n")
        shippingAddress.append(orderEdge?.shippingAddress?.city)
        shippingAddress.append("\n")
        shippingAddress.append(orderEdge?.shippingAddress?.country)
        shippingAddress.append("\n")
        shippingAddress.append(orderEdge?.shippingAddress?.zip)
        binding?.shippingAddress?.text = shippingAddress

        binding?.subtotalPrice?.text = getString(R.string.subtotal_amt) + " " + CurrencyFormatter.setsymbol(orderEdge?.subtotalPriceV2?.amount
                ?: "", orderEdge?.subtotalPriceV2?.currencyCode.toString())
        binding?.shippingPrice?.text = getString(R.string.shipping_amt) + " " + CurrencyFormatter.setsymbol(orderEdge?.totalShippingPriceV2?.amount
                ?: "", orderEdge?.totalShippingPriceV2?.currencyCode.toString())
        binding?.taxPrice?.text = getString(R.string.tax_amt) + " " + CurrencyFormatter.setsymbol(orderEdge?.totalTaxV2?.amount
                ?: "", orderEdge?.totalTaxV2?.currencyCode.toString())
        binding?.orderPrice?.text = CurrencyFormatter.setsymbol(orderEdge?.totalPriceV2?.amount
                ?: "", orderEdge?.totalPriceV2?.currencyCode.toString())

        binding?.customerEmail?.text = orderEdge?.email?:" N/A"
        binding?.customerMobile?.text = orderEdge?.phone?:" N/A"
        binding?.paymentStatus?.text = getString(R.string.payment_status) + " " + orderEdge?.financialStatus.toString()
        if (orderEdge?.financialStatus.toString().equals("REFUNDED")) {
            binding?.cancelledAt?.visibility = View.VISIBLE
            binding?.cancelledReason?.visibility = View.VISIBLE
            val cancelled_at = sdf.parse(orderEdge?.canceledAt?.toLocalDateTime().toString())
            val cancelDate = sdf2.format(cancelled_at)
            binding?.cancelledAt?.text = getString(R.string.cancelled_at) + " " + cancelDate
            binding?.cancelledReason?.text = getString(R.string.cancelled_reason) + " " + orderEdge?.cancelReason.toString()
        }
        binding?.orderStatus?.text = orderEdge?.fulfillmentStatus.toString()
        if (orderEdge?.fulfillmentStatus.toString().equals("UNFULFILLED")) {
            binding?.orderStatusContainer?.setBackgroundColor(resources.getColor(R.color.red))
            binding?.orderStatusIcon?.setImageDrawable(resources.getDrawable(R.drawable.cross_icon))
        } else if (orderEdge?.fulfillmentStatus.toString().equals("FULFILLED")) {
            binding?.orderStatusContainer?.setBackgroundColor(resources.getColor(R.color.green))
            binding?.orderStatusIcon?.setImageDrawable(resources.getDrawable(R.drawable.tick))
        } else {
            binding?.orderStatusContainer?.setBackgroundColor(resources.getColor(R.color.orange))
            binding?.orderStatusIcon?.setImageDrawable(resources.getDrawable(R.drawable.order_history))
        }
    }
}