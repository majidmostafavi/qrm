package com.majidmostafavi.repository;

import com.majidmostafavi.entity.Comment;
import com.majidmostafavi.entity.Post;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepository;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.NotFoundException;
import org.bson.types.ObjectId;

import java.util.Optional;

public class CommentRepo implements ReactivePanacheMongoRepository<Comment> {

    public  Uni<Comment> updateComment(String id, Comment updateComment) {
        Uni<Comment> commentUni = findById(new ObjectId(id));

        return commentUni.call(comment -> {

            comment.content = updateComment.content;

            Uni<Post> uni = Post.findById(new ObjectId(comment.postId));
            return uni.call(posts -> {
                if (posts != null) {
                    Optional<Comment> com = posts.comments.stream()
                            .filter(comment1 -> comment1.equals(comment)).findFirst();
                    if (com.isPresent()) {
                        com.get().content = updateComment.content;
                    }
                }
                return Uni.createFrom().item(comment);
            }).chain(post -> post.persistOrUpdate());
        }).chain(comment -> {
            if (comment == null) {
                throw new NotFoundException();
            }
            return comment.persistOrUpdate();
        });


    }

    public  Uni<Void> deleteComment(String commentId) {
        Uni<Comment> commentUni = findById(new ObjectId(commentId));

        return commentUni.call(comment -> {

            Uni<Post> uni = Post.findById(new ObjectId(comment.postId));
            return uni.call(posts -> {
                if (posts != null) {
                    posts.comments.remove(comment);
                }
                return Uni.createFrom().item(comment);
            }).chain(post -> post.persistOrUpdate());
        }).chain(comment -> {
            if (comment == null) {
                throw new NotFoundException();
            }
            return comment.delete();
        });
    }

    public  Multi<Comment> streamAllComments() {
        return streamAll();
    }

    public  Multi<Comment> streamAllCommentsByPostId(String postId) {
        return stream("postId", postId);
    }



}
