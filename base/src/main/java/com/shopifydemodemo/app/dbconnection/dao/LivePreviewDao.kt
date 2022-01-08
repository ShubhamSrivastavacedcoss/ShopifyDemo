package com.shopifydemodemo.app.dbconnection.dao
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.shopifydemodemo.app.dbconnection.entities.LivePreviewData
@Dao
interface LivePreviewDao {

    @get:Query("SELECT * FROM LivePreviewData")
    val getPreviewDetails: List<LivePreviewData>

    @Insert
    fun insert(data: LivePreviewData)

    @Delete
    fun delete(data: LivePreviewData)

    @Update
    fun update(data: LivePreviewData)

}
