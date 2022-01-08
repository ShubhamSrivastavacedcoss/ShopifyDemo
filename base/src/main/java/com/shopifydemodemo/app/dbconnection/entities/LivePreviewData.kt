package com.shopifydemodemo.app.dbconnection.entities
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
@Entity(tableName = "LivePreviewData")
class LivePreviewData : Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @ColumnInfo(name = "mid")
    var mid: String? = null

    @ColumnInfo(name = "shopurl")
    var shopurl: String? = null

    @ColumnInfo(name = "apikey")
    var apikey: String? = null

    constructor( mid: String, shopurl: String, apikey: String) {
        this.mid = mid
        this.shopurl = shopurl
        this.apikey = apikey
    }
}
