package com.ireny.randon.frasle.warrantyreport.di.modules

import android.content.Context
import androidx.room.Room
import com.ireny.randon.frasle.warrantyreport.MyWarrantReportApp
import com.ireny.randon.frasle.warrantyreport.data.room.WarrantyReportRoomDatabase
import com.ireny.randon.frasle.warrantyreport.repositorys.CompanyRepository
import com.ireny.randon.frasle.warrantyreport.repositorys.ReportRepository
import com.ireny.randon.frasle.warrantyreport.repositorys.ReportTypeRepository
import com.ireny.randon.frasle.warrantyreport.repositorys.TechnicalAdviceRepository
import com.ireny.randon.frasle.warrantyreport.services.ImportDataService
import com.ireny.randon.frasle.warrantyreport.utils.Constants
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule(val application: MyWarrantReportApp) {

    @Provides
    @Singleton
    fun provideAppContext(): Context = application

    @Provides
    @Singleton
    fun provideRoomDatabase(context:Context): WarrantyReportRoomDatabase {
        val build = Room.databaseBuilder(
            context,
            WarrantyReportRoomDatabase::class.java,
            Constants.DATABASE_NAME
        ).build()
        return build
    }

    @Provides
    fun provideReportTypeRepository(database: WarrantyReportRoomDatabase): ReportTypeRepository{
        return ReportTypeRepository(database.reportTypeDao())
    }

    @Provides
    fun provideRCompanyRepository(database: WarrantyReportRoomDatabase): CompanyRepository{
        return CompanyRepository(database.companyDao())
    }

    @Provides
    fun provideTechnicalAdviceRepository(database: WarrantyReportRoomDatabase): TechnicalAdviceRepository{
        return TechnicalAdviceRepository(database.technicalAdviceDao())
    }

    @Provides
    fun provideReportRepository(database: WarrantyReportRoomDatabase): ReportRepository{
        return ReportRepository(database.reportDao())
    }

    @Provides
    fun provideImportDataService(database: WarrantyReportRoomDatabase): ImportDataService{
        return ImportDataService(database)
    }

}