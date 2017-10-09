package com.wpapper.iping.ui.detail

import android.graphics.Color
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

/**
 * Created by oldcwj@gmail.com on 2017/9/29.
 */
object BarChartUtil {

    fun initBarChart(barChart: HorizontalBarChart, valueArray:FloatArray) {
        barChart.setDrawGridBackground(false)
        barChart.description.isEnabled = false

        // scaling can now only be done on x- and y-axis separately
        barChart.setPinchZoom(false)

        barChart.setDrawBarShadow(false)
        barChart.setDrawValueAboveBar(false)
        barChart.isHighlightFullBarEnabled = false

        barChart.setNoDataText("No data")
        barChart.setNoDataTextColor(Color.parseColor("#aaaaaa"))

        barChart.axisLeft.isEnabled = false
        barChart.axisRight.setDrawGridLines(false)
        barChart.axisRight.setDrawZeroLine(false)
        barChart.axisRight.setLabelCount(7, false)

        barChart.axisRight.textSize = 9f
        barChart.axisRight.isEnabled = false

        val xAxis = barChart.xAxis
        xAxis.isEnabled = false
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.textSize = 9f
        xAxis.setCenterAxisLabels(false)
        xAxis.labelCount = 12
        xAxis.granularity = 10f

        val l = barChart.legend
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(false)
        l.formSize = 8f
        l.formToTextSpace = 4f
        l.xEntrySpace = 6f
        l.isEnabled = false

        val yValues = ArrayList<BarEntry>()
        yValues.add(BarEntry(15f, valueArray))

        val set = BarDataSet(yValues, "")
        set.setDrawIcons(false)
        //set.valueFormatter = CustomFormatter()
        set.valueTextSize = 7f
        set.axisDependency = YAxis.AxisDependency.RIGHT
        val colorArray = intArrayOf(Color.parseColor("#00BF9A"),
                Color.parseColor("#80CBC4"),
                Color.parseColor("#B2DFDB"),
                Color.parseColor("#ffc0c0c0"))

        when(valueArray.size) {
            2-> colorArray.set(1, Color.parseColor("#ffc0c0c0"))
            3-> colorArray.set(2, Color.parseColor("#ffc0c0c0"))
        }
        set.setColors(*colorArray)

        val data = BarData(set)
        data.barWidth = 8.5f
        barChart.data = data
        barChart.invalidate()
    }
}