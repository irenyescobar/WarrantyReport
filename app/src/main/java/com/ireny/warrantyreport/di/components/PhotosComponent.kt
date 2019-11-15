package com.ireny.warrantyreport.di.components

import android.content.Context
import com.ireny.warrantyreport.di.modules.PhotosModule
import com.ireny.warrantyreport.ui.report.services.PhotosManager
import dagger.Component

@Component(modules = [PhotosModule::class])
interface PhotosComponent {
    fun inject(context: Context)
    fun photoManager(): PhotosManager
}