package com.wpapper.iping.model

import com.google.gson.reflect.TypeToken
import com.google.gson.Gson
import android.content.Context
import android.content.SharedPreferences


/**
 * Created by oldcwj@gmail.com on 2017/9/27.
 */
class DataSave(mContext: Context) {
    private val preferences: SharedPreferences
    private val editor: SharedPreferences.Editor

    private val tag = "ssh" //xPing

    init {
        preferences = mContext.getSharedPreferences(tag, Context.MODE_PRIVATE)
        editor = preferences.edit()
    }

    private fun setDataList(datalist: MutableList<SshInfo>?) {
        if (null == datalist || datalist.size <= 0)
            return

        val gson = Gson()
        val strJson = gson.toJson(datalist)
        editor.clear()
        editor.putString(tag, strJson)
        editor.commit()
    }

    fun addDataToList(data: SshInfo) {
        val gson = Gson()
        var datalist: MutableList<SshInfo> = ArrayList()

        val strJson = preferences.getString(tag, null)
        if (strJson != null) {
            datalist = gson.fromJson(strJson, object : TypeToken<ArrayList<SshInfo>>() {
            }.type)
        }
        var list = datalist.filter { it.host == (data.host) }
        if (!list.isEmpty()) {
            datalist.removeAll(list)
        }
        datalist.add(data)
        setDataList(datalist)
    }

    fun getDataList(): ArrayList<SshInfo> {
        var datalist: ArrayList<SshInfo> = ArrayList()
        val strJson = preferences.getString(tag, null) ?: return datalist
        val gson = Gson()
        datalist = gson.fromJson(strJson, object : TypeToken<ArrayList<SshInfo>>() {

        }.type)
        return datalist
    }

    fun getData(host: String): SshInfo? {
        var sshInfo: SshInfo? = null;
        var datalist: ArrayList<SshInfo>
        val strJson = preferences.getString(tag, null) ?: return sshInfo
        datalist = Gson().fromJson(strJson, object : TypeToken<ArrayList<SshInfo>>() {
        }.type)
        val result = datalist.filter { it.host == host }
        if (!result.isEmpty()) {
            sshInfo = result[0]
        }

        return sshInfo
    }
}