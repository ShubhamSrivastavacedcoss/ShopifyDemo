package com.shopifydemodemo.app.yotporewards.referfriend

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.shopifydemodemo.app.MyApplication
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.basesection.activities.NewBaseActivity
import com.shopifydemodemo.app.databinding.ActivityReferFriendBinding
import com.shopifydemodemo.app.databinding.ReferfriendDialogBinding
import com.shopifydemodemo.app.utils.ApiResponse
import com.shopifydemodemo.app.utils.ViewModelFactory
import javax.inject.Inject

class ReferFriendActivity : NewBaseActivity() {
    private var binding: ActivityReferFriendBinding? = null
    private var model: ReferFriendViewModel? = null
    var dialog: Dialog? = null

    @Inject
    lateinit var factory: ViewModelFactory
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val group = findViewById<ViewGroup>(R.id.container)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_refer_friend, group, true)
        (application as MyApplication).mageNativeAppComponent!!.doReferFriendInjection(this)
        model = ViewModelProvider(this, factory).get(ReferFriendViewModel::class.java)
        model?.context = this
        showBackButton()
        showTittle(getString(R.string.refer_friends))
        model?.referfriend?.observe(this, Observer { this.consumeSendReferral(it) })
        binding?.emailBut?.setOnClickListener {
            openEmailDialog()
        }
    }

    private fun consumeSendReferral(response: ApiResponse?) {
        if (dialog?.isShowing ?: false) {
            dialog?.dismiss()
            Toast.makeText(this, getString(R.string.mail_sent), Toast.LENGTH_SHORT).show()
        }

    }

    private fun openEmailDialog() {
        dialog = Dialog(this, R.style.WideDialog)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        var referfriendDialogBinding = DataBindingUtil.inflate<ReferfriendDialogBinding>(layoutInflater, R.layout.referfriend_dialog, null, false)
        dialog?.setContentView(referfriendDialogBinding.root)
        referfriendDialogBinding.sendemailBut.setOnClickListener {
            if (TextUtils.isEmpty(referfriendDialogBinding.emailEdt.text.toString().trim())) {
                referfriendDialogBinding.emailEdt.error = getString(R.string.email_validation)
                referfriendDialogBinding.emailEdt.requestFocus()
            } else {
                model?.sendReferral(referfriendDialogBinding.emailEdt.text.toString().trim())
            }
        }
        referfriendDialogBinding.closeBut.setOnClickListener {
            dialog?.dismiss()
        }
        dialog?.show()

    }
}