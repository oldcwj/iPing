package com.wpapper.iping.model

/**
 * Created by oldcwj@gmail.com on 2017/9/26.
 */
open class SshInfo(val name: String, val host: String, val port: Int = 22,
                   val username: String, val password: String)