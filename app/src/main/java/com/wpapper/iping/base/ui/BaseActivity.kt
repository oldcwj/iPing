package com.wpapper.iping.base.ui


import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import com.gyf.barlibrary.ImmersionBar
import com.wpapper.iping.R
import com.wpapper.iping.base.annotation.AutoHideKeyboard
import com.wpapper.iping.base.annotation.Backable
import com.wpapper.iping.base.rx.RxActivity
import com.wpapper.iping.ui.utils.KeyboardUtils

import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.contentView
import org.jetbrains.anko.findOptional


abstract class BaseActivity : RxActivity(), AnkoLogger {

    protected val toolbar: Toolbar? by lazy {
        findOptional<Toolbar>(R.id.toolbar)?.apply {
            setSupportActionBar(this)
            if (this@BaseActivity::class.java.isAnnotationPresent(Backable::class.java)) {
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                this.setNavigationOnClickListener { onBackPressed() }
            }
        }
    }

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)

        ImmersionBar.with(this).navigationBarEnable(false).apply {
//            if (ImmersionBar.isSupportStatusBarDarkFont()) {
//                statusBarDarkFont(true)
//            } else {
//                statusBarAlpha(0.2f)
//            }
        }.init()

    }

    override fun onDestroy() {
        ImmersionBar.with(this).destroy()
        super.onDestroy()
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        initBase()
    }

    private fun initBase() {
        toolbar
        if (this::class.java.isAnnotationPresent(AutoHideKeyboard::class.java)) {
            KeyboardUtils.setupAutoHideKeyboard(this, contentView)
        }
    }

    override fun setContentView(view: View?) {
        super.setContentView(view)
        initBase()
    }

    override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
        super.setContentView(view, params)
        initBase()
    }

//    override fun startActivityForResult(intent: Intent?, requestCode: Int, options: Bundle?) {
//        super.startActivityForResult(intent, requestCode, options)
//        try {
//            if (Class.forName(intent?.component?.className).isAnnotationPresent(Backable::class.java)) {
//                overridePendingTransition(R.anim.activity_open_enter, R.anim.activity_open_exit)
//            }
//        } catch (e: Exception) {
//
//        }
//    }

    override fun finish() {
        if (this::class.java.isAnnotationPresent(AutoHideKeyboard::class.java)) {
            KeyboardUtils.setupAutoHideKeyboard(this, contentView)
        }
        super.finish()
        if (this::class.java.isAnnotationPresent(Backable::class.java)) {
            overridePendingTransition(R.anim.activity_close_enter, R.anim.activity_close_exit)
        }
    }
}