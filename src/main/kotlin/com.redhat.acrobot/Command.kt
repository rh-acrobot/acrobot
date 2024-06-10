package com.redhat.acrobot

import com.redhat.acrobot.CommandFormat.ACRONYM_SEPARATOR
import com.redhat.acrobot.CommandFormat.CHANGE_PREFIX
import com.redhat.acrobot.CommandFormat.UPDATE_EXPLANATION_SEPARATOR
import com.redhat.acrobot.entities.Acronym
import com.redhat.acrobot.entities.Explanation
import org.hibernate.Session

private inline fun validateExplanationOrElse(
    explanationText: String,
    onFailure: (String) -> Nothing
) {
    if (explanationText.length > Explanation.MAX_EXPLANATION_LENGTH)
        onFailure(Messages.EXPLANATION_TOO_LONG)
}

private fun processReplaceExplanation(
    userId: String,
    session: Session,
    acronym: Acronym?,
    oldExplanationText: String,
    newExplanationText: String,
): String {
    acronym ?: return Messages.ACRONYM_NOT_FOUND

    validateExplanationOrElse(newExplanationText) { msg -> return msg }

    val existingExplanation = findExplanation(session, acronym, oldExplanationText)
        ?: return Messages.EXPLANATION_NOT_FOUND

    if (existingExplanation.authorId != userId) {
        return Messages.INSUFFICIENT_PRIVILEGES
    }

    val replacement = acronym.createExplanation(userId, newExplanationText)
    session.persist(replacement)

    deleteExplanation(session, existingExplanation)

    return Messages.EXPLANATION_UPDATED
}

private fun processRemoveExplanation(
    userId: String,
    session: Session,
    acronym: Acronym?,
    explanationText: String,
): String {
    acronym ?: return Messages.ACRONYM_NOT_FOUND

    val target = findExplanation(session, acronym, explanationText) ?: return Messages.EXPLANATION_NOT_FOUND

    if (target.authorId != userId) {
        return Messages.INSUFFICIENT_PRIVILEGES
    }

    deleteExplanation(session, target)

    return Messages.EXPLANATION_REMOVED
}

private fun processNewExplanation(
    userId: String,
    session: Session,
    acronym: Acronym,
    newExplanationText: String,
): String {
    validateExplanationOrElse(newExplanationText) { msg -> return msg }

    val existingExplanation = findExplanation(session, acronym, newExplanationText)

    if (existingExplanation != null) {
        return "That explanation already exists for the given acronym. If you created it, you can update it with ${CHANGE_PREFIX}change ${acronym.acronym} $ACRONYM_SEPARATOR $newExplanationText $UPDATE_EXPLANATION_SEPARATOR [new version]"
    }

    session.persist(acronym.createExplanation(userId, newExplanationText))

    return Messages.EXPLANATION_SAVED
}

private val ACRONYM_SEPARATOR_PATTERN = Regex(
    "(?!${Regex.escape(UPDATE_EXPLANATION_SEPARATOR)})${Regex.escape(ACRONYM_SEPARATOR)}",
)

private fun processChange(userId: String, session: Session, command: String): String {
    val parts = command.split(ACRONYM_SEPARATOR_PATTERN, limit = 2).map { it.trim() }

    if (parts.size < 2) {
        return Messages.INCORRECT_FORMAT_FOR_SAVING_ACRONYM
    }

    val acronymText = parts[0]
    val changeInstruction = parts[1]

    if (changeInstruction.contains(UPDATE_EXPLANATION_SEPARATOR)) {
        val acronym = findAcronym(session, acronymText)

        val changeParts = changeInstruction.split(UPDATE_EXPLANATION_SEPARATOR)

        if (changeParts.size > 2) {
            return Messages.MULTIPLE_UPDATE_SEPARATORS
        }

        check(changeParts.size == 2)

        val oldExplanationText = changeParts[0].trim()
        val newExplanationText = changeParts[1].trim()

        return if (newExplanationText.isNotEmpty()) {
            processReplaceExplanation(
                userId = userId,
                session = session,
                acronym = acronym,
                oldExplanationText = oldExplanationText,
                newExplanationText = newExplanationText,
            )
        } else {
            processRemoveExplanation(
                userId = userId,
                session = session,
                acronym = acronym,
                explanationText = oldExplanationText,
            )
        }
    } else {
        val acronym = findOrCreateAcronym(session, acronymText)

        return processNewExplanation(
            userId = userId,
            session = session,
            acronym = acronym,
            newExplanationText = changeInstruction,
        )
    }
}

private fun processLookup(session: Session, command: String): String {
    val acronym = findAcronym(session, command.trim())

    return if (acronym == null || acronym.explanations.isEmpty()) {
        Messages.ACRONYM_NOT_FOUND
    } else {
        acronym
            .explanations
            .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.explanation })
            .joinToString("\n") { it.explanation }
    }
}

private val HELP_PATTERN = Regex("^\\s*(${Regex.escape(CHANGE_PREFIX)}\\s*)?help\\s*$", RegexOption.IGNORE_CASE)

fun processCommand(userId: String, session: Session, command: String): String {
    val adjusted = command.trim()

    return if (adjusted.matches(HELP_PATTERN)) {
        return Messages.HELP_TEXT
    } else if (adjusted.startsWith(CHANGE_PREFIX)) {
        processChange(
            userId = userId,
            session = session,
            command = adjusted.removePrefix("!"),
        )
    } else {
        processLookup(session, adjusted)
    }
}
