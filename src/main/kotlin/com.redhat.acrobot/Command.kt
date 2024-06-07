package com.redhat.acrobot

import com.redhat.acrobot.CommandFormat.ACRONYM_SEPARATOR
import com.redhat.acrobot.CommandFormat.CHANGE_PREFIX
import com.redhat.acrobot.CommandFormat.UPDATE_EXPLANATION_SEPARATOR
import com.redhat.acrobot.entities.Acronym
import com.redhat.acrobot.entities.Explanation
import com.slack.api.bolt.context.builtin.EventContext
import org.hibernate.Session
import org.hibernate.SessionFactory

data class CommandContext(
    val slack: EventContext,
    val authorId: String,
)

private fun processReplaceExplanation(
    userId: String,
    session: Session,
    acronym: Acronym?,
    oldExplanationText: String,
    newExplanationText: String,
): String {
    acronym ?: return Messages.ACRONYM_NOT_FOUND

    val existingExplanation = findExplanation(session, acronym, oldExplanationText)
        ?: return Messages.EXPLANATION_NOT_FOUND

    if (existingExplanation.authorId != userId) {
        return Messages.INSUFFICIENT_PRIVILEGES
    }

    val replacement = Explanation(acronym, userId, newExplanationText)

    session.persist(replacement)
    session.remove(existingExplanation)

    return Messages.ACRONYM_UPDATED
}

fun processRemoveExplanation(userId: String, session: Session, acronym: Acronym?, explanationText: String): String {
    acronym ?: return Messages.ACRONYM_NOT_FOUND

    val target = findExplanation(session, acronym, explanationText) ?: return Messages.EXPLANATION_NOT_FOUND

    if (target.authorId != userId) {
        return Messages.INSUFFICIENT_PRIVILEGES
    }

    session.remove(target)

    return Messages.EXPLANATION_REMOVED
}

private fun processNewExplanation(
    userId: String,
    session: Session,
    acronym: Acronym,
    newExplanationText: String,
): String {
    val existingExplanation = findExplanation(session, acronym, newExplanationText)

    if (existingExplanation != null) {
        return "That explanation already exists for the given acronym. If you created it, you can update it with ${CHANGE_PREFIX}change ${acronym.acronym} $ACRONYM_SEPARATOR $newExplanationText $UPDATE_EXPLANATION_SEPARATOR [new version]"
    }

    session.persist(Explanation(acronym, userId, newExplanationText))

    return Messages.EXPLANATION_SAVED
}

private fun processChange(ctx: CommandContext, sessionFactory: SessionFactory, command: String): String {
    val parts = command.split(ACRONYM_SEPARATOR, limit = 2).map { it.trim() }

    if (parts.size < 2) {
        return Messages.INCORRECT_FORMAT_FOR_SAVING_ACRONYM
    }

    val acronymText = parts[0]
    val changeInstruction = parts[1]

    return sessionFactory.fromTransaction cmd@{ session ->
        if (changeInstruction.contains(UPDATE_EXPLANATION_SEPARATOR)) {
            val acronym = findAcronym(session, acronymText)

            val changeParts = changeInstruction.split(UPDATE_EXPLANATION_SEPARATOR)

            if (changeParts.size > 2) {
                return@cmd Messages.MULTIPLE_UPDATE_SEPARATORS
            }

            check(changeParts.size == 2)

            val oldExplanationText = changeParts[0].trim()
            val newExplanationText = changeParts[1].trim()

            if (newExplanationText.isNotEmpty()) {
                processReplaceExplanation(
                    userId = ctx.authorId,
                    session = session,
                    acronym = acronym,
                    oldExplanationText = oldExplanationText,
                    newExplanationText = newExplanationText,
                )
            } else {
                processRemoveExplanation(
                    userId = ctx.authorId,
                    session = session,
                    acronym = acronym,
                    explanationText = oldExplanationText,
                )
            }
        } else {
            val acronym = findOrCreateAcronym(session, acronymText)

            processNewExplanation(
                userId = ctx.authorId,
                session = session,
                acronym = acronym,
                newExplanationText = changeInstruction,
            )
        }
    }
}

private fun processLookup(sessionFactory: SessionFactory, command: String): String {
    return sessionFactory.fromTransaction { session ->
        val acronym = findAcronym(session, command.trim())

        if (acronym == null || acronym.explanations.isEmpty()) {
            Messages.ACRONYM_NOT_FOUND
        } else {
            acronym.explanations.sortedBy { it.explanation }.joinToString("\n") { it.explanation }
        }
    }
}

fun processCommand(ctx: CommandContext, sessionFactory: SessionFactory, command: String): String {
    val adjusted = command.trim()

    return if (adjusted.startsWith(CHANGE_PREFIX)) {
        processChange(ctx, sessionFactory, adjusted.removePrefix("!"))
    } else {
        processLookup(sessionFactory, adjusted)
    }
}
