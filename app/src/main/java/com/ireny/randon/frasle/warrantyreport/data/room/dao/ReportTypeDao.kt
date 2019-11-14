package com.ireny.randon.frasle.warrantyreport.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ireny.randon.frasle.warrantyreport.entites.ReportType


@Dao
interface ReportTypeDao {

    @Query("SELECT * from ReportType WHERE enabled = 1 ORDER BY description ASC")
    fun getAll(): LiveData<List<ReportType>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ReportType)

    @Query("DELETE FROM ReportType")
    suspend fun deleteAll()

}