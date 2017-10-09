package com.wpapper.iping.ui.utils

/**
 * Created by oldcwj@gmail.com on 2017/9/25.
 */
import android.app.Activity
import com.wpapper.iping.ui.utils.hub.SimpleHUD
import iping.wpapper.com.iping.R


object T {

    fun committing(activity: Activity) {
        SimpleHUD.show(activity, R.string.hub_committing, false)
    }

    fun loading(activity: Activity) {
        SimpleHUD.show(activity, R.string.hub_loading, false)
    }

    fun hub(activity: Activity, message: String) {
        SimpleHUD.show(activity, message, false)
    }

    fun dismissHub() {
        SimpleHUD.dismiss()
    }



}
