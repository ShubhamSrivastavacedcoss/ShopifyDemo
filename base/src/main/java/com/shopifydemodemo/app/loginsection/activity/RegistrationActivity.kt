package com.shopifydemodemo.app.loginsection.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

import com.shopify.buy3.Storefront
import com.shopifydemodemo.app.MyApplication
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.databinding.MRegistrationpageBinding
import com.shopifydemodemo.app.basesection.activities.NewBaseActivity
import com.shopifydemodemo.app.homesection.activities.HomePage
import com.shopifydemodemo.app.loginsection.viewmodels.RegistrationViewModel
import com.shopifydemodemo.app.utils.Constant
import com.shopifydemodemo.app.utils.ViewModelFactory
import kotlinx.android.synthetic.main.m_newbaseactivity.*

import javax.inject.Inject

class RegistrationActivity : NewBaseActivity() {
    private var binding: MRegistrationpageBinding? = null

    @Inject
    lateinit var factory: ViewModelFactory
    private var model: RegistrationViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val group = findViewById<ViewGroup>(R.id.container)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.m_registrationpage, group, true)
        nav_view.visibility = View.GONE
        showTittle(resources.getString(R.string.signupwithustext))
        showBackButton()
        (application as MyApplication).mageNativeAppComponent!!.doRegistrationActivityInjection(this)
        model = ViewModelProviders.of(this, factory).get(RegistrationViewModel::class.java)
        model!!.context = this
        model!!.Response().observe(this, Observer<Storefront.Customer> { this.consumeResponse(it) })
        model!!.LoginResponse().observe(this, Observer<Storefront.CustomerAccessToken> { this.consumeLoginResponse(it) })
        model!!.message.observe(this, Observer<String> { this.showToast(it) })
        var hand = MyClickHandlers(this)
        try {
            MyApplication.dataBaseReference?.child("additional_info")?.child("login")?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val value = dataSnapshot.getValue(String::class.java)!!
                    hand.image = value
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
        binding!!.handlers = hand
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    private fun consumeLoginResponse(token: Storefront.CustomerAccessToken) {
        showToast(resources.getString(R.string.successfullogin))
        model!!.savetoken(token)
        val intent = Intent(this@RegistrationActivity, HomePage::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        Constant.activityTransition(this)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return false
    }

    private fun consumeResponse(customer: Storefront.Customer) {
        model!!.insertUserData(customer)
    }

    inner class MyClickHandlers(private val context: Context) : BaseObservable() {

        @get:Bindable
        var image: String? = null
            set(image) {
                field = image
                notifyPropertyChanged(BR.image)
            }

        fun RegistrationRequest(view: View) {
            if (binding!!.includedlregistartion.firstname.text!!.toString().isEmpty()) {
                binding!!.includedlregistartion.firstname.error = resources.getString(R.string.empty)
                binding!!.includedlregistartion.firstname.requestFocus()
            } else {
                if (binding!!.includedlregistartion.lastname.text!!.toString().isEmpty()) {
                    binding!!.includedlregistartion.lastname.error = resources.getString(R.string.empty)
                    binding!!.includedlregistartion.lastname.requestFocus()
                } else {
                    if (binding!!.includedlregistartion.email.text!!.toString().isEmpty()) {
                        binding!!.includedlregistartion.email.error = resources.getString(R.string.empty)
                        binding!!.includedlregistartion.email.requestFocus()
                    } else {
                        if (!model!!.isValidEmail(binding!!.includedlregistartion.email.text!!.toString())) {
                            binding!!.includedlregistartion.email.error = resources.getString(R.string.invalidemail)
                            binding!!.includedlregistartion.email.requestFocus()
                        } else {
                            if (binding!!.includedlregistartion.password.text!!.toString().isEmpty()) {
                                binding!!.includedlregistartion.password.error = resources.getString(R.string.empty)
                                binding!!.includedlregistartion.password.requestFocus()
                            } else {
                                if (binding!!.includedlregistartion.ConfirmPassword.text!!.toString().isEmpty()) {
                                    binding!!.includedlregistartion.ConfirmPassword.error = resources.getString(R.string.empty)
                                    binding!!.includedlregistartion.ConfirmPassword.requestFocus()
                                } else {
                                    if (binding!!.includedlregistartion.password.text!!.toString() == binding!!.includedlregistartion.ConfirmPassword.text!!.toString()) {
                                        model!!.getRegistrationDetails(binding!!.includedlregistartion.firstname.text!!.toString(), binding!!.includedlregistartion.lastname.text!!.toString(), binding!!.includedlregistartion.email.text!!.toString(), binding!!.includedlregistartion.password.text!!.toString())
                                    } else {
                                        binding!!.includedlregistartion.password.error = resources.getString(R.string.passwordnotmatch)
                                        binding!!.includedlregistartion.ConfirmPassword.error = resources.getString(R.string.passwordnotmatch)
                                        binding!!.includedlregistartion.password.requestFocus()
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
