package com.ireny.warrantyreport.di.components

import com.ireny.warrantyreport.MyWarrantReportApp
import com.ireny.warrantyreport.data.retrofit.Api
import com.ireny.warrantyreport.di.modules.ApplicationModule
import com.ireny.warrantyreport.repositories.CompanyRepository
import com.ireny.warrantyreport.repositories.ReportRepository
import com.ireny.warrantyreport.repositories.ReportTypeRepository
import com.ireny.warrantyreport.repositories.TechnicalAdviceRepository
import com.ireny.warrantyreport.services.DataHelperService
import com.ireny.warrantyreport.services.UserAccountManager
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ApplicationModule::class))
interface ApplicationComponent {
    fun inject(application: MyWarrantReportApp)
    fun dataHelperService(): DataHelperService
    fun reportTypeRepository(): ReportTypeRepository
    fun companyRepository(): CompanyRepository
    fun technicalAdviceRepository(): TechnicalAdviceRepository
    fun reportRepository(): ReportRepository
    fun userAccountManager(): UserAccountManager
    fun api(): Api
}