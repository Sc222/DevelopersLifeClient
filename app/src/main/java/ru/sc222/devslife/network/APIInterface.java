package ru.sc222.devslife.network;

import retrofit2.Call;
import retrofit2.http.GET;

public interface APIInterface {

    @GET("/random?json=true")
    Call<Entry> getRandomEntry();
}