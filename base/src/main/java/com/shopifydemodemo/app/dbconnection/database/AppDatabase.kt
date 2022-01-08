package com.shopifydemodemo.app.dbconnection.database
import androidx.room.Database
import androidx.room.RoomDatabase
import com.shopifydemodemo.app.dbconnection.dao.AppLocalDataDao
import com.shopifydemodemo.app.dbconnection.dao.CartItemDataDao
import com.shopifydemodemo.app.dbconnection.dao.ItemDataDao
import com.shopifydemodemo.app.dbconnection.dao.LivePreviewDao
import com.shopifydemodemo.app.dbconnection.entities.*

@Database(entities = [AppLocalData::class, UserLocalData::class, CustomerTokenData::class, ItemData::class, CartItemData::class, LivePreviewData::class], version = 10)
abstract class AppDatabase : RoomDatabase() {
    abstract val itemDataDao: ItemDataDao
    abstract val cartItemDataDao: CartItemDataDao
    abstract fun appLocalDataDaoDao(): AppLocalDataDao
    abstract fun getLivePreviewDao(): LivePreviewDao
}
