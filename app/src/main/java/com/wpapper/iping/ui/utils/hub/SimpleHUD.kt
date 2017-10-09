package com.wpapper.iping.ui.utils.hub

import android.content.Context

object SimpleHUD {

    private var dialog: SimpleHUDDialog? = null

    fun show(context: Context, msgRes: Int, cancelable: Boolean) {
        show(context, context.getString(msgRes), cancelable)
    }

    fun show(context: Context, msg: String, cancelable: Boolean) {
        dismiss()
        setupDialog(context, msg, cancelable)
        try {
            dialog!!.show()
        } catch (ex: Exception) {

        }
    }

    private fun setupDialog(ctx: Context, msg: String, cancelable: Boolean) {
        dialog = SimpleHUDDialog.createDialog(ctx)
        dialog!!.setMessage(msg)
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.setCancelable(cancelable)
    }

    fun dismiss() {
        if (dialog?.isShowing ?: false)
            try {
                dialog!!.dismiss()
            } catch (ex: Exception) {

            }
        dialog = null
    }
}
