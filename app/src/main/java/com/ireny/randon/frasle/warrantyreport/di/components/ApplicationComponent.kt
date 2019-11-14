package com.ireny.randon.frasle.warrantyreport.di.components

import com.ireny.randon.frasle.warrantyreport.MyWarrantReportApp
import com.ireny.randon.frasle.warrantyreport.di.modules.ApplicationModule
import com.ireny.randon.frasle.warrantyreport.repositorys.CompanyRepository
import com.ireny.randon.frasle.warrantyreport.repositorys.ReportRepository
import com.ireny.randon.frasle.warrantyreport.repositorys.ReportTypeRepository
import com.ireny.randon.frasle.warrantyreport.repositorys.TechnicalAdviceRepository
import com.ireny.randon.frasle.warrantyreport.services.ImportDataService
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ApplicationModule::class))
interface ApplicationComponent {
    fun inject(application: MyWarrantReportApp)
    fun importDataService(): ImportDataService
    fun reportTypeRepository(): ReportTypeRepository
    fun companyRepository(): CompanyRepository
    fun technicalAdviceRepository(): TechnicalAdviceRepository
    fun reportRepository(): ReportRepository
}