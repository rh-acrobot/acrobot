package com.redhat.acrobot.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.NaturalId;

import java.io.Serializable;
import java.util.Objects;

@Entity
public class Explanation implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NaturalId
    private String explanation;

    @ManyToOne(fetch = FetchType.LAZY)
    @NaturalId
    private Acronym acronym;

    @Basic(optional = false)
    private String authorId;

    public Explanation() {
    }

    Explanation(Acronym acronym, String authorId, String explanation) {
        this.acronym = acronym;
        this.authorId = authorId;
        this.explanation = explanation;
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
        this.explanation = explanation;
    }

    public Acronym getAcronym() {
        return acronym;
    }

    protected void setAcronym(Acronym acronym) {
        this.acronym = acronym;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorEmail) {
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
