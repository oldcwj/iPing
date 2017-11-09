package com.wpapper.iping.ui.utils

import android.util.Log
import com.wpapper.iping.BuildConfig


object L {

    private val TAG_FORMAT = "%s-%s(%d)"

    internal var DEBUG = BuildConfig.DEBUG

    fun i(message: String?) {
        if (DEBUG) {
            Log.i(tag(), message ?: " NO MESSAGE ")
        }
    }

    fun e(e: Exception) {
        if (DEBUG) {
            Log.e(tag(), if (e.message == null) " NO MESSAGE " else e.message, e)
        }
    }

    fun e(message: String?) {
        if (DEBUG) {
            Log.e(tag(), message ?: " NO MESSAGE ")
        }
    }

    private fun tag(): String {
        val throwable = Throwable()
        val target = throwable.stackTrace[2] ?: return "UNKOWN"
        return String.format(TAG_FORMAT, target.className.substring(target.className.lastIndexOf('.') + 1), target.methodName, target.lineNumber)
    }

}
