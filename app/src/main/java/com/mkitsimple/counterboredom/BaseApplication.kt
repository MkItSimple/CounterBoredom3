package com.mkitsimple.counterboredom

import android.app.Application
import com.mkitsimple.counterboredom.di.AppComponent
import com.mkitsimple.counterboredom.di.DaggerAppComponent

class BaseApplication : Application(){

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        this.appComponent = this.initDagger()
    }

    private fun initDagger()  = DaggerAppComponent.builder()
        .build()
}