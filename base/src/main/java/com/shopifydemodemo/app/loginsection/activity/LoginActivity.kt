package com.shopifydemodemo.app.loginsection.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.facebook.*
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.shopify.buy3.Storefront
import com.shopifydemodemo.app.MyApplication
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.databinding.MLoginPageBinding
import com.shopifydemodemo.app.basesection.activities.NewBaseActivity
import com.shopifydemodemo.app.basesection.viewmodels.SplashViewModel
import com.shopifydemodemo.app.cartsection.activities.CartList
import com.shopifydemodemo.app.databinding.MForgotbottomsheetBinding
import com.shopifydemodemo.app.homesection.activities.HomePage
import com.shopifydemodemo.app.loginsection.viewmodels.LoginViewModel
import com.shopifydemodemo.app.sharedprefsection.MagePrefs
import com.shopifydemodemo.app.utils.Constant
import com.shopifydemodemo.app.utils.Urls
import com.shopifydemodemo.app.utils.ViewModelFactory
import kotlinx.android.synthetic.main.m_login.*
import kotlinx.android.synthetic.main.m_newbaseactivity.*
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import javax.inject.Inject

class LoginActivity : NewBaseActivity() {
    private var binding: MLoginPageBinding? = null

    @Inject
    lateinit var factory: ViewModelFactory
    private var model: LoginViewModel? = null
    lateinit var firebaseAnalytics: FirebaseAnalytics
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private val RC_SIGN_IN = 7
    private val TAG = "LoginActivity"
    private var callbackManager: CallbackManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val group = findViewById<ViewGroup>(R.id.container)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.m_login_page, group, true)
        nav_view.visibility = View.GONE
        showBackButton()
        showTittle(resources.getString(R.string.login))
        (application as MyApplication).mageNativeAppComponent!!.doLoginActivtyInjection(this)
        model = ViewModelProviders.of(this, factory).get(LoginViewModel::class.java)
        model!!.context = this
        firebaseAnalytics = Firebase.analytics
        model!!.Response().observe(this, Observer<Storefront.CustomerAccessToken> { this.consumeResponse(it) })
        model!!.getResponsedata_().observe(this, Observer<Storefront.Customer> { this.MapLoginDetails(it) })
        model!!.errormessage.observe(this, Observer<String> { this.showToast(it) })
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
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        callbackManager = CallbackManager.Factory.create()
        fb_button.setReadPermissions("public_profile email")
        fb_button.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            override fun onCancel() {}
            override fun onError(exception: FacebookException) {}
            override fun onSuccess(result: LoginResult?) {
                if (AccessToken.getCurrentAccessToken() != null) {
                    RequestData()
                }
            }
        })
        if(SplashViewModel.featuresModel.socialloginEnable){
            sociallogins.visibility = View.VISIBLE
            orsection.visibility = View.VISIBLE
        }
        binding!!.handlers = hand
    }
    private fun RequestData() {
        val request = GraphRequest.newMeRequest(
            AccessToken.getCurrentAccessToken(),
            object : GraphRequest.GraphJSONObjectCallback {
                override fun onCompleted(`object`: JSONObject?, response: GraphResponse?) {
                    val json = response!!.getJSONObject()
                    Log.i("qwertyy", "" + json)
                    try {
                        if (json != null) {
                            model?.socialLogin(
                                Urls((application as MyApplication))?.mid,
                                json.getString("first_name"),
                                json.getString("last_name"),
                                json.getString("email"),
                                "pass@kwd"
                            )
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            })
        val parameters = Bundle()
        parameters.putString("fields", "id,name,link,email,picture,first_name,last_name")
        request.parameters = parameters
        request.executeAsync()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return false
    }

    private fun showToast(toast: String) {
        Toast.makeText(this, toast, Toast.LENGTH_LONG).show()
    }

    private fun consumeResponse(result: Storefront.CustomerAccessToken) {
        model!!.savetoken(result)
    }

    private fun MapLoginDetails(customer: Storefront.Customer) {
        model!!.saveUser(customer.firstName, customer.lastName)
        MagePrefs.setCustomerEmail(customer.email)
        MagePrefs.setCustomerId(getBase64Decode(customer.id.toString())!!)
        MagePrefs.setCustomerFirstName(customer.firstName)
        MagePrefs.setCustomerLastName(customer.lastName)
        if (intent.getStringExtra("checkout_id") != null) {
            val intent = Intent(this@LoginActivity, CartList::class.java)
            intent.putExtra("checkout_id", getIntent().getStringExtra("checkout_id"))
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            Constant.activityTransition(this)
        } else {
            val intent = Intent(this@LoginActivity, HomePage::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            Constant.activityTransition(this)
        }
        finish()
    }
    fun getBase64Decode(id: String?): String? {
        val data = Base64.decode(id, Base64.DEFAULT)
        var text = String(data, StandardCharsets.UTF_8)
        val datavalue = text.split("/".toRegex()).toTypedArray()
        val valueid = datavalue[datavalue.size - 1]
        val datavalue2 = valueid.split("key".toRegex()).toTypedArray()
        text = datavalue2[0]
        return text
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                val acct = result.signInAccount
                model?.socialLogin(
                    Urls((application as MyApplication))?.mid,
                    acct.displayName,
                    acct.familyName,
                    acct.email,
                    "pass@kwd"
                )
                Log.d(TAG, "onActivityResult: " + acct.displayName)
                Log.d(TAG, "onActivityResult: " + acct.email)
            } else {
                Toast.makeText(
                    this,
                    "" + resources.getString(R.string.loginfailed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    inner class MyClickHandlers(private val context: Context) : BaseObservable() {
        @get:Bindable
        var image: String? = null
            set(image) {
                field = image
                notifyPropertyChanged(BR.image)
            }

        fun onSignUpClicked(view: View) {
            if (binding!!.includedlogin.username.text!!.toString().isEmpty()) {
                binding!!.includedlogin.username.error = resources.getString(R.string.empty)
                binding!!.includedlogin.username.requestFocus()
            } else {
                if (!model!!.isValidEmail(binding!!.includedlogin.username.text!!.toString())) {
                    binding!!.includedlogin.username.error = resources.getString(R.string.invalidemail)
                    binding!!.includedlogin.username.requestFocus()
                } else {
                    if (binding!!.includedlogin.password.text!!.toString().isEmpty()) {
                        binding!!.includedlogin.password.error = resources.getString(R.string.empty)
                        binding!!.includedlogin.password.requestFocus()
                    } else {
                        model!!.getUser(binding!!.includedlogin.username.text!!.toString(), binding!!.includedlogin.password.text!!.toString())
                        val params = Bundle()
                        params.putString("user_email", binding!!.includedlogin.username.text!!.toString())
                        firebaseAnalytics.logEvent("android_email_log", params)
                    }
                }
            }
        }

        fun newsignup(view: View) {
            val signup_page = Intent(context, RegistrationActivity::class.java)
            startActivity(signup_page)
            Constant.activityTransition(context)
        }

        fun GoogleLogin(view: View) {
            val signInIntent = mGoogleSignInClient!!.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        fun FbLogin(view: View) {
            fb_button.performClick()
        }

        fun forgotPass(view: View) {
            var dialog = Dialog(this@LoginActivity, R.style.WideDialog)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
            var mForgotbottomsheetBinding = DataBindingUtil.inflate<MForgotbottomsheetBinding>(layoutInflater, R.layout.m_forgotbottomsheet, null, false)
            dialog.setContentView(mForgotbottomsheetBinding.root)
            mForgotbottomsheetBinding.login.setOnClickListener {
                if (mForgotbottomsheetBinding!!.email.text!!.toString().isEmpty()) {
                    mForgotbottomsheetBinding.email.error = resources.getString(R.string.empty)
                    mForgotbottomsheetBinding.email.requestFocus()
                } else {
                    if (!model!!.isValidEmail(mForgotbottomsheetBinding.email.text!!.toString())) {
                        mForgotbottomsheetBinding.email.error = resources.getString(R.string.invalidemail)
                        mForgotbottomsheetBinding.email.requestFocus()
                    } else {
                        model!!.recoverCustomer(mForgotbottomsheetBinding.email.text!!.toString())
                        mForgotbottomsheetBinding.email.setText(" ")
                        dialog.dismiss()
                    }
                }
            }
            mForgotbottomsheetBinding.closeBut.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }
}
