package com.ireny.randon.frasle.warrantyreport.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ireny.randon.frasle.warrantyreport.entites.*

@Dao
abstract class ReportDao{

    @Transaction
    open suspend fun save(entity: Report){
        entity.id = add(entity)
        deleteAssigneds(entity.id)
        entity.tecnicalAdvices.forEach { el ->
            add(AssignedTechnicalAdvice(entity.id,el.id))
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun add(entity: Report): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun add(entity: AssignedTechnicalAdvice)

    @Query("DELETE FROM AssignedTechnicalAdvice WHERE reportId = :reportId and technicalAdviceId = :tecnicalAdviceId")
    abstract suspend fun deleteAssigned(tecnicalAdviceId:Int, reportId:Long)

    @Query("DELETE FROM AssignedTechnicalAdvice WHERE reportId = :reportId")
    abstract suspend fun deleteAssigneds(reportId:Long)

    @Query("SELECT * FROM Report WHERE id = :id LIMIT 1")
    abstract suspend fun getReportById(id: Long):Report

    @Query("SELECT * FROM TechnicalAdvice WHERE id = :id LIMIT 1")
    abstract suspend fun getTechnicalAdvice(id: Int):TechnicalAdvice?

    @Query("SELECT * FROM ReportType WHERE id = :id LIMIT 1")
    abstract suspend fun getReportType(id: Int):ReportType?

    @Query("SELECT * FROM Company WHERE id = :id LIMIT 1")
    abstract suspend fun getCompany(id: Int):Company?

    @Query("SELECT * FROM AssignedTechnicalAdvice WHERE reportId = :reportId")
    abstract suspend fun getAssignedTechnicalAdvices(reportId: Long):List<AssignedTechnicalAdvice>

    @Query("SELECT * FROM Report")
    abstract fun getReports(): LiveData<List<Report>>

    @Query("SELECT * FROM Report WHERE id = :id LIMIT 1")
    abstract fun getReport(id: Long):LiveData<Report>

    @Query("SELECT * FROM AssignedTechnicalAdvice WHERE reportId = :reportId")
    abstract fun getTechnicalAdvices(reportId: Long):LiveData<List<AssignedTechnicalAdvice>>


}