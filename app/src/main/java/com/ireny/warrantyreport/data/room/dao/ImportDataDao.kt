package com.ireny.warrantyreport.data.room.dao

import androidx.room.*
import com.ireny.warrantyreport.entities.*

@Dao
abstract class ImportDataDao {

    @Transaction
    open suspend fun importData(data: WarrantReportData){

        deleteAssignedTechnicalAdvices()
        deleteReports()
        deleteTechnicalAdvices()
        deleteCompanys()
        deleteReportTypes()

        data.reportTypes.forEach {
            insert(it)
        }

        data.companys.forEach {
            insert(it)
        }

        data.technicalAdvices.forEach {
            insert(it)
        }

        data.reports.forEach {
            insert(it)
        }

        data.assignedTechnicalAdvices.forEach {
            insert(it)
        }
    }


    suspend fun clear(){
        deleteAssignedTechnicalAdvices()
        deleteReports()
        deleteTechnicalAdvices()
        deleteCompanys()
        deleteReportTypes()
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(entity: ReportType)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(entity: Company)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(entity: TechnicalAdvice)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(entity: Report)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(entity: AssignedTechnicalAdvice)

    @Query("DELETE FROM AssignedTechnicalAdvice")
    abstract suspend fun deleteAssignedTechnicalAdvices()

    @Query("DELETE FROM Report")
    abstract suspend fun deleteReports()

    @Query("DELETE FROM TechnicalAdvice")
    abstract suspend fun deleteTechnicalAdvices()

    @Query("DELETE FROM Company")
    abstract suspend fun deleteCompanys()

    @Query("DELETE FROM ReportType")
    abstract suspend fun deleteReportTypes()

}