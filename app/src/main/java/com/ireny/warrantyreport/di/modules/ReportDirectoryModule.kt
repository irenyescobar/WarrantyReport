package com.ireny.warrantyreport.di.modules

import android.content.Context
import com.ireny.warrantyreport.ui.report.services.ReportDirectoryManager
import dagger.Module
import dagger.Provides

@Module
class ReportDirectoryModule(val context: Context) {

    @Provides
    fun provideContext(): Context = context

    @Provides
    fun providePhotosManager(context: Context): ReportDirectoryManager {
        return ReportDirectoryManager(context)
    }
}