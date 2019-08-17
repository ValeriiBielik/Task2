package com.my.bielik.task2.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Retro {

    public static final String BASE_URL = "https://www.flickr.com";

    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private Retro() {
    }

    public static FlickrApi buildFlickrApi() {
        return getRetrofit().create(FlickrApi.class);
    }

    private static Retrofit getRetrofit() {
        return retrofit;
    }
}
