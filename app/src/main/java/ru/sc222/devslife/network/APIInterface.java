package ru.sc222.devslife.network;

import retrofit2.Call;
import retrofit2.http.GET;
import ru.sc222.devslife.network.model.Entry;

public interface APIInterface {
    String GET_RANDOM_POST_URL ="/random?json=true";

    @GET(GET_RANDOM_POST_URL)
    Call<Entry> getRandomEntry();
}
