package com.shopifydemodemo.app.utils

import android.util.Log
import java.lang.Double
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object CurrencyFormatter {
    fun setsymbol(data: String, currency_symbol: String): String {
        Log.i("MageNative", "Amount : $data")
        val format = NumberFormat.getCurrencyInstance(Locale.ENGLISH)
        format.currency = Currency.getInstance(currency_symbol)
        return format.format(Double.valueOf(data))
    }
}
