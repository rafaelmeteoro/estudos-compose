package com.meteoro.jetnews

import android.app.Application
import com.meteoro.jetnews.data.AppContainer
import com.meteoro.jetnews.data.AppContainerImpl

class JetNewsApplication : Application() {

    // AppContainer instance used by the rest of classes to obtain dependencies
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)
    }
}