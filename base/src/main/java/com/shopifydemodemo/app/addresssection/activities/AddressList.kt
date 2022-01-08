package com.shopifydemodemo.app.addresssection.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.shopify.buy3.Storefront
import com.shopifydemodemo.app.MyApplication
import com.shopifydemodemo.app.addresssection.adapters.AddressListAdapter
import com.shopifydemodemo.app.addresssection.models.Address
import com.shopifydemodemo.app.addresssection.viewmodels.AddressModel
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.databinding.MAddresslistBinding
import com.shopifydemodemo.app.basesection.activities.NewBaseActivity
import com.shopifydemodemo.app.utils.ViewModelFactory
import javax.inject.Inject

open class AddressList : NewBaseActivity() {
    internal var binding: MAddresslistBinding? = null
    private var model: AddressModel? = null

    @Inject
    lateinit var factory: ViewModelFactory
    private var addresslist: RecyclerView? = null

    @Inject
    lateinit var adapter: AddressListAdapter
    private var cursor: String? = null
    private var mailingAddressEdges: MutableList<Storefront.MailingAddressEdge>? = null
    private var sheet: BottomSheetBehavior<*>? = null
    var tag: String? = null
    private var address: Address? = null
    private val recyclerViewOnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val visibleItemCount = recyclerView.layoutManager!!.childCount
            val totalItemCount = recyclerView.layoutManager!!.itemCount
            val firstVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            if (!recyclerView.canScrollVertically(1)) {
                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition > 0
                        && totalItemCount >= mailingAddressEdges!!.size) {
                    Log.i("Magenative", "NEwAddress")
                    model!!.addresscursor = cursor.toString()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val group = findViewById<ViewGroup>(R.id.container)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.m_addresslist, group, true)
        binding!!.handler = ClickHandler()
        addresslist = setLayout(binding!!.mainlist.addresslist, "vertical")
        addresslist!!.addOnScrollListener(recyclerViewOnScrollListener)
        showTittle(resources.getString(R.string.myaddress))
        showBackButton()
        (application as MyApplication).mageNativeAppComponent!!.doAddressListInjection(this)
        sheet = BottomSheetBehavior.from(binding!!.mainbottomsheet.bottomSheet)
        sheet!!.state = BottomSheetBehavior.STATE_COLLAPSED
        model = ViewModelProviders.of(this, factory).get(AddressModel::class.java)
        model!!.context = this
        model!!.message.observe(this, Observer<String> { this.showToast(it) })
        model!!.addresses.observe(this, Observer<MutableList<Storefront.MailingAddressEdge>> { this.listAddress(it) })
        model!!.sheet.observe(this, Observer<Boolean> { this.opensheet(it) })
        model!!.editaddress.observe(this, Observer<Address> { this.editAddress(it) })
        sheet!!.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    sheet!!.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }

    private fun editAddress(address: Address) {
        this.address = address
        tag = "edit"
        binding!!.mainbottomsheet.firstname.setText(address.firstName)
        binding!!.mainbottomsheet.lastname.setText(address.lastName)
        binding!!.mainbottomsheet.address1.setText(address.address1)
        binding!!.mainbottomsheet.address2.setText(address.address2)
        binding!!.mainbottomsheet.city.setText(address.city)
        binding!!.mainbottomsheet.country.setText(address.country)
        binding!!.mainbottomsheet.state.setText(address.province)
        binding!!.mainbottomsheet.pincode.setText(address.zip)
        binding!!.mainbottomsheet.phone.setText(address.phone)
    }

    private fun opensheet(aBoolean: Boolean) {
        if (aBoolean) {
            ClickHandler().openSheet()
        }
    }

    private fun listAddress(mailingAddressEdges: MutableList<Storefront.MailingAddressEdge>) {
        try {
            if (mailingAddressEdges.size > 0) {
                if (this.mailingAddressEdges == null) {
                    this.mailingAddressEdges = mailingAddressEdges
                    adapter.setData(mailingAddressEdges, model)
                    addresslist!!.adapter = adapter
                } else {
                    this.mailingAddressEdges!!.addAll(mailingAddressEdges)
                    adapter.notifyDataSetChanged()
                }
                cursor = this.mailingAddressEdges!![this.mailingAddressEdges!!.size - 1].cursor
                Log.i("MageNative", "Cursor : " + cursor!!)

            } else {
                showToast(resources.getString(R.string.noaddressfound))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun showToast(toast: String) {
        Toast.makeText(this@AddressList, toast, Toast.LENGTH_LONG).show()
    }

    override fun onResume() {
        super.onResume()
    }

    inner class ClickHandler {
        fun addAddress(view: View) {
            openSheet()
            tag = "add"
        }

        fun openSheet() {
            if (sheet!!.state == BottomSheetBehavior.STATE_COLLAPSED) {
                sheet!!.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        fun cancelAction(view: View) {
            closeSheet()
        }

        fun closeSheet() {
            if (sheet!!.state == BottomSheetBehavior.STATE_EXPANDED) {
                sheet!!.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        fun Address(view: View) {
            if (binding!!.mainbottomsheet.firstname.text!!.toString().isEmpty()) {
                binding!!.mainbottomsheet.firstname.error = resources.getString(R.string.empty)
                binding!!.mainbottomsheet.firstname.requestFocus()
            } else {
                if (binding!!.mainbottomsheet.lastname.text!!.toString().isEmpty()) {
                    binding!!.mainbottomsheet.lastname.error = resources.getString(R.string.empty)
                    binding!!.mainbottomsheet.lastname.requestFocus()
                } else {
                    if (binding!!.mainbottomsheet.address1.text!!.toString().isEmpty()) {
                        binding!!.mainbottomsheet.address1.error = resources.getString(R.string.empty)
                        binding!!.mainbottomsheet.address1.requestFocus()
                    } else {
                        if (binding!!.mainbottomsheet.address2.text!!.toString().isEmpty()) {
                            binding!!.mainbottomsheet.address2.error = resources.getString(R.string.empty)
                            binding!!.mainbottomsheet.address2.requestFocus()
                        } else {
                            if (binding!!.mainbottomsheet.city.text!!.toString().isEmpty()) {
                                binding!!.mainbottomsheet.city.error = resources.getString(R.string.empty)
                                binding!!.mainbottomsheet.city.requestFocus()
                            } else {
                                if (binding!!.mainbottomsheet.state.text!!.toString().isEmpty()) {
                                    binding!!.mainbottomsheet.state.error = resources.getString(R.string.empty)
                                    binding!!.mainbottomsheet.state.requestFocus()
                                } else {
                                    if (binding!!.mainbottomsheet.country.text!!.toString().isEmpty()) {
                                        binding!!.mainbottomsheet.country.error = resources.getString(R.string.empty)
                                        binding!!.mainbottomsheet.country.requestFocus()
                                    } else {
                                        if (binding!!.mainbottomsheet.pincode.text!!.toString().isEmpty()) {
                                            binding!!.mainbottomsheet.pincode.error = resources.getString(R.string.empty)
                                            binding!!.mainbottomsheet.pincode.requestFocus()
                                        } else {
                                            if (binding!!.mainbottomsheet.phone.text!!.toString().isEmpty()) {
                                                binding!!.mainbottomsheet.phone.error = resources.getString(R.string.empty)
                                                binding!!.mainbottomsheet.phone.requestFocus()
                                            } else {
                                                closeSheet()
                                                Proceed()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        private fun Proceed() {
            val input = Storefront.MailingAddressInput()
            input.firstName = binding!!.mainbottomsheet.firstname.text!!.toString()
            input.lastName = binding!!.mainbottomsheet.lastname.text!!.toString()
            input.company = " "
            input.address1 = binding!!.mainbottomsheet.address1.text!!.toString()
            input.address2 = binding!!.mainbottomsheet.address2.text!!.toString()
            input.city = binding!!.mainbottomsheet.city.text!!.toString()
            input.country = binding!!.mainbottomsheet.country.text!!.toString()
            input.province = binding!!.mainbottomsheet.state.text!!.toString()
            input.zip = binding!!.mainbottomsheet.pincode.text!!.toString()
            input.phone = binding!!.mainbottomsheet.phone.text!!.toString()
            when (tag) {
                "add" -> {
                    mailingAddressEdges = null
                    model!!.addAddress(input)
                }
                "edit" -> model!!.updateAddress(input, address!!.address_id)
            }
            binding!!.mainbottomsheet.firstname.setText("")
            binding!!.mainbottomsheet.lastname.setText("")
            binding!!.mainbottomsheet.address1.setText("")
            binding!!.mainbottomsheet.address2.setText("")
            binding!!.mainbottomsheet.city.setText("")
            binding!!.mainbottomsheet.country.setText("")
            binding!!.mainbottomsheet.state.setText("")
            binding!!.mainbottomsheet.pincode.setText("")
            binding!!.mainbottomsheet.phone.setText("")
        }
    }
}
