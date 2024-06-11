package com.redhat.acrobot.entities;

import jakarta.persistence.*;
import org.hibernate.Length;
import org.hibernate.annotations.NaturalId;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Objects;

@Entity
public class Explanation implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    public static final int MAX_EXPLANATION_LENGTH = Length.LONG;

    @NaturalId
    @Column(length = MAX_EXPLANATION_LENGTH)
    private String explanation;

    @ManyToOne(fetch = FetchType.LAZY)
    @NaturalId
    private Acronym acronym;

    private String authorId;

    public Explanation() {
    }

    Explanation(Acronym acronym, @Nullable String authorId, String explanation) {
        this.acronym = acronym;
        this.authorId = authorId;
        setExplanation(explanation);
    }

    protected int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getExplanation() {
        return explanation;
    }

    protected void setExplanation(String explanation) {
        if (explanation.length() > MAX_EXPLANATION_LENGTH) {
            throw new IllegalArgumentException("Explanation exceeds maximum length of " + MAX_EXPLANATION_LENGTH + ": " + explanation.length());
        }

        this.explanation = explanation;
    }

    public Acronym getAcronym() {
        return acronym;
    }

    protected void setAcronym(Acronym acronym) {
        this.acronym = acronym;
    }

    @Nullable
    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(@Nullable String authorEmail) {
        this.authorId = authorEmail;
    }

    @Override
    public String toString() {
        return "Explanation{" +
                "id=" + id +
                ", explanation='" + explanation + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Explanation that)) return false;

        return Objects.equals(explanation, that.explanation) &&
                Objects.equals(acronym, that.acronym);
    }

    @Override
    public int hashCode() {
        return Objects.hash(explanation, acronym);
    }
}
