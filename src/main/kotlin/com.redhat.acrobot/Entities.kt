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

fun findExplanationsByAuthor(session: Session, authorId: String): List<Explanation> {
    val builder = session.criteriaBuilder

    val query = builder.createQuery(Explanation::class.java)
    val root = query.from(Explanation::class.java)
    query.where(builder.equal(root.get(Explanation_.authorId), authorId))

    val graph = session.createEntityGraph(Explanation::class.java)
    graph.addSubgraph(Explanation_.acronym)

    return session.createQuery(query).applyLoadGraph(graph).list()
}

fun deleteExplanation(session: Session, explanation: Explanation) {
    session.remove(explanation)
    explanation.acronym.explanations.remove(explanation)
}
