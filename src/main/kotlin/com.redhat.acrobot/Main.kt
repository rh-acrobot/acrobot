package com.redhat.acrobot

import com.slack.api.bolt.App
import com.slack.api.bolt.context.builtin.EventContext
import com.slack.api.bolt.socket_mode.SocketModeApp
import com.slack.api.model.event.AppMentionEvent
import com.slack.api.model.event.MessageEvent

private fun EventContext.formatSelfMention(): String {
    return "<@$botUserId>"
}

private fun String.stripSelfMentions(ctx: EventContext): String {
    return replace(ctx.formatSelfMention(), "")
}

private fun String.hasSelfMention(ctx: EventContext): Boolean {
    return contains(ctx.formatSelfMention())
}

private fun processCommand(ctx: EventContext, text: String): String {
    val adjusted = text.stripSelfMentions(ctx).trim()

    return "Adjusted text: $adjusted"
}

private fun trySendMessage(ctx: EventContext, channel: String, threadTs: String?, message: String) {
    val response = ctx.client().chatPostMessage {
        it.channel(channel)
        it.threadTs(threadTs)
        it.text(message)
    }

    if (!response.isOk) {
        ctx.logger.error("Failed to respond: {}", response.error)
    }
}

fun main() {
    val app = App()

    app.command("/hello") { req, ctx ->
        ctx.ack(":wave: Hello!")
    }

    app.event(AppMentionEvent::class.java) { payload, ctx ->
        val text = payload.event.text

        ctx.logger.info(
            "Processing mention: channel {}; content: {}",
            payload.event.channel,
            text,
        )

        trySendMessage(
            ctx = ctx,
            channel = payload.event.channel,
            threadTs = payload.event.threadTs,
            message = processCommand(ctx, text),
        )

        ctx.ack()
    }

    app.event(MessageEvent::class.java) { payload, ctx ->
        val channelType = payload.event.channelType
        val text = payload.event.text

        if (channelType == "im" || text.hasSelfMention(ctx)) {
            ctx.logger.info(
                "Processing message: channel {} ({}); content: {}",
                payload.event.channel,
                channelType,
                text,
            )

            trySendMessage(
                ctx = ctx,
                channel = payload.event.channel,
                threadTs = payload.event.threadTs,
                message = processCommand(ctx, text),
            )
        }

        ctx.ack()
    }

    SocketModeApp(app).start()
}