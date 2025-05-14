package com.example.codechallenge;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;

public class ForumComment {
    @DocumentId
    private String id;
    private String author_id;
    private String author_name;
    private String content;
    private Timestamp timestamp;

    public ForumComment() { }

    public ForumComment(String author_id, String author_name, String content, Timestamp timestamp) {
        this.author_id = author_id;
        this.author_name = author_name;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public String getAuthor_id() { return author_id; }
    public String getAuthor_name() { return author_name; }
    public String getContent() { return content; }
    public Timestamp getTimestamp() { return timestamp; }

    public void setId(String id) { this.id = id; }
    public void setAuthor_id(String author_id) { this.author_id = author_id; }
    public void setAuthor_name(String author_name) { this.author_name = author_name; }
    public void setContent(String content) { this.content = content; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}
