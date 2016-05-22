package io.swift.kata.androiddata.syncadapter;

import io.swift.kata.androiddata.model.Name;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.POST;
import retrofit.http.Path;

public interface NameService {
    @POST("/names")
    Boolean postName(@Body Name name);

    @DELETE("/names/{id}")
    Boolean deleteName(@Path("id") Integer id);
}
