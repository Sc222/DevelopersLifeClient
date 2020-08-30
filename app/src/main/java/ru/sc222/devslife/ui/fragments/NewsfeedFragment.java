package ru.sc222.devslife.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.sc222.devslife.R;
import ru.sc222.devslife.network.APIInterface;
import ru.sc222.devslife.network.ServiceGenerator;
import ru.sc222.devslife.network.model.Entries;
import ru.sc222.devslife.ui.custom.ControllableFragment;
import ru.sc222.devslife.viewmodel.NewsfeedFragmentViewModel;

public class NewsfeedFragment extends ControllableFragment {

    private static final String NEWSFEED_TYPE = "type";
    public static final String NEWSFEED_TYPE_LATEST = "latest";
    public static final String NEWSFEED_TYPE_TOP = "top";
    public static final String NEWSFEED_TYPE_HOT = "hot";

    private boolean isVisible = false;
    private boolean isFirstRun = true;

    private NewsfeedFragmentViewModel newsfeedFragmentViewModel;

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
                /*if (entry.getType().equals(Entry.TYPE_COUB)) {
                    randomFragmentViewModel.setError(new ErrorInfo(LoadError.COUB_NOT_SUPPORTED));
                    Log.e("ERROR", "page entries onResponse: " + "UNSUPPORTED COUB POST");
                } else {
                    randomFragmentViewModel.fixError(LoadError.COUB_NOT_SUPPORTED);
                    randomFragmentViewModel.addEntry(entry.buildSimpleEntry());
                    loadEntryImage(entry.buildSimpleEntry().getGifURL());
                }*/
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
            //randomFragmentViewModel.setError(new ErrorInfo(LoadError.CANT_LOAD_POST));
            Log.e("ERROR", "page entries onFailure: " + t.toString());
        }
    };

    public static NewsfeedFragment newInstance(String type) {
        NewsfeedFragment fragment = new NewsfeedFragment();
        Bundle bundle = new Bundle();
        bundle.putString(NEWSFEED_TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newsfeedFragmentViewModel = new ViewModelProvider(this).get(NewsfeedFragmentViewModel.class);

        if (getArguments() != null) {
            String type = getArguments().getString(NEWSFEED_TYPE);
            newsfeedFragmentViewModel.setType(type);
        }

    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        final TextView textView = root.findViewById(R.id.section_label);
        newsfeedFragmentViewModel.getType().observe(getViewLifecycleOwner(), type ->
        {
            if (type != null) {
                switch (type) {
                    case NEWSFEED_TYPE_LATEST:
                    case NEWSFEED_TYPE_TOP:
                    case NEWSFEED_TYPE_HOT:
                        textView.setText(type);
                        break;
                    default:
                        textView.setText("WRONG TYPE");
                        break;
                }
            }
        });

        //load new entry if there are no cached

        return root;
    }

    @Override
    public void fabNextClicked() {

    }

    @Override
    public void fabPreviousClicked() {

    }

    @Override
    public boolean isFabNextEnabled() {
        return true;
    }

    @Override
    public boolean isFabPreviousEnabled() {
        return true;
    }

    @Override
    public void setIsVisible(boolean isVisible) {
        this.isVisible = isVisible;

        if (isFirstRun && isVisible) {
            Log.e("newsfeed fragment", "LOAD" + newsfeedFragmentViewModel.getType().getValue() + "FIRST TIME");
            isFirstRun = false;
            loadEntries();
        }
    }

    private void loadEntries() {
        //load from web
        APIInterface service = ServiceGenerator.getRetrofit().create(APIInterface.class);
        service.getEntries(newsfeedFragmentViewModel.getType().getValue(), 0, Entries.PREFERRED_PAGE_SIZE, Entries.PREFERRED_TYPES).enqueue(entriesCallback);
    }
}