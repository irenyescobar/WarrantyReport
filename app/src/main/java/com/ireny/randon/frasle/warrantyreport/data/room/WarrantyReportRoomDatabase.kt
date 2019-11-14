package com.ireny.randon.frasle.warrantyreport.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ireny.randon.frasle.warrantyreport.data.room.converters.DateConverter
import com.ireny.randon.frasle.warrantyreport.data.room.dao.CompanyDao
import com.ireny.randon.frasle.warrantyreport.data.room.dao.ReportDao
import com.ireny.randon.frasle.warrantyreport.data.room.dao.ReportTypeDao
import com.ireny.randon.frasle.warrantyreport.data.room.dao.TechnicalAdviceDao
import com.ireny.randon.frasle.warrantyreport.entites.*

@Database(entities = [ ReportType::class, TechnicalAdvice::class, Company::class, Report::class, AssignedTechnicalAdvice::class],
          version = 1,
          exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class WarrantyReportRoomDatabase: RoomDatabase() {
    abstract fun reportTypeDao(): ReportTypeDao
    abstract fun technicalAdviceDao(): TechnicalAdviceDao
    abstract fun companyDao(): CompanyDao
    abstract fun reportDao(): ReportDao
}