package com.redhat.acrobot

import com.redhat.acrobot.entities.Acronym
import com.redhat.acrobot.entities.Acronym_
import com.redhat.acrobot.entities.Explanation
import com.redhat.acrobot.entities.Explanation_
import org.hibernate.Session

private fun findAcronymNormalized(session: Session, normalizedText: String): Acronym? {
    return session.byNaturalId(Acronym::class.java).using(Acronym_.acronym, normalizedText).load()
}

fun findAcronym(session: Session, text: String): Acronym? {
    return findAcronymNormalized(session, Acronym.normalizeText(text))
}

fun findOrCreateAcronym(session: Session, text: String): Acronym {
    val normalizedText = Acronym.normalizeText(text)

    return findAcronymNormalized(session, normalizedText)
        ?: Acronym(normalizedText).also { session.persist(it) }
}

fun findExplanation(session: Session, acronym: Acronym, text: String): Explanation? {
    return session
        .byNaturalId(Explanation::class.java)
        .using(Explanation_.acronym, acronym)
        .using(Explanation_.explanation, text)
        .load()
}