package ru.sc222.devslife.ui.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.sc222.devslife.R;
import ru.sc222.devslife.custom.ErrorInfo;
import ru.sc222.devslife.custom.LoadError;
import ru.sc222.devslife.custom.UiColorSet;
import ru.sc222.devslife.network.APIInterface;
import ru.sc222.devslife.network.ServiceGenerator;
import ru.sc222.devslife.network.model.Entry;
import ru.sc222.devslife.ui.custom.ControllableFragment;
import ru.sc222.devslife.utils.PaletteUtils;
import ru.sc222.devslife.viewmodel.RandomFragmentViewModel;

public class RandomFragment extends ControllableFragment {

    private boolean isVisible = false;
    private boolean isFirstRun = true;


    private RandomFragmentViewModel randomFragmentViewModel;
    private AppCompatImageView imageViewEntry;
    private LinearLayoutCompat toolbarEntry;
    private AppCompatTextView titleEntry;
    private AppCompatTextView subtitleEntry;

    private Callback<Entry> entryCallback = new Callback<Entry>() {
        @Override
        public void onResponse(@NonNull Call<Entry> call, Response<Entry> response) {
            if (response.isSuccessful()) {
                Entry entry = response.body();
                assert entry != null;
                randomFragmentViewModel.fixError(LoadError.CANT_LOAD_POST);
                Log.e("Loaded", "entry author: " + entry.getDescription());
                Log.e("Loaded", "entry desc: " + entry.getDescription());
                Log.e("Loaded", "entry id: " + entry.getId());
                Log.e("Loaded", "entry preview: " + entry.getPreviewURL());
                if (entry.getType().equals(Entry.TYPE_COUB)) {
                    randomFragmentViewModel.setError(new ErrorInfo(LoadError.COUB_NOT_SUPPORTED));
                    Log.e("ERROR", "post onResponse: " + "UNSUPPORTED COUB POST");
                } else {
                    randomFragmentViewModel.fixError(LoadError.COUB_NOT_SUPPORTED);
                    randomFragmentViewModel.addEntry(entry.buildSimpleEntry());
                    loadEntryImage(entry.buildSimpleEntry().getGifURL());
                }
            } else {
                randomFragmentViewModel.setError(new ErrorInfo(LoadError.CANT_LOAD_POST));
                Log.e("ERROR", "post onResponse: ");
            }
        }

        @Override
        public void onFailure(@NonNull Call<Entry> call, @NonNull Throwable t) {
            randomFragmentViewModel.setError(new ErrorInfo(LoadError.CANT_LOAD_POST));
            Log.e("ERROR", "post onFailure: " + t.toString());
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        randomFragmentViewModel = new ViewModelProvider(this).get(RandomFragmentViewModel.class);
    }


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_random, container, false);
        imageViewEntry = root.findViewById(R.id.entry_image);
        toolbarEntry = root.findViewById(R.id.entry_toolbar);
        titleEntry = root.findViewById(R.id.entry_title);
        subtitleEntry = root.findViewById(R.id.entry_subtitle);
        ProgressBar loadProgress = root.findViewById(R.id.entry_progressbar);
        FloatingActionButton fabPrevious = Objects.requireNonNull(getActivity()).findViewById(R.id.fab_previous);
        FloatingActionButton fabNext = getActivity().findViewById(R.id.fab_next);
        LinearLayoutCompat errorLayout = root.findViewById(R.id.error_layout);
        AppCompatImageView errorIcon = root.findViewById(R.id.error_icon);
        AppCompatTextView errorTitle = root.findViewById(R.id.error_title);
        Button errorButton = root.findViewById(R.id.error_button);
        ProgressBar errorProgressBar = root.findViewById(R.id.error_progressbar);
        setupObservers(loadProgress, fabPrevious, fabNext, errorLayout, errorIcon, errorTitle, errorButton, errorProgressBar);
        return root;
    }

    private void setupObservers(ProgressBar loadProgress, FloatingActionButton fabPrevious,
                                FloatingActionButton fabNext, LinearLayoutCompat errorLayout,
                                AppCompatImageView errorIcon, AppCompatTextView errorTitle,
                                Button errorButton, ProgressBar errorProgressBar) {
        randomFragmentViewModel.getCurrentEntry().observe(getViewLifecycleOwner(), entry -> {
            if (entry != null) {
                Log.e("update curr", "update");
                titleEntry.setText(entry.getAuthor());
                subtitleEntry.setText(entry.getDescription());

                UiColorSet colorSet = entry.getColorSet();
                if (colorSet == null)
                    colorSet = randomFragmentViewModel.getDefaultColorSet();
                toolbarEntry.setBackgroundColor(colorSet.getBgColor());
                titleEntry.setTextColor(colorSet.getTitleColor());
                subtitleEntry.setTextColor(colorSet.getSubtitleColor());
            }
        });
        randomFragmentViewModel.getIsCurrentEntryImageLoaded().observe(getViewLifecycleOwner(),
                isLoaded -> {
                    if (isLoaded)
                        loadProgress.setVisibility(View.INVISIBLE);
                    else
                        loadProgress.setVisibility(View.VISIBLE);
                });

        randomFragmentViewModel.getCanLoadNext().observe(getViewLifecycleOwner(), enabled ->
        {
            if (isVisible)
                fabNext.setEnabled(enabled);
        });

        randomFragmentViewModel.getCanLoadPrevious().observe(getViewLifecycleOwner(), enabled ->
        {
            if (isVisible)
                fabPrevious.setEnabled(enabled);
        });


        randomFragmentViewModel.getError().observe(getViewLifecycleOwner(), errorInfo -> {
            randomFragmentViewModel.updateCanLoadPrevious();
            errorProgressBar.setVisibility(View.INVISIBLE);
            if (errorInfo.hasErrors()) {
                imageViewEntry.setImageResource(R.drawable.gray);
                toolbarEntry.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);

                switch (errorInfo.getError()) {
                    case CANT_LOAD_POST:
                    case CANT_LOAD_IMAGE:
                        errorIcon.setImageResource(R.drawable.ic_offline);
                        errorTitle.setText(R.string.error_offline);
                        errorButton.setText(R.string.button_retry);
                        randomFragmentViewModel.setCanLoadNext(false);
                        break;
                    case COUB_NOT_SUPPORTED:
                        errorIcon.setImageResource(R.drawable.ic_sorry);
                        errorTitle.setText(R.string.error_coub);
                        errorButton.setText(R.string.button_next_post);
                        randomFragmentViewModel.setCanLoadNext(false);
                        break;
                }

                errorButton.setOnClickListener(view -> {
                    if (errorProgressBar.getVisibility() == View.INVISIBLE) {
                        errorProgressBar.setVisibility(View.VISIBLE);
                        switch (errorInfo.getError()) {
                            case CANT_LOAD_POST:
                            case COUB_NOT_SUPPORTED:
                                loadEntry();
                                break;
                            case CANT_LOAD_IMAGE:
                                loadEntryImage(errorInfo.getRetryUrl());
                                break;
                        }
                    }
                });
            } else {
                errorLayout.setVisibility(View.GONE);
                toolbarEntry.setVisibility(View.VISIBLE);
            }
        });
    }

    private void loadEntryImage(String url) {
        randomFragmentViewModel.setIsCurrentEntryImageLoaded(false);
        randomFragmentViewModel.setCanLoadNext(false);

        Log.e("Loaded", "image url: " + url);
        Glide.with(Objects.requireNonNull(getActivity()))
                .asGif()
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.gray)
                .listener(new RequestListener<GifDrawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                        if (model instanceof String)
                            randomFragmentViewModel.setError(new ErrorInfo(LoadError.CANT_LOAD_IMAGE, (String) model));
                        Log.e("ERROR", "image onLoadFailed");
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                        randomFragmentViewModel.fixError(LoadError.CANT_LOAD_IMAGE);
                        randomFragmentViewModel.setIsCurrentEntryImageLoaded(true);
                        randomFragmentViewModel.setCanLoadNext(true);
                        if (dataSource == DataSource.REMOTE) {
                            Bitmap bitmap = resource.getFirstFrame();
                            UiColorSet colorSet = PaletteUtils.getColorsFromBitmap(bitmap, randomFragmentViewModel.getDefaultColorSet());
                            randomFragmentViewModel.setColorSet(colorSet);
                        }
                        return false;
                    }
                })
                .into(imageViewEntry);
    }


    @Override
    public void fabNextClicked() {
        loadEntry();
    }

    private void loadRandomEntryFromSite() {
        APIInterface service = ServiceGenerator.getRetrofit().create(APIInterface.class);
        service.getRandomEntry().enqueue(entryCallback);
    }

    private void setupEntryLayoutPlaceholders() {
        titleEntry.setText("");
        subtitleEntry.setText("");
        UiColorSet colorSet = randomFragmentViewModel.getDefaultColorSet();
        toolbarEntry.setBackgroundColor(colorSet.getBgColor());
        titleEntry.setTextColor(colorSet.getTitleColor());
        subtitleEntry.setTextColor(colorSet.getSubtitleColor());
    }

    private void loadEntry() {
        if (!randomFragmentViewModel.switchToNextCachedEntry()) {
            randomFragmentViewModel.setCanLoadNext(false);
            Log.e("Error", "No cached previous entries");
            Log.d("Load", "Loading from web");
            setupEntryLayoutPlaceholders();
            loadRandomEntryFromSite();
        } else {
            Log.d("Nice", "Load next entry from cache");
            loadEntryImage(Objects.requireNonNull(randomFragmentViewModel.getCurrentEntry().getValue()).getGifURL());
        }
    }

    @Override
    public void fabPreviousClicked() {
        if (!randomFragmentViewModel.switchToPreviousCachedEntry()) {
            Log.e("Error", "No cached previous entries");
        } else {
            Log.d("Nice", "Load previous entry from cache");
            loadEntryImage(Objects.requireNonNull(Objects.requireNonNull(randomFragmentViewModel.getCurrentEntry().getValue()).getGifURL()));
        }
    }

    @Override
    public boolean isFabNextEnabled() {
        return randomFragmentViewModel.getCanLoadNext().getValue();
    }

    @Override
    public boolean isFabPreviousEnabled() {
        return randomFragmentViewModel.getCanLoadPrevious().getValue();
    }

    @Override
    public void setIsVisible(boolean isVisible) {
        this.isVisible = isVisible;
        Log.e("RANDOM", "BECOMES: " + isVisible);
        if (isFirstRun && isVisible) {
            Log.e("random", "LOAD ENTRY FIRST TIME");
            isFirstRun = false;
            loadRandomEntryFromSite();
        }
    }
}