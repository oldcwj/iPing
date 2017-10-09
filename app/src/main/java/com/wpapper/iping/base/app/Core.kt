package com.wpapper.iping.base.app

import android.app.Application
import android.content.Context

class Core : Application(), AppLifecycleManager.IAppLifecycleListener {

    override fun onCreate() {
        super.onCreate()
        _instance = this
        AppLifecycleManager.init(this)
        AppLifecycleManager.registerListener(this)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

    override fun onBecomeForegroundRunning() {
    }

    override fun onBecomeBackgroundRunning() {
    }

    companion object {
        private var _instance: Core? = null
        val instance: Core
            get() = _instance!!
    }

}