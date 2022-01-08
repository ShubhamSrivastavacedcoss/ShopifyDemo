package com.shopifydemodemo.app.searchsection.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.automl.FirebaseAutoMLLocalModel
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceAutoMLImageLabelerOptions
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceImageLabelerOptions
import com.google.zxing.integration.android.IntentIntegrator
import com.mindorks.paracamera.Camera
import com.shopify.buy3.Storefront
import com.shopifydemodemo.app.MyApplication
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.databinding.MAutosearchBinding
import com.shopifydemodemo.app.basesection.activities.NewBaseActivity
import com.shopifydemodemo.app.basesection.viewmodels.SplashViewModel.Companion.featuresModel
import com.shopifydemodemo.app.searchsection.adapters.SearchRecylerAdapter
import com.shopifydemodemo.app.searchsection.viewmodels.SearchListModel
import com.shopifydemodemo.app.utils.ViewModelFactory
import javax.inject.Inject

class AutoSearch : NewBaseActivity() {
    private var binding: MAutosearchBinding? = null

    @Inject
    lateinit var factory: ViewModelFactory
    private var model: SearchListModel? = null
    private var search_cursor: String? = null
    private var search_string: String? = ""

    @Inject
    lateinit var adapter: SearchRecylerAdapter
    private var viewlist: RecyclerView? = null
    private lateinit var camera: Camera
    private val PERMISSION_REQUEST_CODE = 1
    var image: FirebaseVisionImage? = null
    var scrolling: Boolean = false
    private val TAG = "AutoSearch"
    lateinit var labeler: FirebaseVisionImageLabeler
    private var search_product: MutableList<Storefront.ProductEdge>? = null

    private val recyclerViewOnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            val visibleItemCount = recyclerView.layoutManager!!.childCount
            val totalItemCount = recyclerView.layoutManager!!.itemCount
            val firstVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            if (!recyclerView.canScrollVertically(1)) {
                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0
                        && totalItemCount >= search_product!!.size) {
                    model!!.searchcursor = search_cursor!!
                    model!!.setSearchData(search_string)
                    scrolling = true
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val group = findViewById<ViewGroup>(R.id.container)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.m_autosearch, group, true)
        showBackButton()
        showTittle(resources.getString(R.string.search))
        val secondary = FirebaseApp.getInstance("MageNative")
        val localModel = FirebaseAutoMLLocalModel.Builder()
                .setAssetFilePath("manifest.json")
                .build()
        val optionsBuilder = FirebaseVisionOnDeviceAutoMLImageLabelerOptions.Builder(localModel)
        // val options = optionsBuilder.setConfidenceThreshold(0.5f).build()
        val options = FirebaseVisionOnDeviceImageLabelerOptions.Builder()
                .setConfidenceThreshold(0.7f)
                .build()
        labeler = FirebaseVision.getInstance().getOnDeviceImageLabeler(options)
        camera = Camera.Builder()
                .resetToCorrectOrientation(true)//1
                .setTakePhotoRequestCode(Camera.REQUEST_TAKE_PHOTO)//2
                .setDirectory("pics")//3
                .setName("delicious_${System.currentTimeMillis()}")//4
                .setImageFormat(Camera.IMAGE_JPEG)//5
                .setCompression(75)//6
                .build(this)
        viewlist = setLayout(binding!!.searchlist, "vertical")
        viewlist!!.addOnScrollListener(recyclerViewOnScrollListener)
        (application as MyApplication).mageNativeAppComponent!!.doAutoSearchActivityInjection(this)
        model = ViewModelProviders.of(this, factory).get(SearchListModel::class.java)
        model!!.message.observe(this, Observer<String> { this.showToast(it) })
        model!!.setPresentmentCurrencyForModel()
        model!!.filteredproducts!!.observe(this, Observer<MutableList<Storefront.ProductEdge>> { this.setRecylerData(it) })
        binding!!.searchtext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                search_string = s.toString()
                model!!.searchcursor = "nocursor"
                if (search_string?.length!! >= 3) {
                    model!!.setSearchData(search_string)
                } else if (search_string?.length!! == 0) {
                    model!!.setSearchData(search_string)
                }
            }
            override fun afterTextChanged(s: Editable) {
                Log.d(TAG, "afterTextChanged: " + s.toString())
            }
        })
        binding?.searchtext?.requestFocus()
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (featuresModel.qr_code_search_scanner) {
            menuInflater.inflate(R.menu.menu_scanner, menu)
            return true
        } else {
            return false
        }
    }

    private fun setRecylerData(products: MutableList<Storefront.ProductEdge>) {
        try {
            if (products.size > 0) {
                adapter!!.presentmentcurrency = model!!.presentmentcurrency
                if (!scrolling) {
                    search_product = products
                    adapter!!.setData(search_product!!, this@AutoSearch)
                    viewlist!!.adapter = adapter
                } else {
                    if (model!!.searchcursor == "nocursor") {
                        adapter!!.products!!.clear()
                        adapter!!.products!!.addAll(products!!)
                    } else {
                        adapter!!.products!!.addAll(products!!)
                    }
                    adapter!!.notifyDataSetChanged()
                }
                Log.d(TAG, "setRecylerData: " + products)
                search_cursor = products[products.size - 1].cursor
            } else {
                showToast(resources.getString(R.string.noproducts))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.scanner -> {
                val integrator = IntentIntegrator(this)
                integrator.setPrompt("Scan a barcode")
                integrator.setCameraId(0) // Use a specific camera of the device
                integrator.setOrientationLocked(true)
                integrator.setBeepEnabled(true)
                integrator.captureActivity = SearchByScanner::class.java
                integrator.initiateScan()
                true
            }
            R.id.camera -> {
                takePicture()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Camera.REQUEST_TAKE_PHOTO) {
                val bitmap = camera.cameraBitmap
                if (bitmap != null) {
                    checkImage(bitmap)
                } else {
                    Toast.makeText(this.applicationContext, getString(R.string.picture_not_taken), Toast.LENGTH_SHORT).show()
                }
            } else {
                val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
                if (result != null) {
                    if (result.contents == null) {
                        Toast.makeText(applicationContext, "" + resources.getString(R.string.noresultfound), Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        try {
                            adapter!!.products = null
                            Log.i("MageNative", "Barcode" + result.contents)
                            model!!.searchResultforscanner(result.contents)
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun takePicture() {
        if (!hasPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                !hasPermission(android.Manifest.permission.CAMERA)) {
            // If do not have permissions then request it
            requestPermissions()
        } else {
            // else all permissions granted, go ahead and take a picture using camera
            try {
                camera.takePicture()
            } catch (e: Exception) {
                // Show a toast for exception
                Toast.makeText(this.applicationContext, getString(R.string.error_taking_picture),
                        Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA), PERMISSION_REQUEST_CODE)
        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.CAMERA), PERMISSION_REQUEST_CODE)
            return
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        camera.takePicture()
                    } catch (e: Exception) {
                        Toast.makeText(this.applicationContext, getString(R.string.error_taking_picture),
                                Toast.LENGTH_SHORT).show()
                    }
                }
                return
            }
        }
    }

    private fun checkImage(bitmap: Bitmap) {
        image = FirebaseVisionImage.fromBitmap(bitmap)
        labeler.processImage(image!!)
                .addOnSuccessListener { labels ->
                    for (label in labels) {
                        val text = label.text
                        val confidence = label.confidence
                        if (label.confidence > 0.7) {
                            Log.d(TAG, "checkImage: "+text)
                            model!!.getProductsByKeywords(text)
                            Log.i("MageNative", "Label : " + text)
                            Log.i("MageNative", "confidence : $confidence")
                        } else {
                            continue
                        }
                    }
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                }
    }

    private fun hasPermission(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(this,
                permission) == PackageManager.PERMISSION_GRANTED
    }
}
