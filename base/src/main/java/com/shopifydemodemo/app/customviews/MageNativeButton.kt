package com.shopifydemodemo.app.customviews

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log

import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.shopifydemodemo.app.MyApplication

import com.shopifydemodemo.app.R

class MageNativeButton : AppCompatButton {

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs)
        setTextColor(attrs)
        setTextSize(attrs)
        setBack(attrs)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
        setTextColor(attrs)
        setTextSize(attrs)
        setBack(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.MageNativeButton)
            val type = a.getString(R.styleable.MageNativeButton_buttontype)
            try {
                if (type != null && type == "white" ||type =="round") {
                    var typeface: Typeface? = null
                    typeface = Typeface.createFromAsset(context.assets, "fonts/cairoregular.ttf")
                    setTypeface(typeface)
                } else {
                    if (type != null) {
                        var typeface: Typeface? = null
                        if (typeface == null) {
                         typeface = Typeface.createFromAsset(context.assets, "fonts/cairobold.ttf")
//                            typeface = Typeface.createFromAsset(context.assets, "fonts/$type.ttf")
                        }
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
        val a = context.obtainStyledAttributes(attrs, R.styleable.MageNativeButton)
        val type = a.getString(R.styleable.MageNativeButton_buttontype)
        when (type) {
            "bold" -> setTextColor(resources.getColor(R.color.white))
            "normal" -> setTextColor(resources.getColor(R.color.white))
            "white" -> setTextColor(resources.getColor(R.color.white))
        }
        a.recycle()
    }

    private fun setTextSize(attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.MageNativeButton)
        val type = a.getString(R.styleable.MageNativeButton_buttontype)
        when (type) {
            "bold" -> textSize = 15f
            "normal" -> textSize = 13f
            "white" -> textSize = 15f
        }
        a.recycle()
    }

    private fun setBack(attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.MageNativeButton)
        val type = a.getBoolean(R.styleable.MageNativeButton_buttonastext, false)
        if (!type) {
            try {
                MyApplication.dataBaseReference?.child("additional_info")?.child("appthemecolor")?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        /*val value = dataSnapshot.getValue(String::class.java)!!*/
                        var value = dataSnapshot.getValue(String::class.java)!!
                        if (!value.contains("#"))
                        {
                            value= "#"+value
                        }
                        setBackgroundColor(Color.parseColor(value))
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
        } else {
            setBackgroundColor(resources.getColor(R.color.white))
        }
        a.recycle()
    }
}
