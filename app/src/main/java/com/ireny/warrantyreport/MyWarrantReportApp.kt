package com.ireny.warrantyreport

import android.app.Application
import android.content.Context
import com.facebook.stetho.Stetho
import com.ireny.warrantyreport.di.components.ApplicationComponent
import com.ireny.warrantyreport.di.components.DaggerApplicationComponent
import com.ireny.warrantyreport.di.modules.ApplicationModule

class MyWarrantReportApp:Application(){

    init {
        instance = this
    }

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

    companion object {
        private var instance: MyWarrantReportApp? = null
        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }

}