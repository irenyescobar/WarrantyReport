package com.ireny.warrantyreport.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ireny.warrantyreport.entities.*
import java.util.*

@Dao
abstract class ReportDao{

    @Query("UPDATE Report SET code =:code, code_generated_at =:generated_at  WHERE id = :reportId")
    abstract suspend fun saveCode(code:String, generated_at: Date, reportId: Long)

    @Transaction
    open suspend fun save(entity: Report){
        entity.id = add(entity)
        deleteAssigneds(entity.id)
        entity.tecnicalAdvices.forEach { el ->
            add(AssignedTechnicalAdvice(entity.id,el.id))
        }
    }

    @Transaction
    open suspend fun delete(reportId: Long){
        deleteAssigneds(reportId)
        deleteReport(reportId)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun add(entity: Report): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun add(entity: AssignedTechnicalAdvice)

    @Query("DELETE FROM AssignedTechnicalAdvice WHERE reportId = :reportId and technicalAdviceId = :tecnicalAdviceId")
    abstract suspend fun deleteAssigned(tecnicalAdviceId:Int, reportId:Long)

    @Query("DELETE FROM AssignedTechnicalAdvice WHERE reportId = :reportId")
    abstract suspend fun deleteAssigneds(reportId:Long)

    @Query("DELETE FROM Report WHERE id = :reportId")
    abstract suspend fun deleteReport(reportId:Long)

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

    @Query("SELECT R.id, R.distributor, R.client, R.created_at, R.code, R.companyId, IFNULL(C.description, '') as company FROM Report R left join Company C on C.id = R.companyId WHERE code IS NULL order by R.id desc")
    abstract fun getPendingReports(): LiveData<List<Report01>>

    @Query("SELECT R.id, R.distributor, R.client, R.created_at, R.code, R.companyId, IFNULL(C.description, '') as company FROM Report R left join Company C on C.id = R.companyId WHERE code IS NOT NULL order by R.id desc")
    abstract fun getCompletedReports(): LiveData<List<Report01>>

    @Query("SELECT * FROM Report WHERE id = :id LIMIT 1")
    abstract fun getReport(id: Long):LiveData<Report>

    @Query("SELECT * FROM AssignedTechnicalAdvice WHERE reportId = :reportId")
    abstract fun getTechnicalAdvices(reportId: Long):LiveData<List<AssignedTechnicalAdvice>>


}