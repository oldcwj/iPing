package com.wpapper.iping.ui.utils

import android.util.Log
import com.wpapper.iping.model.SshInfo
import net.schmizz.sshj.AndroidConfig
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.common.IOUtils
import net.schmizz.sshj.transport.verification.PromiscuousVerifier
import java.util.concurrent.TimeUnit

/**
 * Created by oldcwj@gmail.com on 2017/9/26.
 */

class SSHManager {
    companion object {
        fun newInstance(): SSHManager {
            return SSHManager()
        }
    }

    fun ssh(sshInfo: SshInfo): String {
        val ssh = SSHClient(AndroidConfig())
        var value: String = "AA"
        try {
            ssh.addHostKeyVerifier(PromiscuousVerifier());
            ssh.connect(sshInfo.host, sshInfo.port)
            ssh.authPassword(sshInfo.username, sshInfo.password)
            val session = ssh.startSession()
            try {
                val cmd = session.exec("top -bn 1 -i -c \n df -m")
                value = IOUtils.readFully(cmd.inputStream).toString()
                Log.i("iping", value)
                cmd.join(5, TimeUnit.SECONDS)
                println("\n** exit status: " + cmd.exitStatus!!)
            } finally {
                session.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            ssh.disconnect()
        }

        return value
    }
}