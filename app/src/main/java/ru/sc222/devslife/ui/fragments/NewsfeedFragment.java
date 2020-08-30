package ru.sc222.devslife.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import ru.sc222.devslife.R;
import ru.sc222.devslife.custom.PageLoadError;
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
                PageLoadError error = PageLoadError.NO_ERRORS;
                boolean isEmpty = entries.size() == 0;
                if (isEmpty)
                    error = PageLoadError.NO_POSTS;
                newsfeedFragmentViewModel.setError(error);
                entriesRecyclerAdapter = new EntriesRecyclerAdapter(getContext(), entries,
                        newsfeedFragmentViewModel.getType().getValue());
                recyclerView.setAdapter(entriesRecyclerAdapter);
            }
        });

        LinearLayoutCompat errorLayout = root.findViewById(R.id.error_layout);
        AppCompatImageView errorIcon = root.findViewById(R.id.error_icon);
        AppCompatTextView errorTitle = root.findViewById(R.id.error_title);
        Button errorButton = root.findViewById(R.id.error_button);
        newsfeedFragmentViewModel.getError().observe(getViewLifecycleOwner(), new Observer<PageLoadError>() {
            @Override
            public void onChanged(PageLoadError error) {
                newsfeedFragmentViewModel.updateCanLoadPrevious();
                newsfeedFragmentViewModel.updateCanLoadNext();
                if (error != PageLoadError.NO_ERRORS) {
                    recyclerView.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);

                    switch (error) {
                        case CANT_LOAD_PAGE:
                            errorIcon.setImageResource(R.drawable.ic_offline);
                            errorTitle.setText(R.string.error_offline);
                            break;
                        case NO_POSTS:
                            errorIcon.setImageResource(R.drawable.ic_sorry);
                            errorTitle.setText(R.string.no_posts);
                            break;
                    }

                    errorButton.setOnClickListener(view -> {
                        switch (error) {
                            case NO_POSTS:
                            case CANT_LOAD_PAGE:
                                newsfeedFragmentViewModel.loadEntries(0);
                                break;
                        }
                    });
                } else {
                    errorLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });

        FloatingActionButton fabPrevious = Objects.requireNonNull(getActivity()).findViewById(R.id.fab_previous);
        FloatingActionButton fabNext = getActivity().findViewById(R.id.fab_next);

        newsfeedFragmentViewModel.getCanLoadNext().observe(getViewLifecycleOwner(), enabled ->
        {
            if (isVisible)
                fabNext.setEnabled(enabled);
        });

        newsfeedFragmentViewModel.getCanLoadPrevious().observe(getViewLifecycleOwner(), enabled ->
        {
            if (isVisible)
                fabPrevious.setEnabled(enabled);
        });

        return root;
    }

    @Override
    public void fabNextClicked() {
        newsfeedFragmentViewModel.loadEntries(1);
    }

    @Override
    public void fabPreviousClicked() {
        newsfeedFragmentViewModel.loadEntries(-1);
    }

    @Override
    public boolean isFabNextEnabled() {
        return newsfeedFragmentViewModel.getCanLoadNext().getValue();
    }

    @Override
    public boolean isFabPreviousEnabled() {
        return newsfeedFragmentViewModel.getCanLoadPrevious().getValue();
    }

    @Override
    public void setIsVisible(boolean isVisible) {
        this.isVisible = isVisible;

        if (isFirstRun && isVisible) {
            Log.e("newsfeed fragment", "LOAD" + newsfeedFragmentViewModel.getType().getValue() + "FIRST TIME");
            isFirstRun = false;
            newsfeedFragmentViewModel.loadEntries(0);
        }
    }
}