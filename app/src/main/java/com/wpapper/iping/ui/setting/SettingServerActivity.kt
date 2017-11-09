package com.wpapper.iping.ui.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import com.wpapper.iping.R
import com.wpapper.iping.base.annotation.AutoHideKeyboard
import com.wpapper.iping.base.annotation.Backable
import com.wpapper.iping.base.ui.BaseActivity
import com.wpapper.iping.model.DataSave
import com.wpapper.iping.model.SshInfo
import kotlinx.android.synthetic.main.content_edit_server.*

/**
 * Created by oldcwj@gmail.com on 2017/9/27.
 */
@AutoHideKeyboard
@Backable
class SettingServerActivity : BaseActivity() {

    companion object {
        const val KEY_HOST = "key_host"
        fun start(context: Context, host: String) {
            val intent = Intent(context, SettingServerActivity::class.java)
            intent.putExtra(KEY_HOST, host)
            context.startActivity(intent)
        }
    }

    @JvmField
    var host: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_server)

        host = intent.getStringExtra(KEY_HOST)

        if (!host.isEmpty()) {
            var sshInfo = DataSave(this@SettingServerActivity).getData(host)
            if (sshInfo != null) {
                nameTextView.setText(sshInfo.name)
                hostTextView.setText(sshInfo.host)
                portTextView.setText(sshInfo.port.toString())
                userNameTextView.setText(sshInfo.username)
                passwordTextView.setText(sshInfo.password)
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit_server, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == R.id.action_save) {
            if (submit()) finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun submit(): Boolean {
        val name = nameTextView.text.toString()
        val host = hostTextView.text.toString()
        val port = portTextView.text.toString()
        val username = userNameTextView.text.toString()
        val password = passwordTextView.text.toString()
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(host)
                && !TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            val sshInfo = SshInfo(name, host, port?.toInt()?:22, username, password)
            DataSave(this@SettingServerActivity).addDataToList(sshInfo)
            return true
        } else {
            return false
        }
    }
}