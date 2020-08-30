package ru.sc222.devslife.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.sc222.devslife.custom.SimpleEntry;
import ru.sc222.devslife.network.APIInterface;
import ru.sc222.devslife.network.ServiceGenerator;
import ru.sc222.devslife.network.model.Entries;
import ru.sc222.devslife.network.model.Entry;

public class NewsfeedFragmentViewModel extends ViewModel {

    private MutableLiveData<String> type = new MutableLiveData<>(null);
    private MutableLiveData<List<SimpleEntry>> entries = new MutableLiveData<>(null);

    //todo create livedata for page


    private Callback<Entries> entriesCallback = new Callback<Entries>() {
        @Override
        public void onResponse(@NonNull Call<Entries> call, Response<Entries> response) {
            if (response.isSuccessful()) {
                Entries entries = response.body();
                assert entries != null;
                //randomFragmentViewModel.fixError(LoadError.CANT_LOAD_POST);
                Log.e("Loaded", "entries page array size: " + entries.getEntries().size());
                Log.e("Loaded", "entries total: " + entries.getTotalCount());

                //todo check if array is not empty than it's the last page (or there are no posts at all)
                setEntries(entries);
            } else {
                try {
                    assert response.errorBody() != null;
                    //randomFragmentViewModel.setError(new ErrorInfo(LoadError.CANT_LOAD_POST));
                    Log.e("ERROR", "page entries onResponse: " + response.errorBody().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFailure(@NonNull Call<Entries> call, @NonNull Throwable t) {
            Log.e("ERROR", "page entries onFailure: " + t.toString());
        }
    };


    public MutableLiveData<String> getType() {
        return type;
    }

    public void setType(String type) {
        this.type.setValue(type);
    }

    public void setEntries(Entries entries) {
        List<SimpleEntry> result = new ArrayList<>();
        for (Entry entry : entries.getEntries()) {
            result.add(entry.buildSimpleEntry());
        }

        this.entries.setValue(result);
    }


    //we will call this method to get the data
    public MutableLiveData<List<SimpleEntry>> getEntries() {
        return entries;
    }


    public void loadEntries() {
        APIInterface service = ServiceGenerator.getRetrofit().create(APIInterface.class);
        service.getEntries(this.getType().getValue(), 0, Entries.PREFERRED_PAGE_SIZE, Entries.PREFERRED_TYPES).enqueue(entriesCallback);
    }
}