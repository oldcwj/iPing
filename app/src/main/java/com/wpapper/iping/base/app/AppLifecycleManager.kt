package com.wpapper.iping.base.app

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.atomic.AtomicInteger

object AppLifecycleManager : Application.ActivityLifecycleCallbacks {

    private const val WHAT_NOTIFY_ON_BECOME_FOREGROUND_RUNNING = 0
    private const val WHAT_NOTIFY_ON_BECOME_BACKGROUND_RUNNING = 1

    interface IAppLifecycleListener {
        fun onBecomeForegroundRunning()
        fun onBecomeBackgroundRunning()
    }

    private val activityRefs = hashSetOf<java.lang.ref.WeakReference<Activity>>()

    private var listeners: CopyOnWriteArraySet<IAppLifecycleListener> = CopyOnWriteArraySet()

    private lateinit var score: AtomicInteger

    var currentRunningActivity: Activity? = null

    private val eventHandler: Handler = Handler(Looper.getMainLooper()) { msg ->
        when (msg.what) {
            WHAT_NOTIFY_ON_BECOME_FOREGROUND_RUNNING -> {
                for (listener in listeners) {
                    listener.onBecomeForegroundRunning()
                }
                true
            }
            WHAT_NOTIFY_ON_BECOME_BACKGROUND_RUNNING -> {
                for (listener in listeners) {
                    listener.onBecomeBackgroundRunning()
                }
                true
            }
            else -> {
                false
            }

        }
    }

    val isRunningForeground: Boolean
        get() = score.get() > 0

    fun init(core: Core) {
        core.registerActivityLifecycleCallbacks(this)
        score = AtomicInteger(0)
    }

    fun registerListener(listener: IAppLifecycleListener): Boolean {
        return listeners.add(listener)
    }

    fun unregisterListener(listener: IAppLifecycleListener): Boolean {
        return listeners.remove(listener)
    }

    fun exitApp() {
        activityRefs.forEach { it.get()?.apply{
            if (!isFinishing && !isDestroyed) {
                finish()
            }
        }}
        activityRefs.clear()
    }


    override fun onActivityPaused(activity: android.app.Activity?) {
        if (currentRunningActivity == activity) {
            currentRunningActivity = null
        }
    }

    override fun onActivityResumed(activity: Activity?) {
        currentRunningActivity = activity
    }

    override fun onActivityStarted(activity: android.app.Activity?) {
        if (score.getAndIncrement() == 0) {
            eventHandler.obtainMessage(WHAT_NOTIFY_ON_BECOME_FOREGROUND_RUNNING).sendToTarget()
        }
    }

    override fun onActivityDestroyed(activity: android.app.Activity?) {
//        activityRefs.removeIf { ref -> ref.get() === activity }
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {
        if (score.decrementAndGet() == 0) {
            eventHandler.obtainMessage(WHAT_NOTIFY_ON_BECOME_BACKGROUND_RUNNING).sendToTarget()
        }
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        if (activity != null) {
            activityRefs.add(WeakReference(activity))
        }
    }

}