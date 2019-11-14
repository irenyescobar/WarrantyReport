package com.ireny.randon.frasle.warrantyreport.di.components

import android.content.Context
import com.ireny.randon.frasle.warrantyreport.di.modules.PhotosModule
import com.ireny.randon.frasle.warrantyreport.ui.report.photos.PhotosManager
import dagger.Component

@Component(modules = [PhotosModule::class])
interface PhotosComponent {
    fun inject(context: Context)
    fun photoManager(): PhotosManager
}