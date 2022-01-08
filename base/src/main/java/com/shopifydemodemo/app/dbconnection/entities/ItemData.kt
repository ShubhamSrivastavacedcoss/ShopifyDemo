package com.shopifydemodemo.app.dbconnection.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

import java.io.Serializable

@Entity
class ItemData : Serializable {
    @PrimaryKey
    @ColumnInfo(name = "product_id")
    lateinit var product_id: String
}
