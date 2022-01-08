package com.shopifydemodemo.app.productsection.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.JsonObject
import com.karumi.dexter.Dexter
import com.karumi.dexter.DexterBuilder
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.mindorks.paracamera.Camera
import com.shopifydemodemo.app.MyApplication
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.basesection.activities.NewBaseActivity
import com.shopifydemodemo.app.databinding.ActivityJudgeMeCreateReviewBinding
import com.shopifydemodemo.app.productsection.viewmodels.ProductViewModel
import com.shopifydemodemo.app.utils.ApiResponse
import com.shopifydemodemo.app.utils.Urls
import com.shopifydemodemo.app.utils.ViewModelFactory
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File
import java.nio.charset.StandardCharsets
import javax.inject.Inject


class JudgeMeCreateReview : NewBaseActivity(), View.OnClickListener {
    var binding: ActivityJudgeMeCreateReviewBinding? = null
    private val TAG = "JudgeMeCreateReview"
    private var camera: Camera? = null
    private var external_id: String? = null


    @Inject
    lateinit var factory: ViewModelFactory
    private var model: ProductViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val group = findViewById<ViewGroup>(R.id.container)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_judge_me_create_review, group, true)
        (application as MyApplication).mageNativeAppComponent!!.doJudgeMeReviewInjection(this)
        showBackButton()
        showTittle(getString(R.string.write_a_review))
        if (intent.hasExtra("external_id")) {
            external_id = intent.getStringExtra("external_id")
        }
        model = ViewModelProvider(this, factory).get(ProductViewModel::class.java)
        model?.context = this
        model?.getjudgeMeReviewCreate?.observe(this, Observer { this.consumeReviewCreate(it) })
        binding?.addImage?.setOnClickListener(this)
        binding?.ratingBar?.progressTintList= ColorStateList.valueOf(Color.parseColor(themeColor))
        binding?.submitReview?.setOnClickListener(this)
        camera = Camera.Builder()
                .resetToCorrectOrientation(true)//1
                .setTakePhotoRequestCode(Camera.REQUEST_TAKE_PHOTO)//2
                .setDirectory("pics")//3
                .setName("delicious_${System.currentTimeMillis()}")//4
                .setImageFormat(Camera.IMAGE_JPG)//5
                .setCompression(75)//6
                .build(this)
    }

    private fun consumeReviewCreate(response: ApiResponse?) {
        var responseData = JSONObject(response?.data.toString())
        Toast.makeText(this, responseData.getString("message"), Toast.LENGTH_LONG).show()
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onClick(v: View?) {
        if (v?.id == binding?.addImage?.id) {
            Dexter.withContext(this)
                    .withPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(object : DexterBuilder.MultiPermissionListener, MultiplePermissionsListener {
                        override fun withListener(p0: MultiplePermissionsListener?): DexterBuilder {
                            TODO("Not yet implemented")
                        }

                        override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                            p0?.let {
                                if (p0.areAllPermissionsGranted()) {
                                    selectImage(this@JudgeMeCreateReview)
                                }
                            }
                        }

                        override fun onPermissionRationaleShouldBeShown(p0: MutableList<PermissionRequest>?, p1: PermissionToken?) {
                            p1?.continuePermissionRequest()
                        }
                    }).check()
        }
        if (v?.id == binding?.submitReview?.id) {
            rateProduct()
        }
    }

    private fun rateProduct() {
        if (TextUtils.isEmpty(binding?.nameEdt?.text?.toString()?.trim())) {
            binding?.nameEdt?.error = getString(R.string.name_validation)
            binding?.nameEdt?.requestFocus()
        } else if (TextUtils.isEmpty(binding?.titleEdt?.text?.toString()?.trim())) {
            binding?.titleEdt?.error = getString(R.string.review_title_validation)
            binding?.titleEdt?.requestFocus()
        } else if (TextUtils.isEmpty(binding?.bodyEdt?.text?.toString()?.trim())) {
            binding?.bodyEdt?.error = getString(R.string.review_validation)
            binding?.bodyEdt?.requestFocus()
        } else if (TextUtils.isEmpty(binding?.emailEdt?.text?.toString()?.trim())) {
            binding?.emailEdt?.error = getString(R.string.email_validation)
            binding?.emailEdt?.requestFocus()
        } else if (!model?.isValidEmail(binding?.emailEdt?.text?.toString()?.trim()!!)!!) {
            binding?.emailEdt?.error = resources.getString(R.string.invalidemail)
            binding?.emailEdt?.requestFocus()
        } else {
            var params = JsonObject()
            params.addProperty("name", binding?.nameEdt?.text?.toString())
            params.addProperty("email", binding?.emailEdt?.text?.toString())
            params.addProperty("rating", binding?.ratingBar?.rating)
            params.addProperty("title", binding?.titleEdt?.text?.toString())
            params.addProperty("body", binding?.bodyEdt?.text?.toString())
            params.addProperty("id", external_id)
            params.addProperty("url", Urls(application as MyApplication).shopdomain)
            params.addProperty("platform", "shopify")
            model?.judgemeReviewCreate(params)
        }
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

    private fun selectImage(context: Context) {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Choose your profile picture")
        builder.setItems(options, DialogInterface.OnClickListener { dialog, item ->
            if (options[item] == "Take Photo") {
                camera?.takePicture()
            } else if (options[item] == "Choose from Gallery") {
                val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(pickPhoto, 1)
            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        })
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode !== Activity.RESULT_CANCELED) {
            when (requestCode) {
                Camera.REQUEST_TAKE_PHOTO -> if (resultCode === Activity.RESULT_OK) {
                    val selectedImage = camera?.cameraBitmapPath
                    var file = File(selectedImage)
                    if (selectedImage != null) {
                        var file = File(selectedImage)
                        val requestFile: RequestBody = RequestBody.create(
                                MediaType.parse(getMimeType(Uri.fromFile(file))),
                                file
                        )
                        val body: MultipartBody.Part = MultipartBody.Part.createFormData("filename", file.name, requestFile)
                        Log.d(TAG, "onActivityResult: " + body)
                    }
                }
                1 -> if (resultCode === Activity.RESULT_OK && data != null) {
                    val selectedImage = data.data?.path
                    if (selectedImage != null) {
                        var file = File(selectedImage)
                        val requestFile: RequestBody = RequestBody.create(
                                MediaType.parse(getMimeType(Uri.fromFile(file))),
                                file
                        )
                        val body: MultipartBody.Part = MultipartBody.Part.createFormData("filename", file.name, requestFile)
                        Log.d(TAG, "onActivityResult: " + body)
                    }
                }
            }
        }
    }


    fun getMimeType(uri: Uri): String? {
        var mimeType: String? = null
        mimeType = if (ContentResolver.SCHEME_CONTENT.equals(uri.scheme)) {
            val cr: ContentResolver = this.getContentResolver()
            cr.getType(uri)
        } else {
            val fileExtension: String = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString())
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase())
        }
        return mimeType
    }
}