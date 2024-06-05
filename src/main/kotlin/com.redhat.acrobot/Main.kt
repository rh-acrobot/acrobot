package com.redhat.acrobot

import com.slack.api.bolt.App
import com.slack.api.bolt.socket_mode.SocketModeApp
import com.slack.api.model.event.AppMentionEvent

fun main() {
    val app = App()

    app.command("/hello") { req, ctx ->
        ctx.ack(":wave: Hello!")
    }

    app.event(AppMentionEvent::class.java) { payload, ctx ->
        ctx.logger.info("Got mention event")

        val response = ctx.client().chatPostMessage {
            it.channel(payload.event.channel)
            it.threadTs(payload.event.threadTs)
            it.text("I have been summoned!")
        }

        if (!response.isOk) {
            ctx.logger.error("Failed to respond: ${response.error}")
        }

        ctx.ack()
    }

    SocketModeApp(app).start()
}