package com.shopifydemodemo.app.dbconnection.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

import com.shopifydemodemo.app.dbconnection.entities.ItemData

@Dao
interface ItemDataDao {

    @get:Query("SELECT * FROM itemdata")
    val all: List<ItemData>

    @get:Query("SELECT * FROM itemdata")
    val wish_count: LiveData<List<ItemData>>

    @Query("SELECT * FROM itemdata WHERE product_id = :id")
    fun getSingleData(id: String): ItemData

    @Insert
    fun insert(data: ItemData)

    @Delete
    fun delete(data: ItemData)

    @Update
    fun update(data: ItemData)

    @Query("DELETE  FROM itemdata")
    fun deleteall()

}
