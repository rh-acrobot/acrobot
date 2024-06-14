package com.redhat.acrobot

import com.slack.api.bolt.App
import com.slack.api.bolt.context.builtin.EventContext
import com.slack.api.bolt.socket_mode.SocketModeApp
import com.slack.api.model.event.AppMentionEvent
import com.slack.api.model.event.MessageEvent

private fun String.cleanSlackMessage(ctx: EventContext): String {
    return stripSelfMentions(ctx).decodeSlackEscapes()
}

private fun MessageSource.trySendMessage(message: String) {
    val response = ctx.client().chatPostMessage {
        it.channel(channel)
        it.threadTs(threadTs)
        it.text(message.encodeSlackEscapes())
        it.mrkdwn(false)
    }

    if (!response.isOk) {
        ctx.logger.error("Failed to respond: {}", response.error)
    }
}

private data class MessageSource(
    val ctx: EventContext,
    val channel: String,
    val threadTs: String?,
)

private fun handleErrors(source: MessageSource, block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        source.ctx.logger.error("Exception while processing message", e)

        try {
            source.trySendMessage("I'm sorry, I couldn't process your message.")
        } catch (e2: Exception) {
            source.ctx.logger.error("Exception while attempting to send error response", e2)
        }
    }
}

fun main() {
    val app = App()
    val sessionFactory = createSessionFactory()

    app.command("/hello") { req, ctx ->
        ctx.ack(":wave: Hello!")
    }

    app.event(AppMentionEvent::class.java) { payload, ctx ->
        app.executorService().submit {
            val source = MessageSource(
                ctx = ctx,
                channel = payload.event.channel,
                threadTs = payload.event.threadTs,
            )

            val text = payload.event.text

            ctx.logger.info(
                "Processing mention: channel {}; content: {}",
                payload.event.channel,
                text,
            )

            handleErrors(source) {
                source.trySendMessage(sessionFactory.fromTransaction { session ->
                    processMessage(
                        userId = payload.event.user,
                        session = session,
                        command = text.cleanSlackMessage(ctx),
                    )
                })
            }
        }

        ctx.ack()
    }

    app.event(MessageEvent::class.java) { payload, ctx ->
        app.executorService().submit {
            val source = MessageSource(
                ctx = ctx,
                channel = payload.event.channel,
                threadTs = payload.event.threadTs,
            )

            val channelType = payload.event.channelType
            val text = payload.event.text

            if (channelType == "im" || text.hasSelfMention(ctx)) {
                ctx.logger.info(
                    "Processing message: channel {} ({}); content: {}",
                    payload.event.channel,
                    channelType,
                    text,
                )

                handleErrors(source) {
                    source.trySendMessage(sessionFactory.fromTransaction { session ->
                        processMessage(
                            userId = payload.event.user,
                            session = session,
                            command = text.cleanSlackMessage(ctx),
                        )
                    })
                }
            }
        }

        ctx.ack()
    }

    SocketModeApp(app).start()
}
