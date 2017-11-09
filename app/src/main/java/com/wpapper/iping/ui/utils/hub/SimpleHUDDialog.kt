package com.wpapper.iping.ui.utils.hub

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.TextView
import com.wpapper.iping.R


internal class SimpleHUDDialog(context: Context, theme: Int) : Dialog(context, theme) {

    fun setMessage(message: String) {
        val msgView = findViewById<TextView>(R.id.simplehud_message)
        msgView.text = message
    }

    companion object {

        fun createDialog(context: Context): SimpleHUDDialog {
            val dialog = SimpleHUDDialog(context, R.style.SimpleHUD)
            dialog.setContentView(R.layout.simplehud)

            val params = dialog.window!!.attributes
            params.gravity = Gravity.TOP
            params.verticalMargin = 0.2f
            return dialog
        }
    }


}
