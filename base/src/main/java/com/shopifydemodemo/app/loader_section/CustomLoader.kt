package com.shopifydemodemo.app.loader_section

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.shopifydemodemo.app.MyApplication
import com.shopifydemodemo.app.R
import kotlinx.android.synthetic.main.activity_custom_loader.*
import java.util.*

class CustomLoader(context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Objects.requireNonNull(this.getWindow())!!.setBackgroundDrawableResource(android.R.color.transparent);
        setContentView(R.layout.activity_custom_loader)
        this.setCancelable(false);

        MyApplication.dataBaseReference?.child("additional_info")?.child("appthemecolor")?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                /*val value = dataSnapshot.getValue(String::class.java)!!*/
                var value = dataSnapshot.getValue(String::class.java)!!
                if (!value.contains("#")) {
                    value = "#" + value
                }
                spinkit.setColor(Color.parseColor(value))
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.i("DBConnectionError", "" + databaseError.details)
                Log.i("DBConnectionError", "" + databaseError.message)
                Log.i("DBConnectionError", "" + databaseError.code)
            }
        })
    }
}