package com.ireny.warrantyreport.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ireny.warrantyreport.entities.Company

@Dao
interface CompanyDao {

    @Query("SELECT * from Company WHERE enabled = 1 ORDER BY description ASC")
    fun getAll(): LiveData<List<Company>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: Company)

    @Query("DELETE FROM Company")
    suspend fun deleteAll()
}