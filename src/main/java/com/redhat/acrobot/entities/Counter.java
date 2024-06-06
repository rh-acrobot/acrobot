package com.redhat.acrobot.entities;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;

@Entity
public class Counter {
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Basic(optional = false)
    private Instant timestamp = Instant.now();

    @Basic(optional = false)
    private String userId;

    @Basic(optional = false)
    private String message;

    public Counter() {
    }

    public Counter(String user, String msg) {
        this.userId = user;
        this.message = msg;
    }

    public Long getId() {
        return id;
    }

    protected void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userEmail) {
        this.userId = userEmail;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Counter that)) return false;
        return id.equals(that.id) && timestamp.equals(that.timestamp) && userId.equals(that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timestamp, userId);
    }

    @Override
    public String toString() {
        return "Counter{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", userId='" + userId + '\'' +
                '}';
    }
}
