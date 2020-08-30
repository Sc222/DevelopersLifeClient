package ru.sc222.devslife.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import ru.sc222.devslife.network.model.Entries;
import ru.sc222.devslife.network.model.Entry;

public interface APIInterface {

    @GET("/random?json=true")
    Call<Entry> getRandomEntry();

    //category can be: latest, hot, top
    //page size can change from 5 to 50
    //types can be: 'gif', 'coub', 'gif, coub'
    @GET("/{category}/{page}?json=true")
    Call<Entries> getEntries(@Path("category") String type,
                             @Path("page") int page,
                             @Query("pageSize") int pageSize,
                             @Query("types") String types);
}
