package com.shopifydemodemo.app.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.LevelListDrawable
import android.os.AsyncTask
import android.util.Log
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL


internal  class LoadImage : AsyncTask<Any?, Void?, Bitmap?>() {
    private var mDrawable: LevelListDrawable? = null


     override fun onPostExecute(bitmap: Bitmap?) {
        Log.d("TAG", "onPostExecute drawable $mDrawable")
        Log.d("TAG", "onPostExecute bitmap $bitmap")
        if (bitmap != null) {
            val d = BitmapDrawable(bitmap)
            mDrawable!!.addLevel(1, 1, d)
            mDrawable!!.setBounds(0, 0, bitmap.width, bitmap.height)
            mDrawable!!.level = 1
            // i don't know yet a better way to refresh TextView
            // mTv.invalidate() doesn't work as expected
            /*val t: CharSequence = mTv.getText()
            mTv.setText(t)*/
        }
    }

    override fun doInBackground(vararg p0: Any?): Bitmap? {
        val source = p0[0] as String
        mDrawable = p0[1] as LevelListDrawable
        Log.d("TAG", "doInBackground $source")
        try {
            val `is`: InputStream = URL(source).openStream()
            return BitmapFactory.decodeStream(`is`)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }


}