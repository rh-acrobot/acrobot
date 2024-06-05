package com.redhat.acrobot

import com.slack.api.bolt.App
import com.slack.api.bolt.socket_mode.SocketModeApp

fun main() {
    val app = App()

    app.command("/hello") { req, ctx ->
        ctx.ack(":wave: Hello!")
    }

    SocketModeApp(app).start()
}