package com.shopifydemodemo.app.dbconnection.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

import com.shopifydemodemo.app.dbconnection.entities.AppLocalData
import com.shopifydemodemo.app.dbconnection.entities.CustomerTokenData
import com.shopifydemodemo.app.dbconnection.entities.UserLocalData

@Dao
interface AppLocalDataDao {

    @get:Query("SELECT * FROM applocaldata")
    val all: List<AppLocalData>

    /***
     *
     * @return UserLocalData
     */

    @get:Query("SELECT * FROM UserLocalData")
    val allUserData: List<UserLocalData>

    @get:Query("SELECT * FROM CustomerTokenData")
    val customerToken: List<CustomerTokenData>

    @Insert
    fun insert(appLocalData: AppLocalData)

    @Query("DELETE  FROM AppLocalData")
    fun delete()

    @Update
    fun update(appLocalData: AppLocalData)

    @Insert
    fun insertUserData(UserLocalData: UserLocalData)

    @Delete
    fun deleteUserData(UserLocalData: UserLocalData)

    @Update
    fun updateUserData(UserLocalData: UserLocalData)

    @Query("DELETE  FROM UserLocalData")
    fun deletealldata()

    /***
     *
     * @param customerTokenData
     */

    @Insert
    fun InsertCustomerToken(customerTokenData: CustomerTokenData)

    @Update
    fun UpdateCustomerToken(customerTokenData: CustomerTokenData)

    @Delete
    fun deleteCustomerToken(CustomerTokenData: CustomerTokenData)

    @Query("DELETE  FROM CustomerTokenData")
    fun deleteall()

}
