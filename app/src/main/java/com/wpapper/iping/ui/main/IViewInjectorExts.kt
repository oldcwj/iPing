
import android.widget.LinearLayout
import android.widget.TextView
import com.github.mikephil.charting.charts.PieChart
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import com.wpapper.iping.R
import com.wpapper.iping.base.rx.RxActivity
import com.wpapper.iping.ui.main.PieChartUtil
import com.wpapper.iping.model.CmdParser
import com.wpapper.iping.model.Info
import com.wpapper.iping.model.SshInfo
import com.wpapper.iping.ui.utils.SSHManager
import com.wpapper.iping.ui.utils.exts.subscribeNext
import com.wpapper.iping.ui.utils.exts.subscribeOnComputation
import com.wpapper.iping.ui.utils.exts.ui
import io.reactivex.Observable
import net.idik.lib.slimadapter.viewinjector.IViewInjector

fun IViewInjector<*>.pieChart(id: Int, sshInfo: SshInfo, context: RxActivity): IViewInjector<*> {
    val layout: LinearLayout = findViewById<LinearLayout>(id)
    val chartCpu: PieChart = layout.findViewById<PieChart>(R.id.chart_cpu)
    val chartMem: PieChart = layout.findViewById(R.id.chart_mem)
    val chartDisk: PieChart = layout.findViewById(R.id.chart_disk)
    val daysView = layout.findViewById<TextView>(R.id.state)
    val nameView = layout.findViewById<TextView>(R.id.name)
    daysView.text = "loading"
    nameView.text = sshInfo.name

    PieChartUtil.getPieChart(chartCpu, 0f, 0f, 0)
    PieChartUtil.getPieChart(chartMem, 0f, 0f, 1)
    PieChartUtil.getPieChart(chartDisk, 0f, 0f, 2)

    Observable.create<Info> {
        val result = SSHManager.newInstance().ssh(sshInfo)
        val info = CmdParser.newInstance().parse(result)
        it.onNext(info)
    }.subscribeOnComputation()
            .ui()
            .bindToLifecycle(context)
            .subscribeNext {
                PieChartUtil.getPieChart(chartCpu, 100 - it.cpu.idle, it.cpu.idle, 0)
                PieChartUtil.getPieChart(chartMem, it.mem.used, it.mem.free + it.mem.cached, 1)
                PieChartUtil.getPieChart(chartDisk, it.disk.used, it.disk.free, 2)

                daysView.text = "Running " + it.days.toString() + "days"
            }

    return this
}
