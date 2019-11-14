package com.ireny.warrantyreport.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ireny.warrantyreport.entities.TechnicalAdvice

@Dao
interface TechnicalAdviceDao {

    @Query("SELECT * from TechnicalAdvice WHERE enabled = 1 ORDER BY description ASC")
    fun getAll(): LiveData<List<TechnicalAdvice>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: TechnicalAdvice)

    @Query("DELETE FROM TechnicalAdvice")
    suspend fun deleteAll()
}