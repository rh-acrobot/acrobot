package com.redhat.acrobot

import com.redhat.acrobot.entities.Explanation
import com.slack.api.bolt.App
import com.slack.api.bolt.context.builtin.EventContext
import com.slack.api.bolt.socket_mode.SocketModeApp
import com.slack.api.model.event.AppMentionEvent
import com.slack.api.model.event.MessageEvent
import org.hibernate.SessionFactory

private fun EventContext.formatSelfMention(): String {
    return "<@$botUserId>"
}

private fun String.stripSelfMentions(ctx: EventContext): String {
    return replace(ctx.formatSelfMention(), "")
}

private fun String.hasSelfMention(ctx: EventContext): Boolean {
    return contains(ctx.formatSelfMention())
}

private data class CommandContext(
    val slack: EventContext,
    val authorId: String,
)

private fun processCommand(ctx: CommandContext, sessionFactory: SessionFactory, text: String): String {
    val adjusted = text.stripSelfMentions(ctx.slack).trim()

    return sessionFactory.fromTransaction { session ->
        val acronym = findOrCreateAcronym(session, "TEST")
        val existing = findExplanation(session, acronym, adjusted)

        if (existing == null) {
            ctx.slack.logger.info("Adding new explanation to acronym TEST: {}", text)

            val newExplanation = Explanation(acronym, ctx.authorId, text)
            acronym.explanations.add(newExplanation)

            session.persist(newExplanation)
        }

        "Acronym now has ${acronym.explanations.count()} explanations"
    }
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
    val sessionFactory = createSessionFactory()

    app.command("/hello") { req, ctx ->
        ctx.ack(":wave: Hello!")
    }

    app.event(AppMentionEvent::class.java) { payload, ctx ->
        app.executorService().submit {
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
                message = processCommand(
                    ctx = CommandContext(
                        slack = ctx,
                        authorId = payload.event.user,
                    ),
                    sessionFactory = sessionFactory,
                    text = text,
                ),
            )
        }

        ctx.ack()
    }

    app.event(MessageEvent::class.java) { payload, ctx ->
        app.executorService().submit {
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
                    message = processCommand(
                        ctx = CommandContext(
                            slack = ctx,
                            authorId = payload.event.user,
                        ),
                        sessionFactory = sessionFactory,
                        text = text,
                    ),
                )
            }
        }

        ctx.ack()
    }

    SocketModeApp(app).start()
}