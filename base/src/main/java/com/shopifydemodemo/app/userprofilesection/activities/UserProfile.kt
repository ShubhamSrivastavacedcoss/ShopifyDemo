package com.shopifydemodemo.app.userprofilesection.activities

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.shopify.buy3.Storefront
import com.shopifydemodemo.app.MyApplication
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.databinding.MUserprofileBinding
import com.shopifydemodemo.app.basesection.activities.NewBaseActivity
import com.shopifydemodemo.app.userprofilesection.models.User
import com.shopifydemodemo.app.userprofilesection.viewmodels.UserProfileViewModel
import com.shopifydemodemo.app.utils.ViewModelFactory

import javax.inject.Inject

class UserProfile : NewBaseActivity() {
    private var binding: MUserprofileBinding? = null

    @Inject
    lateinit var factory: ViewModelFactory
    private var model: UserProfileViewModel? = null
    private var user: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val group = findViewById<ViewGroup>(R.id.container)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.m_userprofile, group, true)
        showBackButton()
        showTittle(resources.getString(R.string.myprofile))
        (application as MyApplication).mageNativeAppComponent!!.doUserProfileInjection(this)
        user = User()
        binding!!.user = user
        binding!!.handler = ClickHandler()
        model = ViewModelProviders.of(this, factory).get(UserProfileViewModel::class.java)
        model!!.context=this
        model!!.getResponse_().observe(this, Observer<Storefront.Customer> { this.consumeResponse(it) })
        model!!.passwordResponse.observe(this, Observer<String> { this.consumeResponse(it) })
        model!!.errorMessageResponse.observe(this, Observer<String> { this.showToast(it) })
    }

    private fun consumeResponse(customer: Storefront.Customer) {
        user!!.firstname = customer.firstName
        user!!.lastname = customer.lastName
        user!!.email = customer.email
        model!!.saveUser(user!!)
    }

    private fun consumeResponse(password: String) {
        user!!.password = password
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    inner class ClickHandler {
        fun updateProfile(view: View, user: User) {
            if (binding!!.firstname.text!!.toString().isEmpty()) {
                binding!!.firstname.error = resources.getString(R.string.empty)
                binding!!.firstname.requestFocus()
            } else {
                if (binding!!.lastname.text!!.toString().isEmpty()) {
                    binding!!.lastname.error = resources.getString(R.string.empty)
                    binding!!.lastname.requestFocus()
                } else {
                    if (binding!!.email.text!!.toString().isEmpty()) {
                        binding!!.email.error = resources.getString(R.string.empty)
                        binding!!.email.requestFocus()
                    } else {
                        if (!model!!.isValidEmail(binding!!.email.text!!.toString())) {
                            binding!!.email.error = resources.getString(R.string.invalidemail)
                            binding!!.email.requestFocus()
                        } else {
                            if (binding!!.password.text!!.toString().isEmpty()) {
                                binding!!.password.error = resources.getString(R.string.empty)
                                binding!!.password.requestFocus()
                            } else {
                                if (binding!!.ConfirmPassword.text!!.toString().isEmpty()) {
                                    binding!!.ConfirmPassword.error = resources.getString(R.string.empty)
                                    binding!!.ConfirmPassword.requestFocus()
                                } else {
                                    if (binding!!.password.text!!.toString() == binding!!.ConfirmPassword.text!!.toString()) {
                                        user.firstname = binding!!.firstname.text!!.toString()
                                        user.lastname = binding!!.lastname.text!!.toString()
                                        user.email = binding!!.email.text!!.toString()
                                        user.password = binding!!.password.text!!.toString()
                                        model!!.updateDataonServer(user)
                                    } else {
                                        binding!!.password.error = resources.getString(R.string.passwordnotmatch)
                                        binding!!.ConfirmPassword.error = resources.getString(R.string.passwordnotmatch)
                                        binding!!.password.requestFocus()
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
