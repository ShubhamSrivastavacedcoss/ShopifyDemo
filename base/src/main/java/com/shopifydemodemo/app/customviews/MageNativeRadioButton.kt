package com.shopifydemodemo.app.customviews

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet

import androidx.appcompat.widget.AppCompatRadioButton

import com.shopifydemodemo.app.R

class MageNativeRadioButton : AppCompatRadioButton {

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs)
        setTextColor(attrs)
        setTextSize(attrs)

    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
        setTextColor(attrs)
        setTextSize(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.MageNativeRadioButton)
            val type = a.getString(R.styleable.MageNativeRadioButton_radiotype)
            try {
                if (type != null) {
                    var typeface: Typeface? = null
                    if (typeface == null) {
//                        typeface = Typeface.createFromAsset(context.assets, "fonts/$type.ttf")
                      typeface = Typeface.createFromAsset(context.assets, "fonts/cairobold.ttf")
                    }
                    setTypeface(typeface)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            a.recycle()
        }
    }

    private fun setTextColor(attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.MageNativeRadioButton)
        val type = a.getString(R.styleable.MageNativeRadioButton_radiotype)
        when (type) {
            "bold" -> setTextColor(resources.getColor(R.color.black))
            "normal" -> setTextColor(resources.getColor(R.color.black))
        }
    }

    private fun setTextSize(attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.MageNativeRadioButton)
        val type = a.getString(R.styleable.MageNativeRadioButton_radiotype)
        when (type) {
            "bold" -> textSize = 15f
            "normal" -> textSize = 13f
        }
    }
}
