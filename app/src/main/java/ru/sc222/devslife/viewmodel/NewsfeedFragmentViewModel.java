package ru.sc222.devslife.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.sc222.devslife.custom.PageLoadError;
import ru.sc222.devslife.custom.SimpleEntry;
import ru.sc222.devslife.network.APIInterface;
import ru.sc222.devslife.network.ServiceGenerator;
import ru.sc222.devslife.network.model.Entries;
import ru.sc222.devslife.network.model.Entry;

public class NewsfeedFragmentViewModel extends ViewModel {

    private MutableLiveData<String> type = new MutableLiveData<>(null);
    private MutableLiveData<Integer> page = new MutableLiveData<>(0);
    private MutableLiveData<PageLoadError> error = new MutableLiveData<>(PageLoadError.NO_ERRORS);
    private MutableLiveData<List<SimpleEntry>> entries = new MutableLiveData<>(null);

    private MutableLiveData<Boolean> canLoadPrevious = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> canLoadNext = new MutableLiveData<>(false);

    private Callback<Entries> entriesCallback = new Callback<Entries>() {
        @Override
        public void onResponse(@NonNull Call<Entries> call, Response<Entries> response) {
            if (response.isSuccessful()) {
                updateCanLoadNext();
                Entries entries = response.body();
                assert entries != null;
                Log.d("Loaded", "entries page array size: " + entries.getEntries().size());
                Log.d("Loaded", "entries total: " + entries.getTotalCount());
                setEntries(entries);
            } else {
                Log.e("Error", "page entries onResponse");
                setError(PageLoadError.CANT_LOAD_PAGE);
            }
        }

        @Override
        public void onFailure(@NonNull Call<Entries> call, @NonNull Throwable t) {
            Log.e("Error", "page entries onFailure: " + t.toString());
            setError(PageLoadError.CANT_LOAD_PAGE);
        }
    };

    public MutableLiveData<String> getType() {
        return type;
    }

    public void setType(String type) {
        this.type.setValue(type);
    }

    public MutableLiveData<Integer> getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page.setValue(page);
    }

    public MutableLiveData<PageLoadError> getError() {
        return error;
    }

    public void setError(PageLoadError error) {
        this.error.setValue(error);
    }

    public void setEntries(Entries entries) {
        List<SimpleEntry> result = new ArrayList<>();
        for (Entry entry : entries.getEntries()) {
            result.add(entry.buildSimpleEntry());
        }

        this.entries.setValue(result);
    }

    public MutableLiveData<List<SimpleEntry>> getEntries() {
        return entries;
    }

    public void loadEntries(Integer pageOffset) {
        this.canLoadNext.setValue(false);
        setPage(getPage().getValue() + pageOffset);
        APIInterface service = ServiceGenerator.getRetrofit().create(APIInterface.class);
        service.getEntries(this.getType().getValue(), getPage().getValue(), Entries.PREFERRED_PAGE_SIZE, Entries.PREFERRED_TYPES).enqueue(entriesCallback);
    }

    public MutableLiveData<Boolean> getCanLoadNext() {
        return canLoadNext;
    }

    public void updateCanLoadNext() {
        PageLoadError errors = getError().getValue();
        this.canLoadNext.setValue(errors == PageLoadError.NO_ERRORS);
    }

    public MutableLiveData<Boolean> getCanLoadPrevious() {
        return canLoadPrevious;
    }

    public void updateCanLoadPrevious() {
        PageLoadError errors = getError().getValue();
        Integer page = getPage().getValue();
        boolean result = page > 0 && (errors == PageLoadError.NO_POSTS || errors == PageLoadError.NO_ERRORS);
        this.canLoadPrevious.setValue(result);
    }
}