package com.ireny.warrantyreport.di.modules

import android.content.Context
import com.ireny.warrantyreport.services.ReportDirectoryManager
import dagger.Module
import dagger.Provides

@Module
class ReportDirectoryModule(val context: Context) {

    @Provides
    fun provideContext(): Context = context

    @Provides
    fun provideReportDirectoryManager(context: Context): ReportDirectoryManager {
        return ReportDirectoryManager(context)
    }
}