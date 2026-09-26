package net.alphaben.commentanalyzer.model;


import java.time.LocalTime;

public class Message {

    public enum Role { USER, AI }

    private final Role role;
    private final String text;
    private final LocalTime timestamp;

    public Message(Role role, String text) {
        this.role = role;
        this.text = text;
        this.timestamp = LocalTime.now();
    }

    public Role getRole() { return role; }
    public String getText() { return text; }
    public LocalTime getTimestamp() { return timestamp; }
}
