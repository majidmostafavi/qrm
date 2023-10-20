package com.majidmostafavi.entity;

import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;
import jakarta.json.bind.annotation.JsonbTransient;

import java.time.LocalDateTime;

public class Comment extends ReactivePanacheMongoEntity {

    public String title;
    public String content;
    public LocalDateTime creationDate;
    @JsonbTransient
    public String postId;

    @Override
    public boolean equals(Object c) {
        if (c == null) return false;
        Comment comp = ((Comment) c);
        return comp.id.equals(id);
    }
}
