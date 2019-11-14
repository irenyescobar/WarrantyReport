package com.ireny.randon.frasle.warrantyreport

import android.app.Application
import com.facebook.stetho.Stetho
import com.ireny.randon.frasle.warrantyreport.di.components.ApplicationComponent
import com.ireny.randon.frasle.warrantyreport.di.components.DaggerApplicationComponent
import com.ireny.randon.frasle.warrantyreport.di.modules.ApplicationModule

class MyWarrantReportApp:Application(){

    lateinit var component: ApplicationComponent

    override fun onCreate() {
        super.onCreate()

        initAppComponent()
        component.inject(this)

        if(BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }
    }

    private fun initAppComponent() {
        component = DaggerApplicationComponent
            .builder()
            .applicationModule(ApplicationModule(this))
            .build()
    }

}