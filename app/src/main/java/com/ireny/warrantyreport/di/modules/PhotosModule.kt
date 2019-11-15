package com.ireny.warrantyreport.di.modules

import android.content.Context
import com.ireny.warrantyreport.ui.report.services.PhotosManager
import dagger.Module
import dagger.Provides

@Module
class PhotosModule(val context: Context) {

    @Provides
    fun provideContext(): Context = context

    @Provides
    fun providePhotosManager(context: Context): PhotosManager {
        return PhotosManager(context)
    }
}