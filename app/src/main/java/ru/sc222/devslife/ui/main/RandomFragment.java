package ru.sc222.devslife.ui.main;

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
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.lifecycle.ViewModelProviders;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.sc222.devslife.R;
import ru.sc222.devslife.network.APIInterface;
import ru.sc222.devslife.network.model.Entry;
import ru.sc222.devslife.network.ServiceGenerator;
import ru.sc222.devslife.ui.custom.ControllableFragment;

public class RandomFragment extends ControllableFragment {

    private RandomFragmentViewModel randomFragmentViewModel;
    private AppCompatImageView imageViewEntry;
    private LinearLayoutCompat toolbarEntry;
    private AppCompatTextView titleEntry;
    private AppCompatTextView subtitleEntry;
    private int defaultBgColor;
    private int defaultTitleColor;
    private int defaultSubtitleColor;

    private Callback<Entry> entryCallback = new Callback<Entry>() {
        @Override
        public void onResponse(@NonNull Call<Entry> call, Response<Entry> response) {

            if (response.isSuccessful()) {
                Entry entry = response.body();
                assert entry != null;
                randomFragmentViewModel.fixError(ErrorInfo.Error.CANT_LOAD_POST);

                Log.e("Loaded", "entry author: " + entry.getDescription());
                Log.e("Loaded", "entry desc: " + entry.getDescription());
                Log.e("Loaded", "entry id: " + entry.getId());
                Log.e("Loaded", "entry preview: " + entry.getPreviewURL());

                if (entry.getType().equals(Entry.TYPE_COUB)) {
                    randomFragmentViewModel.setError(new ErrorInfo(ErrorInfo.Error.COUB_NOT_SUPPORTED));
                    Log.e("ERROR", "post onResponse: " + "UNSUPPORTED COUB POST");
                } else {
                    randomFragmentViewModel.fixError(ErrorInfo.Error.COUB_NOT_SUPPORTED);
                    randomFragmentViewModel.addEntry(entry.buildSimpleEntry());
                    loadEntryImage(entry.buildSimpleEntry().getGifURL());
                }
            } else {
                try {
                    assert response.errorBody() != null;
                    randomFragmentViewModel.setError(new ErrorInfo(ErrorInfo.Error.CANT_LOAD_POST));
                    Log.e("ERROR", "post onResponse: " + response.errorBody().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFailure(@NonNull Call<Entry> call, @NonNull Throwable t) {
            randomFragmentViewModel.setError(new ErrorInfo(ErrorInfo.Error.CANT_LOAD_POST));
            Log.e("ERROR", "post onFailure: " + t.toString());
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        randomFragmentViewModel = ViewModelProviders.of(this).get(RandomFragmentViewModel.class);
        defaultBgColor = ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.default_bg_color);
        defaultTitleColor = ContextCompat.getColor(getContext(), R.color.default_title_color);
        defaultSubtitleColor = ContextCompat.getColor(getContext(), R.color.default_subtitle_color);
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

        randomFragmentViewModel.getCurrentEntry().observe(getViewLifecycleOwner(), entry -> {
            if (entry != null) {
                Log.e("update curr", "update");
                titleEntry.setText(entry.getAuthor());
                subtitleEntry.setText(entry.getDescription());
                if (entry.areColorsSet()) {
                    toolbarEntry.setBackgroundColor(entry.getBgColor());
                    titleEntry.setTextColor(entry.getTitleColor());
                    subtitleEntry.setTextColor(entry.getSubtitleColor());
                } else {
                    toolbarEntry.setBackgroundColor(defaultBgColor);
                    titleEntry.setTextColor(defaultTitleColor);
                    subtitleEntry.setTextColor(defaultSubtitleColor);
                }
            }
        });

        ProgressBar loadProgress = root.findViewById(R.id.entry_progressbar);
        FloatingActionButton fabPrevious = Objects.requireNonNull(getActivity()).findViewById(R.id.fab_previous);
        FloatingActionButton fabNext = getActivity().findViewById(R.id.fab_next);
        randomFragmentViewModel.getIsCurrentEntryImageLoaded().observe(getViewLifecycleOwner(),
                isLoaded -> {
                    if (isLoaded)
                        loadProgress.setVisibility(View.INVISIBLE);
                    else
                        loadProgress.setVisibility(View.VISIBLE);
                });

        randomFragmentViewModel.getCanLoadNext().observe(getViewLifecycleOwner(),fabNext::setEnabled);

        //TODO ANIMATE FAB TRANSITION
        randomFragmentViewModel.getCanLoadPrevious().observe(getViewLifecycleOwner(), fabPrevious::setEnabled);

        LinearLayoutCompat errorLayout = root.findViewById(R.id.error_layout);
        AppCompatImageView errorIcon = root.findViewById(R.id.error_icon);
        AppCompatTextView errorTitle = root.findViewById(R.id.error_title);
        Button errorButton = root.findViewById(R.id.error_button);
        randomFragmentViewModel.getError().observe(getViewLifecycleOwner(), errorInfo -> {
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
                        randomFragmentViewModel.setCanLoadPrevious(false);
                        randomFragmentViewModel.setCanLoadNext(false);
                        break;
                    case COUB_NOT_SUPPORTED:
                        errorIcon.setImageResource(R.drawable.ic_sorry);
                        errorTitle.setText(R.string.error_coub);
                        errorButton.setText(R.string.button_next_post);
                        break;
                }

                errorButton.setOnClickListener(view -> {
                    switch (errorInfo.getError()) {
                        case CANT_LOAD_POST:
                        case COUB_NOT_SUPPORTED:
                            loadEntry();
                            break;
                        case CANT_LOAD_IMAGE:
                            loadEntryImage(errorInfo.getRetryUrl());
                            break;
                    }
                });
            } else {
                errorLayout.setVisibility(View.GONE);
                toolbarEntry.setVisibility(View.VISIBLE);
            }
        });

        loadEntry();
        return root;
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
                            randomFragmentViewModel.setError(new ErrorInfo(ErrorInfo.Error.CANT_LOAD_IMAGE, (String) model));

                        assert e != null;
                        Log.e("ERROR", "image onLoadFailed: "+e.getMessage());
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                        randomFragmentViewModel.fixError(ErrorInfo.Error.CANT_LOAD_IMAGE);

                        randomFragmentViewModel.setIsCurrentEntryImageLoaded(true);
                        randomFragmentViewModel.setCanLoadNext(true);
                        if (dataSource == DataSource.REMOTE) {
                            Bitmap firstFrame = resource.getFirstFrame();
                            Palette p = Palette.from(firstFrame).generate();
                            Palette.Swatch vibrantSwatch = p.getVibrantSwatch();
                            if (vibrantSwatch != null) {
                                Log.e("NICE", "NICE PALETTE");
                                int bgColor = ColorUtils.setAlphaComponent(vibrantSwatch.getRgb(), 204);
                                int titleColor = vibrantSwatch.getTitleTextColor();
                                int subTitleColor = vibrantSwatch.getBodyTextColor();
                                randomFragmentViewModel.updateEntryColors(bgColor, titleColor, subTitleColor);
                            } else {
                                Log.e("bad", "BAD PALETTE");
                            }
                        }
                        return false;
                    }
                })
                .into(imageViewEntry);
    }

    @Override
    public void fabNextClicked() {
        titleEntry.setText("");
        subtitleEntry.setText("");
        loadEntry();
    }

    private void loadEntry() {
        Glide.with(Objects.requireNonNull(getContext())).clear(imageViewEntry);

        if (!randomFragmentViewModel.switchToNextEntry()) {

            Log.e("LOAD FROM WEB", "LOAD FROM WEB");
            //load new entry if there are no cached
            APIInterface service = ServiceGenerator.getRetrofit().create(APIInterface.class);
            service.getRandomEntry().enqueue(entryCallback);

            toolbarEntry.setBackgroundColor(defaultBgColor);
            titleEntry.setTextColor(defaultTitleColor);
            subtitleEntry.setTextColor(defaultSubtitleColor);
        } else {
            Log.e("LOADED CACHED", "LOADED CACHED");
            loadEntryImage(Objects.requireNonNull(randomFragmentViewModel.getCurrentEntry().getValue()).getGifURL());
        }
    }

    @Override
    public void fabPreviousClicked() {
        if (!randomFragmentViewModel.switchToPreviousEntry()) {
            Log.e("HIDE FAB", "AAAA");
        } else {
            Log.e("WTF FAB", "loaded from cache");

            // cached gifs are shown without errors
            randomFragmentViewModel.setError(new ErrorInfo(ErrorInfo.Error.NO_ERRORS));
            loadEntryImage(Objects.requireNonNull(randomFragmentViewModel.getCurrentEntry().getValue().getGifURL()));
        }
    }
}