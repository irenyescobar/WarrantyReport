package com.ireny.warrantyreport.di.components

import android.content.Context
import com.ireny.warrantyreport.di.modules.ReportDirectoryModule
import com.ireny.warrantyreport.services.ReportDirectoryManager
import dagger.Component

@Component(modules = [ReportDirectoryModule::class])
interface ReportDirectoryComponent {
    fun inject(context: Context)
    fun reportDirectoryManager(): ReportDirectoryManager
}