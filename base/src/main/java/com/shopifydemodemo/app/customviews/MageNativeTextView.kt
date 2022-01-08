package com.shopifydemodemo.app.customviews

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log

import androidx.appcompat.widget.AppCompatTextView

import com.shopifydemodemo.app.R

import java.util.Objects

class MageNativeTextView : AppCompatTextView {

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs)
        set_TextColor(attrs)
        setSize(attrs)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
        set_TextColor(attrs)
        setSize(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.MageNativeTextView)
            val type = a.getString(R.styleable.MageNativeTextView_texttype)
            try {
                Log.i("MageNative", "TextView : " + type!!)
                if (type != null && type == "whitetext") {
                    var typeface: Typeface? = null
                    typeface = Typeface.createFromAsset(context.assets, "fonts/cairoregular.ttf")
                    setTypeface(typeface)
                } else {
                    if (type != null) {
//                        val typeface = Typeface.createFromAsset(context.assets, "fonts/$type.ttf")
                        val typeface = Typeface.createFromAsset(context.assets, "fonts/cairobold.ttf")
                        setTypeface(typeface)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            a.recycle()
        }
    }

    private fun set_TextColor(attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.MageNativeTextView)
        val type = a.getString(R.styleable.MageNativeTextView_texttype)
        when (Objects.requireNonNull<String>(type)) {
            "bold" -> setTextColor(resources.getColor(R.color.black))
            "normal" -> setTextColor(resources.getColor(R.color.black))
            "whitetext" -> setTextColor(resources.getColor(R.color.white))
            else -> {
            }
        }
        a.recycle()
    }

    private fun setSize(attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.MageNativeTextView)
        val type = a.getString(R.styleable.MageNativeTextView_texttype)
        when (Objects.requireNonNull<String>(type)) {
            "bold" -> textSize = 15f
            "normal" -> textSize = 11f
            "whitetext" -> textSize = 13f
        }
        a.recycle()
    }
}
