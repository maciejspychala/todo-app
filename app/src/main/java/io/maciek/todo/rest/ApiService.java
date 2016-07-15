package io.maciek.todo.rest;

import java.util.List;

import io.maciek.todo.rest.models.Note;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by maciej on 12.07.16.
 */
public interface ApiService {

    @GET("todo")
    Observable<List<Note>> getTodos(@Query("_start") int start, @Query("_end") int end);

    @PUT("todo/{id}")
    Observable<Note> postTodo(@Path("id") String id, @Body Note note);
}
