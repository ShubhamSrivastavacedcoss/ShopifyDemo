package com.shopifydemodemo.app.basesection.activities

import android.annotation.SuppressLint
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import com.shopifydemodemo.app.MyApplication
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.databinding.MWebpageBinding
import com.shopifydemodemo.app.loader_section.CustomLoader
import com.shopifydemodemo.app.utils.Urls

class Weblink : NewBaseActivity() {
    private var webView: WebView? = null
    private var currentUrl: String? = null
    private var binding: MWebpageBinding? = null
    private var customLoader: CustomLoader? = null

    @SuppressLint("SetJavaScriptEnabled")
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val content = findViewById<ViewGroup>(R.id.container)
        getLayoutInflater().inflate(R.layout.m_webpage, content, true)
        binding = DataBindingUtil.inflate<MWebpageBinding>(
            getLayoutInflater(),
            R.layout.m_webpage,
            content,
            true
        )
        webView = binding!!.webview
        showBackButton()
        if (getIntent().hasExtra("name") && getIntent().getStringExtra("name") != null) {
            showTittle(getIntent().getStringExtra("name") ?: "")
        }
        Log.i("MageNative", "Link :" + getIntent().getStringExtra("link")!!)
        if (getIntent().getStringExtra("link")!!.contains("https") || getIntent().getStringExtra("link")!!.contains("http")) {
            currentUrl = getIntent().getStringExtra("link")
        } else {
            currentUrl =
                "https://" + Urls(MyApplication.context).shopdomain + getIntent().getStringExtra("link")
        }
        customLoader = CustomLoader(this)
        customLoader?.show()
        webView!!.getSettings().setJavaScriptEnabled(true)
        webView!!.getSettings().setLoadWithOverviewMode(true)
        webView!!.getSettings().setUseWideViewPort(true)
        setUpWebViewDefaults(webView!!)
        webView!!.loadUrl(currentUrl!!.trim({ it <= ' ' }))
        webView!!.setWebChromeClient(WebChromeClient())
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpWebViewDefaults(webView: WebView) {
        val settings = webView.getSettings()
        settings.setJavaScriptEnabled(true)
        settings.setDomStorageEnabled(true)
        settings.setUseWideViewPort(true)
        settings.setLoadWithOverviewMode(true)
        settings.setBuiltInZoomControls(true)
        settings.setDisplayZoomControls(false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
        webView.setWebViewClient(object : WebViewClient() {
            public override fun onReceivedError(
                view: WebView,
                errorCode: Int,
                description: String,
                failingUrl: String
            ) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                Log.i("URL", "" + description)
                customLoader?.dismiss()
            }

            public override fun onLoadResource(view: WebView, url: String) {
                Log.i("URL", "" + url)
            }

            public override fun onPageFinished(view: WebView, url: String) {
                customLoader?.dismiss()
                Log.i("pageURL", "" + url)
                val javascript =
                    "javascript: document.getElementsByClassName('grid--table')[0].style.display = 'none' "
                val javascript1 =
                    "javascript: document.getElementsByClassName('site-header')[0].style.display = 'none' "
                val javascript2 =
                    "javascript: document.getElementsByClassName('site-footer')[0].style.display = 'none' "
                val javascript3 =
                    "javascript: document.getElementsByClassName('ui-admin-bar__content')[0].style.display = 'none' "
                val javascript4 =
                    "javascript: document.getElementsByClassName('sweettooth-launcher-container')[0].style.display = 'none' "
                val javascript5 =
                    "javascript: document.getElementsByClassName('ui-admin-bar__body')[0].style.display = 'none' "
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    webView.evaluateJavascript(javascript, object : ValueCallback<String> {
                        public override fun onReceiveValue(value: String) {
                            Log.i("pageVALUE1", "" + value)
                        }
                    })
                    webView.evaluateJavascript(javascript1, object : ValueCallback<String> {
                        public override fun onReceiveValue(value: String) {
                            Log.i("pageVALUE1", "" + value)
                        }
                    })
                    webView.evaluateJavascript(javascript2, object : ValueCallback<String> {
                        public override fun onReceiveValue(value: String) {
                            Log.i("pageVALUE1", "" + value)
                        }
                    })
                    webView.evaluateJavascript(javascript3, object : ValueCallback<String> {
                        public override fun onReceiveValue(value: String) {
                            Log.i("pageVALUE1", "" + value)
                        }
                    })
                    webView.evaluateJavascript(javascript4, object : ValueCallback<String> {
                        public override fun onReceiveValue(value: String) {
                            Log.i("pageVALUE1", "" + value)
                        }
                    })
                    webView.evaluateJavascript(javascript5, object : ValueCallback<String> {
                        public override fun onReceiveValue(value: String) {
                            Log.i("pageVALUE1", "" + value)
                        }
                    })
                } else {
                    webView.loadUrl(javascript)
                    webView.loadUrl(javascript1)
                    webView.loadUrl(javascript2)
                    webView.loadUrl(javascript3)
                    webView.loadUrl(javascript4)
                    webView.loadUrl(javascript5)
                }
            }

            public override fun onReceivedSslError(
                view: WebView,
                handler: SslErrorHandler,
                error: SslError
            ) {
                super.onReceivedSslError(view, handler, error)
                Log.i("URL", "" + error.getUrl())
                customLoader?.dismiss()
            }
        })
    }

    public override fun onBackPressed() {
        if (webView!!.canGoBack()) {
            webView!!.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
