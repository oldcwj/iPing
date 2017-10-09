package com.wpapper.iping.base.ui

import android.os.Bundle
import android.view.View
import com.wpapper.iping.base.annotation.AutoHideKeyboard
import com.wpapper.iping.base.rx.RxFragment
import com.wpapper.iping.ui.utils.KeyboardUtils
import org.jetbrains.anko.AnkoLogger


abstract class BaseFragment() : RxFragment(), AnkoLogger {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (this::class.java.isAnnotationPresent(AutoHideKeyboard::class.java)) {
            KeyboardUtils.setupAutoHideKeyboard(activity, view)
        }
    }
}