package com.shopifydemodemo.app.basesection.models

import android.graphics.*
import android.util.Log
import android.widget.ImageView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.BindingAdapter
import androidx.databinding.library.baseAdapters.BR
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.shopifydemodemo.app.MyApplication.Companion.context
import com.shopifydemodemo.app.R
import com.shopifydemodemo.app.basesection.activities.Splash
import com.shopifydemodemo.app.loginsection.activity.LoginActivity
import com.shopifydemodemo.app.loginsection.activity.RegistrationActivity
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

private val TAG = "CommanModel"
fun <String> ImageView.loadCircularImage(
        model: kotlin.String,
        borderSize: Float = 0F,
        borderColor: Int = Color.TRANSPARENT
) {
    Glide.with(context)
            .asBitmap()
            .load(model)
            .placeholder(R.drawable.image_placeholder)
            .error(R.drawable.image_placeholder)
            .apply(RequestOptions.circleCropTransform())
            .into(object : BitmapImageViewTarget(this) {
                override fun setResource(resource: Bitmap?) {
                    setImageDrawable(
                            resource?.run {
                                RoundedBitmapDrawableFactory.create(
                                        resources,
                                        if (borderSize > 0) {
                                            createBitmapWithBorder(borderSize, borderColor)
                                        } else {
                                            this
                                        }
                                ).apply {
                                    isCircular = true
                                }
                            }
                    )
                }
            })
}

fun Bitmap.createBitmapWithBorder(borderSize: Float, borderColor: Int = Color.WHITE): Bitmap {
    val borderOffset = (borderSize * 2).toInt()
    val halfWidth = width / 2
    val halfHeight = height / 2
    val circleRadius = Math.min(halfWidth, halfHeight).toFloat()
    val newBitmap = Bitmap.createBitmap(
            width + borderOffset,
            height + borderOffset,
            Bitmap.Config.ARGB_8888
    )

    // Center coordinates of the image
    val centerX = halfWidth + borderSize
    val centerY = halfHeight + borderSize

    val paint = Paint()
    val canvas = Canvas(newBitmap).apply {
        // Set transparent initial area
        drawARGB(0, 0, 0, 0)
    }

    // Draw the transparent initial area
    paint.isAntiAlias = true
    paint.style = Paint.Style.FILL
    canvas.drawCircle(centerX, centerY, circleRadius, paint)

    // Draw the image
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(this, borderSize, borderSize, paint)

    // Draw the createBitmapWithBorder
    paint.xfermode = null
    paint.style = Paint.Style.STROKE
    paint.color = borderColor
    paint.strokeWidth = borderSize
    canvas.drawCircle(centerX, centerY, circleRadius, paint)
    return newBitmap
}

class CommanModel : BaseObservable() {
    @get:Bindable
    var imageurl: String? = null
        set(imageurl) {
            field = imageurl
            notifyPropertyChanged(BR.imageurl)
        }


    companion object {
        @BindingAdapter("imageUrl")
        @JvmStatic
        fun loadImage(view: ImageView, imageUrl: String?) {
            if (view.context is Splash || view.context is LoginActivity || view.context is RegistrationActivity) {
                Glide.with(context)
                        .asBitmap()
                        .load(imageUrl)
                        .priority(Priority.HIGH)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(view)
            } else {
                Glide.with(context)
                        .asBitmap()
                        .load(imageUrl)
                        .placeholder(R.drawable.image_placeholder)
                        .error(R.drawable.image_placeholder)
                        .priority(Priority.HIGH)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(view)
            }
        }

        @BindingAdapter("circleImageUrl")
        @JvmStatic
        fun circleLoadImage(view: ImageView, imageUrl: String?) {
            Log.d(TAG, "circleLoadImage: " + imageUrl)
            val observable = Observable.fromCallable { imageUrl }
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<String?> {
                        override fun onSubscribe(d: Disposable) {
                        }

                        override fun onNext(s: String) {
                            view.loadCircularImage<String>(s, 2f, Color.parseColor(view.tag.toString()))
                        }

                        override fun onError(e: Throwable) {
                        }

                        override fun onComplete() {
                        }
                    })
        }

        @BindingAdapter("radius", "url")
        @JvmStatic
        fun Image(view: ImageView, radius: String?, url: String?) {

            val observable = Observable.fromCallable { url }
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<String?> {
                        override fun onSubscribe(d: Disposable) {

                        }

                        override fun onNext(s: String) {
                            var round: RequestOptions
                            when (radius) {
                                "0" -> {
                                    round = RequestOptions().placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).dontTransform().diskCacheStrategy(DiskCacheStrategy.ALL)
                                }
                                else -> {
                                    round = RequestOptions().placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).transform(RoundedCorners(radius!!.toInt())).diskCacheStrategy(DiskCacheStrategy.ALL)
                                }
                            }
                            Glide.with(view.context)
                                    .load(url)
                                    .thumbnail(0.5f)
                                    .apply(round)
                                    .into(view)

                        }

                        override fun onError(e: Throwable) {

                        }

                        override fun onComplete() {

                        }
                    })
        }
    }


}
