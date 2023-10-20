package com.majidmostafavi.resource;

import com.majidmostafavi.entity.Comment;
import com.majidmostafavi.entity.Post;
import com.majidmostafavi.repository.PostRepo;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Path("/post")
public class PostResource {

    @Inject
    PostRepo postRepo;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<Post> list() {
        return postRepo.streamAllPosts();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> addPost(Post post) {
        post.creationDate = LocalDateTime.now();
        return post.<Post>persist().map(v ->
                Response.created(URI.create("/posts/" + v.id.toString()))
                        .entity(post).build());
    }

    @PUT
    @Path("/{id}")
    public Uni<Post> update(@PathParam("id") String id, Post updatePost) {
        return postRepo.updatePost(id, updatePost);
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Post> getPost(@PathParam("id") String id) {
        return postRepo.findById(new ObjectId(id));
    }

    @DELETE
    @Path("/{id}")
    public Uni<Void> deletePost(@PathParam("id") String id) {
        return postRepo.deletePost(id);
    }

    @GET
    @Path("/search")
    public Uni<List<Post>> search(@QueryParam("author") String author, @QueryParam("title") String title,
                                  @QueryParam("dateFrom") String dateFrom, @QueryParam("dateTo") String dateTo) {
        if (author != null) {
            return postRepo.find("{'author': ?1,'title': ?2}", author, title).list();
        }
        return postRepo
                .find("{'creationDate': {$gte: ?1}, 'creationDate': {$lte: ?2}}", ZonedDateTime.parse(dateFrom).toLocalDateTime(),
                        ZonedDateTime.parse(dateTo).toLocalDateTime()).list();
    }

    @GET
    @Path("/search2")
    public Uni<List<Post>> searchCustomQueries(@QueryParam("authors") List<String> authors) {

        // using Document
        return Post.find(new Document("author", new Document("$in", authors))).list();

        // using a raw JSON query
        //Post.find("{'$or': {'author':John Doe, 'author':Grace Kelly}}");
        //Post.find("{'author': {'$in': [John Doe, Grace Kelly]}}");

        // using Panache QL
        //Post.find("author in (John Doe,Grace Kelly)");

    }

    @PUT
    @Path("/{id}/comment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> addCommentToPost(@PathParam("id") String id, Comment comment) {
        return postRepo.addCommentToPost(comment, id).map(v -> Response.accepted(v).build());
    }

}
