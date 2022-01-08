package com.shopifydemodemo.app.customviews

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet

import androidx.appcompat.widget.AppCompatEditText

import com.shopifydemodemo.app.R

class MageNativeEditText : AppCompatEditText {

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs)
        setTextColor(attrs)
        setSize(attrs)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
        setTextColor(attrs)
        setSize(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.MageNativeEditText)
            val type = a.getString(R.styleable.MageNativeEditText_edittype)
            try {
                if (type != null && type == "white") {
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

    private fun setTextColor(attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.MageNativeEditText)
        val type = a.getString(R.styleable.MageNativeEditText_edittype)
        when (type) {
            "bold" -> setTextColor(resources.getColor(R.color.black))
            "normal" -> setTextColor(resources.getColor(R.color.black))
            "white" -> setTextColor(resources.getColor(R.color.white))
        }
    }

    private fun setSize(attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.MageNativeEditText)
        val type = a.getString(R.styleable.MageNativeEditText_edittype)
        when (type) {
            "bold" -> textSize = 15f
            "normal" -> textSize = 13f
            "white" -> textSize = 15f
        }
    }
}
