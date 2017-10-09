package com.wpapper.iping.ui.utils

import android.app.Activity
import android.text.Selection
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import org.jetbrains.anko.inputMethodManager
import org.jetbrains.anko.onTouch
import java.util.concurrent.TimeUnit


object KeyboardUtils {

    private fun showKeyboardWithOption(editText: EditText, selectAll: Boolean, selectionEnd: Boolean) {
        Observable.just(editText)
                .subscribeOn(AndroidSchedulers.mainThread())
                .delaySubscription(200, TimeUnit.MILLISECONDS)
                .subscribe({
                    editText.requestFocus()
                    editText.context.inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
                    if (selectionEnd) {
                        editText.setSelection(editText.text.length)
                    }
                    if (selectAll) {
                        Selection.selectAll(editText.text)
                    }
                }, { it.printStackTrace() })
    }

    private fun hideKeyboard(activity: Activity) {
        try {
            activity.inputMethodManager.hideSoftInputFromWindow(
                    activity.currentFocus!!.windowToken, 0)
        } catch (e: Exception) {

        }
    }

    fun setupAutoHideKeyboard(activity: Activity, root: View?) {
        if (root == null) {
            return
        }
        if (root !is EditText) {
            // FIXME: 如果已经设置了TouchListener呢？
            root.onTouch { _, _ ->
                hideKeyboard(activity)
                false
            }
        }
        if (root is ViewGroup) {
            for (i in 0..root.childCount - 1) {
                val innerView = root.getChildAt(i)
                setupAutoHideKeyboard(activity, innerView)
            }
        }
    }


    fun showKeyboardAndSelectAll(editText: EditText) {
        showKeyboardWithOption(editText, true, false)
    }

    fun showKeyboard(editText: EditText) {
        showKeyboardWithOption(editText, false, false)
    }

    fun showKeyboardAndSelectEnd(editText: EditText) {
        showKeyboardWithOption(editText, false, true)
    }

    interface KeyboardAction {
        fun actionOnChange(isShow: Boolean)
    }
}
