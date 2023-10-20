package com.majidmostafavi.entity;

import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;

import java.time.LocalDateTime;
import java.util.List;

public class Post extends ReactivePanacheMongoEntity {

    public String title;
    public String content;
    public String author;
    public LocalDateTime creationDate;
    public List<Comment> comments;
}
