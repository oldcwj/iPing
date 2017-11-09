package com.wpapper.iping.ui.utils.exts

import android.content.Context
import com.simplemobiletools.commons.extensions.hasExternalSDCard
import com.wpapper.iping.ui.Config

val Context.config: Config get() = Config.newInstance(this)

fun Context.isPathOnRoot(path: String) = !(path.startsWith(config.internalStoragePath) || (hasExternalSDCard() && path.startsWith(config.sdCardPath)))
