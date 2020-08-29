package ru.sc222.devslife.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {
    private static final String BASE_URL = "https://developerslife.ru/";
    public static final String TYPE_GIF = "gif";
    private static Retrofit retrofit;
    private static Gson gson;

    public static Retrofit getRetrofit() {

        //todo add extra method call to exclude not needed fields
        gson = new GsonBuilder()
                .create();

        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}