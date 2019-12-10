package com.ireny.warrantyreport.di.modules

import android.content.Context
import androidx.room.Room
import com.ireny.warrantyreport.MyWarrantReportApp
import com.ireny.warrantyreport.data.room.WarrantyReportRoomDatabase
import com.ireny.warrantyreport.data.room.migrations.MIGRATION_1_2
import com.ireny.warrantyreport.repositories.CompanyRepository
import com.ireny.warrantyreport.repositories.ReportRepository
import com.ireny.warrantyreport.repositories.ReportTypeRepository
import com.ireny.warrantyreport.repositories.TechnicalAdviceRepository
import com.ireny.warrantyreport.services.ImportDataService
import com.ireny.warrantyreport.services.UserAccountManager
import com.ireny.warrantyreport.utils.Constants
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
        ).addMigrations(MIGRATION_1_2).build()
        return build
    @Provides
    @Singleton
    fun provideUserAccountManager(context:Context): UserAccountManager {
        return UserAccountManager(context)
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