package com.wpapper.iping.ui.main

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.wpapper.iping.base.annotation.AutoHideKeyboard
import com.wpapper.iping.base.ui.BaseActivity
import com.wpapper.iping.ui.detail.DetailActivity
import com.wpapper.iping.model.DataSave

import iping.wpapper.com.iping.R
import kotlinx.android.synthetic.main.activity_main.*

import com.wpapper.iping.model.SshInfo
import com.wpapper.iping.ui.setting.SettingServerActivity
import net.idik.lib.slimadapter.SlimAdapter
import pieChart

/**
 * top -bn 1 -i -c
 * df -m
 * free -m
 */
@AutoHideKeyboard
class MainActivity : BaseActivity() {

    private val slimAdapter: SlimAdapter by lazy {
        SlimAdapter.create()
                .register<SshInfo>(R.layout.activity_main_item) { info, injector ->
                    injector.pieChart(R.id.layout, info, this@MainActivity)
                            .clicked(R.id.layout) {
                                DetailActivity.start(this@MainActivity, info.host)
                            }
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        slimAdapter.attachTo(recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
        })

        refreshLayout.setOnRefreshListener {
            slimAdapter.notifyDataSetChanged()
            refreshLayout.isRefreshing = false
        }
    }

    override fun onResume() {
        super.onResume()

        val sshInfoList = DataSave(this@MainActivity).getDataList()

        slimAdapter.updateData(sshInfoList)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_main, menu);
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_setting -> SettingServerActivity.start(this, "")
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

}
