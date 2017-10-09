package com.wpapper.iping.ui.main

import android.graphics.Color
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.MPPointF

/**
 * Created by oldcwj@gmail.com on 2017/9/26.
 */
object PieChartUtil {

    fun getPieChart(pieChart: PieChart, used: Float, free: Float, type: Int): PieChart {

        pieChart.setUsePercentValues(true);
        pieChart.description.isEnabled = false
        pieChart.setExtraOffsets(5f, 1f, 5f, 1f);
        pieChart.dragDecelerationFrictionCoef = 0.95f
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.WHITE);

        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);

        pieChart.holeRadius = 78f
        pieChart.transparentCircleRadius = 79f

        pieChart.setDrawCenterText(true);
        pieChart.centerText = when(type) {
            0 -> Math.ceil(used.toDouble()).toString() + "%"
            1 -> Math.ceil((used / 1024).toDouble()).toString() + "M"
            2 -> Math.ceil((used / (1024)).toDouble()).toString() + "G"
            3 -> Math.ceil((used / (1024*1024)).toDouble()).toString() + "G"
            else -> ""
        }

        pieChart.rotationAngle = 0f
        pieChart.isRotationEnabled = true
        pieChart.isHighlightPerTapEnabled = true

        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(used))
        entries.add(PieEntry(free))

        val label = when(type) {
            0 -> "cpu"
            1 -> "mem"
            2 -> "disk"
            3 -> "net"
            else -> ""
        }
        val dataSet = PieDataSet(entries, label)
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 1f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 3f


        // add a lot of colors
        val colors = ArrayList<Int>()
        colors.add(Color.parseColor("#00BF9A"))
        colors.add(Color.GRAY)
        dataSet.colors = colors
        dataSet.setDrawValues(false)

        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.legend.isEnabled = false

        pieChart.highlightValues(null)
        pieChart.invalidate()

        return pieChart
    }
}