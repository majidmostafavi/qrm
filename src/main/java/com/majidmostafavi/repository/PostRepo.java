package com.majidmostafavi.repository;

import com.majidmostafavi.entity.Comment;
import com.majidmostafavi.entity.Post;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepository;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.List;

@RequestScoped
public class PostRepo implements ReactivePanacheMongoRepository<Post> {
    @Inject
    CommentRepo commentRepo;

    public Uni<Post> updatePost(String id, Post updatePost) {
        Uni<Post> postUni = Post.findById(new ObjectId(id));
        return postUni
                .onItem().transform(post -> {
                    post.content = updatePost.content;
                    post.title = updatePost.title;
                    return post;
                }).call(post -> post.persistOrUpdate());
    }


    public  Uni<Post> addCommentToPost(Comment comment, String postId) {
        Uni<Post> postUni = findById(new ObjectId(postId));

        return postUni.onItem().transform(post -> {

            if (post.comments == null) {
                post.comments = List.of(comment);
            } else {
                post.comments.add(comment);
            }
            comment.creationDate = LocalDateTime.now();
            comment.postId = postId;
            return post;
        }).call(post -> comment.persist().chain(() -> post.persistOrUpdate()));
    }

    public  Uni<Void> deletePost(String postId) {
        Uni<Post> postUni = findById(new ObjectId(postId));
        Multi<Comment> commentsUni = commentRepo.streamAllCommentsByPostId(postId);

        return postUni.call(post -> commentsUni.onItem().call(comment -> comment.delete())
                .collect().asList()).chain(post -> {
            if (post == null) {
                throw new NotFoundException();
            }
            return post.delete();
        });
    }

    public Multi<Post> streamAllPosts() {
        return streamAll();
    }
}
