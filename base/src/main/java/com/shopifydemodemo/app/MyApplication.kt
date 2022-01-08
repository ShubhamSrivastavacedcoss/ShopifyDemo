package com.shopifydemodemo.app

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.shopifydemodemo.app.dependecyinjection.MageNativeAppComponent
import com.shopifydemodemo.app.dependecyinjection.DaggerMageNativeAppComponent
import com.shopifydemodemo.app.dependecyinjection.UtilsModule
import com.shopifydemodemo.app.sharedprefsection.MagePrefs
import net.danlew.android.joda.JodaTimeAndroid

class MyApplication : MultiDexApplication() {
    var mageNativeAppComponent: MageNativeAppComponent? = null

    override fun onCreate() {
        super.onCreate()
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        FacebookSdk.setAutoInitEnabled(true)
        FacebookSdk.fullyInitialize()
        JodaTimeAndroid.init(this)
        MagePrefs.getInstance(this)
        context = this
        mageNativeAppComponent = DaggerMageNativeAppComponent.builder().utilsModule(UtilsModule(this)).build()
        val options = FirebaseOptions.Builder()
                .setProjectId("shopify-dev-project-2f51e")
                .setApplicationId("1:445702503308:android:f8dee0b320adfdaa68b4a9") // Required for Analytics.
                .setApiKey("AIzaSyD0GhHgrwqVQC7m3LBOkoxVzVefP6EQAZw") // Required for Auth.
                .setDatabaseUrl("https://shopify-dev-project-2f51e.firebaseio.com/") // Required for RTDB.
                .build()

//        val options = FirebaseOptions.Builder()
//                .setProjectId("live-shopify-project")
//                .setApplicationId("1:322600045606:android:ccd0e8d87b47235fab6ae7") // Required for Analytics.
//                .setApiKey("AIzaSyC1LTEUGgrKWBDVRV0VMQJOCN2O-UyVKr4") // Required for Auth.
//                .setDatabaseUrl("https://live-shopify-project.firebaseio.com/") // Required for RTDB.
//                .build()
        firebaseapp = FirebaseApp.initializeApp(this /* Context */, options, "MageNative")

    }

    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(context)
    }

    companion object {
        lateinit var context: MyApplication
        lateinit var firebaseapp: FirebaseApp
        private var mFirebaseSecondanyInstance: FirebaseDatabase? = null
        var flag: Boolean = false;
        fun getmFirebaseSecondanyInstance(): FirebaseDatabase {
            if (mFirebaseSecondanyInstance == null) {
                val secondary = FirebaseApp.getInstance("MageNative")
                mFirebaseSecondanyInstance = FirebaseDatabase.getInstance(secondary)
            }
            return mFirebaseSecondanyInstance as FirebaseDatabase
        }

        var dataBaseReference: DatabaseReference? = null
    }
}
