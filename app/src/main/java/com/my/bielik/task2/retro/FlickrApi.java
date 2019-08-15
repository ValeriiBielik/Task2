package com.my.bielik.task2.retro;

import com.my.bielik.task2.retro.response.FlickrResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FlickrApi {

    @GET("/services/rest/?method=flickr.photos.search&format=json&nojsoncallback=1")
    Call<FlickrResponse> getPhotos(@Query("api_key") String apiKey, @Query("text") String text, @Query("media") String media, @Query("page") int page);
}
