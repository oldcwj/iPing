package com.wpapper.iping.base.ui

import android.support.annotation.CallSuper
import iping.wpapper.com.iping.R
import org.jetbrains.anko.find


abstract class BaseStatefulActivity : BaseActivity() {

    val statefulLayout by lazy { find<com.gturedi.views.StatefulLayout>(R.id.stateful_layout) }
    private val statefulViewContainer by lazy { find<android.widget.FrameLayout>(R.id.stateful_view_container) }

    @CallSuper
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        super.setContentView(R.layout.activity_stateful_base)
    }

    override fun setContentView(layoutResID: Int) {
        statefulViewContainer.apply {
            removeAllViews();
            addView(layoutInflater.inflate(layoutResID, statefulViewContainer, false))
        }
    }

    override fun setContentView(view: android.view.View?) {
        statefulViewContainer.apply {
            removeAllViews();
            addView(view)
        }
    }

    override fun setContentView(view: android.view.View?, params: android.view.ViewGroup.LayoutParams?) {
        statefulViewContainer.apply {
            removeAllViews();
            addView(view, params)
        }
    }

}