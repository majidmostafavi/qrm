package com.majidmostafavi.resource;

import com.majidmostafavi.entity.Comment;
import com.majidmostafavi.repository.CommentRepo;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.bson.types.ObjectId;

@Path("/comment")
public class CommentResource {

    @Inject
    CommentRepo commentRepo;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<Comment> list() {
        return commentRepo.streamAllComments();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Comment> getComment(@PathParam("id") String id) {
        return commentRepo.findById(new ObjectId(id));
    }

    @DELETE
    @Path("/{id}")
    public Uni<Void> deleteComment(@PathParam("id") String id) {
        return commentRepo.deleteComment(id);
    }

    @PUT
    @Path("/{id}")
    public Uni<Comment> update(@PathParam("id") String id, Comment updateComment) {
        return commentRepo.updateComment(id, updateComment);
    }
}
