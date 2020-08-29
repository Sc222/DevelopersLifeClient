package ru.sc222.devslife.ui.main;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.sc222.devslife.R;
import ru.sc222.devslife.core.SimpleEntry;
import ru.sc222.devslife.network.APIInterface;
import ru.sc222.devslife.ui.ControllableFragment;
import ru.sc222.devslife.network.Entry;
import ru.sc222.devslife.network.ServiceGenerator;

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
        public void onResponse(Call<Entry> call, Response<Entry> response) {

            if (response.isSuccessful()) {
                Entry entry = response.body();
                assert entry != null;
                Log.e("Loaded", "entry author: " + entry.getDescription());
                Log.e("Loaded", "entry desc: " + entry.getDescription());
                Log.e("Loaded", "entry id: " + entry.getId());
                Log.e("Loaded", "entry preview: " + entry.getPreviewURL());

                if (entry.getType().equals(Entry.TYPE_COUB)) {
                    Log.e("Bad random post", "it has " + entry.getType() + " type");
                    //todo SHOW UNSUPPORTED POST ERROR MESSAGE
                } else {
                    randomFragmentViewModel.addEntry(entry.buildSimpleEntry());
                    loadEntryImage(entry.buildSimpleEntry());
                }
            } else {
                //todo SHOW ERROR
            }
        }

        @Override
        public void onFailure(Call<Entry> call, Throwable t) {
            //todo SHOW ERROR
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
        //TODO ANIMATE FAB TRANSITION
        randomFragmentViewModel.getIsCurrentEntryImageLoaded().observe(getViewLifecycleOwner(),
                isLoaded -> {
                    fabNext.setEnabled(isLoaded);
                    if (isLoaded)
                        loadProgress.setVisibility(View.INVISIBLE);
                    else
                        loadProgress.setVisibility(View.VISIBLE);
                });

        //TODO ANIMATE FAB TRANSITION
        randomFragmentViewModel.getCanLoadPrevious().observe(getViewLifecycleOwner(), fabPrevious::setEnabled);

        loadEntry();
        return root;
    }

    private void loadEntryImage(SimpleEntry entry) {

        //next image can't be loaded while current wasn't
        randomFragmentViewModel.setIsCurrentEntryImageLoaded(false);
        Log.e("Loaded", "entry desc: " + entry.getDescription());
        Log.e("Loaded", "entry gif: " + entry.getGifURL());

        String correctGifUrl = entry.getGifURL().replaceFirst("http", "https");
        Log.e("Loaded", "url: " + correctGifUrl);
        Glide.with(Objects.requireNonNull(getActivity()))
                .asGif()
                .load(correctGifUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.entry_placeholder)
                .listener(new RequestListener<GifDrawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                        Log.e("ERROR", "ERROR LOADING IMAGE");
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                        randomFragmentViewModel.setIsCurrentEntryImageLoaded(true);
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
            loadEntryImage(Objects.requireNonNull(randomFragmentViewModel.getCurrentEntry().getValue()));
        }
    }

    @Override
    public void fabPreviousClicked() {

        if (!randomFragmentViewModel.switchToPreviousEntry()) {
            Log.e("HIDE FAB", "AAAA");
        } else {
            Log.e("WTF FAB", "loaded from cache");
            loadEntryImage(Objects.requireNonNull(randomFragmentViewModel.getCurrentEntry().getValue()));
        }
    }
}