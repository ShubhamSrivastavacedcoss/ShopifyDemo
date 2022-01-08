package com.shopifydemodemo.app.dbconnection.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

import java.io.Serializable

@Entity
class AppLocalData : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @ColumnInfo(name = "istrialexpire")
    var isIstrialexpire: Boolean = false

    @ColumnInfo(name = "trialexpiredata")
    var trialexpiredata: String? = null

    @ColumnInfo(name = "currencycode")
    var currencycode: String? = null

}
