package com.wpapper.iping.ui.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import com.wpapper.iping.R
import com.wpapper.iping.base.ui.BaseActivity
import com.wpapper.iping.model.CmdParser
import com.wpapper.iping.model.DataSave
import com.wpapper.iping.model.Info
import com.wpapper.iping.ui.utils.SSHManager
import com.wpapper.iping.ui.utils.exts.subscribeNext
import com.wpapper.iping.ui.utils.exts.subscribeOnComputation
import com.wpapper.iping.ui.utils.exts.ui
import io.reactivex.Observable
import com.wpapper.iping.base.annotation.Backable
import com.wpapper.iping.ui.main.PieChartUtil
import com.wpapper.iping.ui.setting.SettingServerActivity
import com.wpapper.iping.ui.utils.hub.SimpleHUD
import kotlinx.android.synthetic.main.activity_main_item.*
import kotlinx.android.synthetic.main.content_detail.*


/**
 * Created by oldcwj@gmail.com on 2017/9/28.
 */
@Backable
class DetailActivity: BaseActivity() {

    companion object {
        const val KEY_HOST = "key_host"
        fun start(context: Context, host: String) {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(KEY_HOST, host)
            context.startActivity(intent)
        }
    }

    @JvmField
    var host: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        host = intent.getStringExtra(KEY_HOST)

        refreshLayout.setOnRefreshListener {
            loadData()
            refreshLayout.isRefreshing = false
        }

        loadData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == R.id.action_setting) {
            SettingServerActivity.start(this, host)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadData() {
        var sshInfo = DataSave(this@DetailActivity).getData(host)

        state.text = "loading"
        name.text = sshInfo!!.name

        PieChartUtil.getPieChart(chart_cpu, 0f, 0f, 0)
        PieChartUtil.getPieChart(chart_mem, 0f, 0f, 1)
        PieChartUtil.getPieChart(chart_disk, 0f, 0f, 2)

        SimpleHUD.show(this, "Loading", true)
        Observable.create<Info> {
            val result = SSHManager.newInstance().ssh(sshInfo)
            val info = CmdParser.newInstance().parse(result)

            //val result2 = SSHManager.newInstance().sshLs(sshInfo)

            it.onNext(info)
        }.subscribeOnComputation()
                .ui()
                .bindToLifecycle(this@DetailActivity)
                .subscribeNext {
                    SimpleHUD.dismiss()

                    PieChartUtil.getPieChart(chart_cpu, 100 - it.cpu.idle, it.cpu.idle, 0)
                    val free = it.mem.free + it.mem.cached
                    PieChartUtil.getPieChart(chart_mem, it.mem.used, free, 1)
                    PieChartUtil.getPieChart(chart_disk, it.disk.used, it.disk.free, 2)

                    state.text = "Running " + it.days.toString() + "days"

                    bar_text_cpu.text = "Cpu | sys ${it.cpu.sys}%    us ${it.cpu.us}%" +
                            "    other ${it.cpu.other}%    idle ${it.cpu.idle}%"
                    BarChartUtil.initBarChart(bar_cpu, floatArrayOf(it.cpu.sys, it.cpu.us,
                            it.cpu.other, it.cpu.idle))

                    bar_text_mem.text = "Mem | used ${Math.ceil((it.mem.used/1024).toDouble())}M" +
                            "    cached ${Math.ceil((it.mem.cached/1024).toDouble())}M" +
                            "    free ${Math.ceil((it.mem.free/1024).toDouble())}M"
                    BarChartUtil.initBarChart(bar_mem, floatArrayOf(it.mem.used,
                            it.mem.cached, it.mem.free))

                    bar_text_disk.text = "Disk | used ${Math.ceil((it.disk.used/1024).toDouble())}G" +
                            "    free ${Math.ceil((it.disk.free/1024).toDouble())}G"
                    BarChartUtil.initBarChart(bar_disk, floatArrayOf(it.disk.used, it.disk.free))

                    bar_text_net.text = "Net"
                    BarChartUtil.initBarChart(bar_net, floatArrayOf(332f, 223f))
                }

    }
}