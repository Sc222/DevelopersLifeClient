package ru.sc222.devslife.ui.main;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.sc222.devslife.R;
import ru.sc222.devslife.core.APIInterface;
import ru.sc222.devslife.core.ControllableFragment;
import ru.sc222.devslife.core.Entry;
import ru.sc222.devslife.core.ServiceGenerator;

public class RandomFragment extends ControllableFragment {

    private RandomTabViewModel randomTabViewModel;
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
                randomTabViewModel.setEntryAuthor(entry.getAuthor());
                randomTabViewModel.setEntryDescription(entry.getDescription());

                Log.e("Loaded", "entry author: " + entry.getDescription());
                Log.e("Loaded", "entry desc: " + entry.getDescription());
                Log.e("Loaded", "entry id: " + entry.getId());
                Log.e("Loaded", "entry preview: " + entry.getPreviewURL());

                if (entry.getType().equals(Entry.TYPE_COUB)) {
                    Log.e("Bad random post", "it has "+entry.getType()+" type");
                    //todo SHOW UNSUPPORTED POST ERROR MESSAGE
                } else {
                    Log.e("Loaded", "entry gif: " + entry.getGifURL());

                    String correctGifUrl = entry.getGifURL().replaceFirst("http", "https");
                    Log.e("Loaded", "url: " + correctGifUrl);
                    Glide.with(Objects.requireNonNull(getActivity()))
                            .asGif()
                            .placeholder(R.drawable.entry_placeholder)
                            .load(correctGifUrl)
                            .addListener(new RequestListener<GifDrawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                                    Bitmap firstFrame = resource.getFirstFrame();
                                    Palette p = Palette.from(firstFrame).generate();
                                    Palette.Swatch vibrantSwatch = p.getVibrantSwatch();

                                    if (vibrantSwatch != null) {
                                        int backgroundColor = vibrantSwatch.getRgb();
                                        backgroundColor = ColorUtils.setAlphaComponent(backgroundColor, 204);
                                        int titleColor = vibrantSwatch.getTitleTextColor();
                                        int subTitleColor = vibrantSwatch.getBodyTextColor();
                                        toolbarEntry.setBackgroundColor(backgroundColor);
                                        titleEntry.setTextColor(titleColor);
                                        subtitleEntry.setTextColor(subTitleColor);
                                    } else {
                                        toolbarEntry.setBackgroundColor(defaultBgColor);
                                        titleEntry.setTextColor(defaultTitleColor);
                                        subtitleEntry.setTextColor(defaultSubtitleColor);
                                    }
                                    return false;
                                }
                            })
                            .into(imageViewEntry);

                }
            }


        }

        @Override
        public void onFailure(Call<Entry> call, Throwable t) {

        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        randomTabViewModel = ViewModelProviders.of(this).get(RandomTabViewModel.class);
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
        titleEntry = (AppCompatTextView) root.findViewById(R.id.entry_title);
        subtitleEntry = (AppCompatTextView) root.findViewById(R.id.entry_subtitle);

        randomTabViewModel.getEntryAuthor().observe(getViewLifecycleOwner(), titleEntry::setText);
        randomTabViewModel.getEntryDescription().observe(getViewLifecycleOwner(), subtitleEntry::setText);

        APIInterface service = ServiceGenerator.getRetrofit().create(APIInterface.class);
        service.getRandomEntry().enqueue(entryCallback);

        return root;
    }

    @Override
    public void fabNextClicked() {
        APIInterface service = ServiceGenerator.getRetrofit().create(APIInterface.class);
        service.getRandomEntry().enqueue(entryCallback);

        toolbarEntry.setBackgroundColor(defaultBgColor);
        titleEntry.setTextColor(defaultTitleColor);
        subtitleEntry.setTextColor(defaultSubtitleColor);
    }

    @Override
    public void fabPreviousClicked() {

    }
}