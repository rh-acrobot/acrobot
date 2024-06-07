package com.redhat.acrobot

import com.slack.api.bolt.context.builtin.EventContext

fun EventContext.formatSelfMention(): String {
    return "<@$botUserId>"
}

fun String.stripSelfMentions(ctx: EventContext): String {
    return replace(ctx.formatSelfMention(), "")
}

fun String.hasSelfMention(ctx: EventContext): Boolean {
    return contains(ctx.formatSelfMention())
}

fun String.decodeSlackEscapes(): String {
    return replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&")
}

fun String.encodeSlackEscapes(): String {
    return replace("&amp;", "&").replace("<", "&lt;").replace(">", "&gt;")
}
