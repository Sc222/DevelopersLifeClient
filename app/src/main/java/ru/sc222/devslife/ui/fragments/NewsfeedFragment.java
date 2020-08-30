package ru.sc222.devslife.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import ru.sc222.devslife.R;
import ru.sc222.devslife.ui.custom.ControllableFragment;
import ru.sc222.devslife.viewmodel.NewsfeedFragmentViewModel;

public class NewsfeedFragment extends ControllableFragment {

    private static final String NEWSFEED_TYPE = "type";
    public static final String NEWSFEED_TYPE_LATEST = "latest";
    public static final String NEWSFEED_TYPE_BEST = "best";
    public static final String NEWSFEED_TYPE_HOT = "hot";

    private boolean isVisible = false;

    private NewsfeedFragmentViewModel newsfeedFragmentViewModel;

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
                        textView.setText(type);
                        break;
                    case NEWSFEED_TYPE_BEST:
                        textView.setText(type);
                        break;
                    case NEWSFEED_TYPE_HOT:
                        textView.setText(type);
                        break;
                    default:
                        textView.setText("WRONG TYPE");
                        break;
                }
            }
        });
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
    }
}