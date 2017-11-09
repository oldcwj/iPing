package com.wpapper.iping.ui.dialogs

import android.support.v7.app.AlertDialog
import android.view.View
import android.view.WindowManager
import com.simplemobiletools.commons.dialogs.FilePickerDialog
import com.simplemobiletools.commons.extensions.*
import com.wpapper.iping.R
import com.wpapper.iping.ui.folder.SimpleActivity
import com.wpapper.iping.ui.utils.exts.config
import kotlinx.android.synthetic.main.dialog_compress_as.view.*

import java.io.File

class CompressAsDialog(val activity: SimpleActivity, val path: String, val callback: (destination: String) -> Unit) {
    private val view = activity.layoutInflater.inflate(R.layout.dialog_compress_as, null)

    init {
        val file = File(path)
        val filename = path.getFilenameFromPath()
        val indexOfDot = if (filename.contains('.') && file.isFile) filename.lastIndexOf(".") else filename.length
        val baseFilename = filename.substring(0, indexOfDot)
        var realPath = file.parent.trimEnd('/')

        view.apply {
            file_name.setText(baseFilename)

            file_path.text = activity.humanizePath(realPath)
            file_path.setOnClickListener {
                FilePickerDialog(activity, realPath, false, activity.config.shouldShowHidden, true) {
                    file_path.text = activity.humanizePath(it)
                    realPath = it
                }
            }
        }

        AlertDialog.Builder(activity)
                .setPositiveButton(R.string.ok, null)
                .setNegativeButton(R.string.cancel, null)
                .create().apply {
                        activity.setupDialogStuff(view, this, R.string.compress_as)
                        window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
                        getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(View.OnClickListener {
                            val name = view.file_name.value
                            when {
                                name.isEmpty() -> activity.toast(R.string.empty_name)
                                name.isAValidFilename() -> {
                                    val newFile = File(realPath, "$name.zip")
                                    if (newFile.exists()) {
                                        activity.toast(R.string.name_taken)
                                        return@OnClickListener
                                    }

                                    dismiss()
                                    callback(newFile.absolutePath)
                                }
                                else -> activity.toast(R.string.invalid_name)
                            }
                        })
        }
    }
}
