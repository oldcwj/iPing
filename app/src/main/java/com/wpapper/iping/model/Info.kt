package com.wpapper.iping.model

import android.text.TextUtils
import android.util.Log

/**
 * Created by oldcwj@gmail.com on 2017/9/26.
 */
open class Info(var days: Int, var cpu: Cpu, var mem: Mem, var disk: Disk, var net: Net, var runningDay: Int)

open class Cpu(var sys: Float = 0f, var us: Float = 0f, var idle: Float = 0f, var other: Float = 0f)

open class Mem(var used: Float = 0f, var cached: Float = 0f, var free: Float = 0f)

open class Disk(var used: Float = 0f, var free: Float = 0f, var read: Float = 0f, var send: Float = 0f)

open class Net(var recv: Float = 0f, var sent: Float = 0f)

class CmdParser {
    companion object {
        fun newInstance(): CmdParser = CmdParser()
    }

    fun parse(result: String): Info {
        var cpu = Cpu()
        val mem = Mem()
        val disk = Disk()
        val net = Net()
        val info = Info(0, cpu, mem, disk, net, 0)
        if (!TextUtils.isEmpty(result) && result.contains("Cpu")) {
            val cpuString = result.substring(result.indexOf("Cpu(s):"), result.indexOf("Mem"))
            val us = cpuString.substring(cpuString.indexOf("%us") - 4, cpuString.indexOf("%us")).toFloat()
            val sy = cpuString.substring(cpuString.indexOf("%sy") - 4, cpuString.indexOf("%sy")).toFloat()
            val id = cpuString.substring(cpuString.indexOf("%id") - 4, cpuString.indexOf("%id")).toFloat()
            val other = 100 - us - sy - id
            cpu.us = us; cpu.sys = sy; cpu.idle = id; cpu.other = other

            val memString = result.substring(result.indexOf("Mem"), result.indexOf("Swap"))
            Log.i("memString", memString)
            val total = memString.substring(memString.indexOf("Mem:") + 4, memString.indexOf("k total")).toFloat()
            val used = memString.substring(memString.indexOf("total") + 6, memString.indexOf("k used")).toFloat()
            val free = memString.substring(memString.indexOf("used") + 5, memString.indexOf("k free")).toFloat()
            val buffers = memString.substring(memString.indexOf("free") + 5, memString.indexOf("k buffers")).toFloat()
            mem.used = used; mem.free = free; mem.cached = buffers

            val days = result.substring(result.indexOf("up") + 2, result.indexOf("day")).trim().toInt()
            info.days = days

            val diskString = result.substring(result.indexOf("Mounted on"), result.length)
            Log.i("disk", diskString)
            val diskValue = diskString.split("\\s+".toRegex())
            disk.used = diskValue[4].trim().toFloat()
            disk.free = diskValue[5].trim().toFloat()
        }

        return info
    }
}