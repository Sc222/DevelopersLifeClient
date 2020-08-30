package ru.sc222.devslife.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import ru.sc222.devslife.R;
import ru.sc222.devslife.custom.ErrorInfo;
import ru.sc222.devslife.custom.LoadError;
import ru.sc222.devslife.custom.SimpleEntry;
import ru.sc222.devslife.viewmodel.EntryViewModel;

public class EntriesRecyclerAdapter extends RecyclerView.Adapter<EntriesRecyclerAdapter.EntryViewHolder> {

    Context context;
    List<SimpleEntry> entries;
    private String type;

    public EntriesRecyclerAdapter(Context context, List<SimpleEntry> entries, String type) {
        this.context = context;
        this.entries = entries;
        this.type = type;
    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.newsfeed_item, parent, false);
        return new EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        SimpleEntry entry = entries.get(position);

        EntryViewModel vm = new ViewModelProvider((ViewModelStoreOwner) context).get(type + entry.getId(), EntryViewModel.class);
        holder.setViewModel(vm);

        if (entry.getGifURL().equals(""))
            vm.setError(new ErrorInfo(LoadError.COUB_NOT_SUPPORTED));
        else
            holder.loadImage(entry.getGifURL());
        holder.title.setText(entry.getAuthor());
        holder.subtitle.setText(entry.getDescription());
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public class EntryViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView image;
        AppCompatTextView title;
        AppCompatTextView subtitle;
        LinearLayoutCompat errorLayout;
        private EntryViewModel viewModel;
        private ProgressBar progressBar;
        private LinearLayoutCompat toolbar;
        private AppCompatImageView errorIcon;
        private AppCompatTextView errorTitle;

        //todo add toolbar,  progressbar, errors layout and others

        private RequestListener<GifDrawable> requestListener = new RequestListener<GifDrawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                if (model instanceof String && viewModel != null)
                    viewModel.setError(new ErrorInfo(LoadError.CANT_LOAD_IMAGE, (String) model));
                return false;
            }

            @Override
            public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                if (viewModel != null) {
                    viewModel.setIsCurrentEntryImageLoaded(true);
                    viewModel.fixError(LoadError.CANT_LOAD_IMAGE);
                }
                return false;
            }
        };

        public EntryViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.entry_image);
            title = itemView.findViewById(R.id.entry_title);
            subtitle = itemView.findViewById(R.id.entry_subtitle);
            errorLayout = itemView.findViewById(R.id.error_layout);
            progressBar = itemView.findViewById(R.id.entry_progressbar);
            toolbar = itemView.findViewById(R.id.entry_toolbar);
            errorIcon = itemView.findViewById(R.id.error_icon);
            errorTitle = itemView.findViewById(R.id.error_title);
        }

        public void loadImage(String url) {
            if (viewModel != null)
                viewModel.setIsCurrentEntryImageLoaded(false);
            image.setImageResource(R.color.placeholder_color);
            Glide.with(context)
                    .asGif()
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.gray)
                    .listener(requestListener)
                    .into(image);
        }

        public void setViewModel(EntryViewModel viewModel) {
            this.viewModel = viewModel;
            setUpObservers();
        }

        private void setUpObservers() {
            viewModel.getIsCurrentEntryImageLoaded().observe((LifecycleOwner) context, isLoaded -> {
                if (isLoaded)
                    progressBar.setVisibility(View.GONE);
                else
                    progressBar.setVisibility(View.VISIBLE);
            });

            viewModel.getError().observe((LifecycleOwner) context, errorInfo -> {
                if (errorInfo.hasErrors()) {
                    image.setImageResource(R.drawable.gray);
                    toolbar.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);

                    switch (errorInfo.getError()) {
                        case CANT_LOAD_IMAGE:
                            errorIcon.setImageResource(R.drawable.ic_offline);
                            errorTitle.setText(R.string.error_offline_short);
                            break;
                        case COUB_NOT_SUPPORTED:
                            errorIcon.setImageResource(R.drawable.ic_sorry);
                            errorTitle.setText(R.string.error_coub_short);
                            break;
                    }
                } else {
                    errorLayout.setVisibility(View.GONE);
                    toolbar.setVisibility(View.VISIBLE);
                }
            });
        }
    }
}
