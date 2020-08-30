package ru.sc222.devslife.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.sc222.devslife.R;
import ru.sc222.devslife.ui.adapters.EntriesRecyclerAdapter;
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
    private EntriesRecyclerAdapter entriesRecyclerAdapter;

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
        View root = inflater.inflate(R.layout.fragment_newsfeed, container, false);
        //final TextView textView = root.findViewById(R.id.section_label);
        newsfeedFragmentViewModel.getType().observe(getViewLifecycleOwner(), type ->
        {
            if (type != null) {
                switch (type) {
                    case NEWSFEED_TYPE_LATEST:
                    case NEWSFEED_TYPE_TOP:
                    case NEWSFEED_TYPE_HOT:
                        //textView.setText(type);
                        break;
                    default:
                        //textView.setText("WRONG TYPE");
                        break;
                }
            }
        });

        RecyclerView recyclerView = root.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        newsfeedFragmentViewModel.getEntries().observe(getViewLifecycleOwner(), entries -> {
            if (entries != null) {
                entriesRecyclerAdapter = new EntriesRecyclerAdapter(getContext(), entries);
                recyclerView.setAdapter(entriesRecyclerAdapter);
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
            //loadEntries();
            newsfeedFragmentViewModel.loadEntries();
        }
    }

    private void loadEntries() {
        //load from web
    }
}