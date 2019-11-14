package com.ireny.randon.frasle.warrantyreport.di.modules

import android.content.Context
import com.ireny.randon.frasle.warrantyreport.ui.report.photos.PhotosManager
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