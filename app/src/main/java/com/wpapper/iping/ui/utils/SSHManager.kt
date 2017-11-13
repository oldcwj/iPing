package com.wpapper.iping.ui.utils

import android.text.TextUtils
import android.util.Log
import com.simplemobiletools.commons.extensions.formatSize
import com.simplemobiletools.commons.models.FileDirItem
import com.wpapper.iping.model.SshInfo
import net.schmizz.sshj.AndroidConfig
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.common.IOUtils
import net.schmizz.sshj.common.StreamCopier
import net.schmizz.sshj.transport.verification.PromiscuousVerifier
import java.util.concurrent.TimeUnit
import net.schmizz.sshj.xfer.FileSystemFile
import net.schmizz.sshj.xfer.TransferListener
import java.util.ArrayList
import java.util.concurrent.Callable

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
        var value = "AA"
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


    fun sshLs(sshInfo: SshInfo, path: String): ArrayList<FileDirItem> {
        val ssh = SSHClient(AndroidConfig())
        var value = "AA"

        val files = ArrayList<FileDirItem>()

        try {
            ssh.addHostKeyVerifier(PromiscuousVerifier());
            ssh.connect(sshInfo.host, sshInfo.port)
            ssh.authPassword(sshInfo.username, sshInfo.password)
            val session = ssh.startSession()
            try {
                val cmd = session.exec("ls -al " + path)
                value = IOUtils.readFully(cmd.inputStream).toString()
                val lines: List<String> = value.reader().readLines();
                lines.map {

                }
                for (v in lines.indices) {
                    Log.i("iping", lines[v])
                    if (v < 3) continue

                    val parts = lines[v].split("\\s+".toRegex())
                    if (parts.size >= 4) {
                        val permissions = parts[0].trim()
                        val isDirectory = permissions.startsWith("d")
                        val isFile = permissions.startsWith("-")
                        val size = if (isFile) parts[4].trim() else "0"
                        val childrenCnt = if (isFile) "0" else parts[1].trim()
                        val filename = TextUtils.join(" ", parts.subList(8, parts.size)).trimStart('/')

                        val fileSize = size.toLong()
                        val filePath = "${path.trimEnd('/')}/$filename"
                        val fileDirItem = FileDirItem(filePath, filename, isDirectory, childrenCnt.toInt(), fileSize)
                        files.add(fileDirItem)
                    }
                }

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

        return files
    }

    fun scpDownload(sshInfo: SshInfo, localPath: String, remotePath: String) {
        val ssh = SSHClient(AndroidConfig())
        try {
            ssh.addHostKeyVerifier(PromiscuousVerifier());
            ssh.connect(sshInfo.host, sshInfo.port)
            ssh.authPassword(sshInfo.username, sshInfo.password)

            ssh.newSCPFileTransfer().download(remotePath, FileSystemFile(localPath));
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            ssh.disconnect()
        }
    }

    fun scpDownload(sshInfo: SshInfo, localPath: String, remotePath: List<FileDirItem>,
                    callback: ((Int, String, Long, Long) -> Unit)) {
        val ssh = SSHClient(AndroidConfig())
        try {
            ssh.addHostKeyVerifier(PromiscuousVerifier());
            ssh.connect(sshInfo.host, sshInfo.port)
            ssh.authPassword(sshInfo.username, sshInfo.password)

            remotePath.forEachIndexed { index, fileDirItem ->
                Log.i("download", "it=" + fileDirItem.path + ":localPaht=" + localPath)
                val scpTransfer = ssh.newSCPFileTransfer()
                scpTransfer.transferListener = object : TransferListener {
                    override fun file(name: String?, size: Long): StreamCopier.Listener {
                        return StreamCopier.Listener {
                            Log.i("progress", "=" + it.formatSize())
                            callback(index, fileDirItem.name, fileDirItem.size, it)
                        }
                    }

                    override fun directory(name: String?): TransferListener {
                        return this
                    }

                }
                scpTransfer.download(fileDirItem.path, FileSystemFile(localPath));
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            ssh.disconnect()
        }
    }

    fun scpUpload(sshInfo: SshInfo, localPath: String, remotePath: String) {
        val ssh = SSHClient(AndroidConfig())
        try {
            ssh.addHostKeyVerifier(PromiscuousVerifier());
            ssh.connect(sshInfo.host, sshInfo.port)
            ssh.authPassword(sshInfo.username, sshInfo.password)

            // Present here to demo algorithm renegotiation - could have just put this before connect()
            // Make sure JZlib is in classpath for this to work
            ssh.useCompression();

            ssh.newSCPFileTransfer().upload(FileSystemFile(localPath), remotePath);
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            ssh.disconnect()
        }
    }

    fun scpUpload(sshInfo: SshInfo, localPath: List<String>, remotePath: String) {
        val ssh = SSHClient(AndroidConfig())
        try {
            ssh.addHostKeyVerifier(PromiscuousVerifier());
            ssh.connect(sshInfo.host, sshInfo.port)
            ssh.authPassword(sshInfo.username, sshInfo.password)

            // Present here to demo algorithm renegotiation - could have just put this before connect()
            // Make sure JZlib is in classpath for this to work
            ssh.useCompression();

            localPath.forEach {
                ssh.newSCPFileTransfer().upload(FileSystemFile(it), remotePath)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            ssh.disconnect()
        }
    }
}