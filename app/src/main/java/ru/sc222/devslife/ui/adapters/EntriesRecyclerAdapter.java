package ru.sc222.devslife.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import ru.sc222.devslife.R;
import ru.sc222.devslife.custom.SimpleEntry;

public class EntriesRecyclerAdapter extends RecyclerView.Adapter<EntriesRecyclerAdapter.EntryViewHolder> {

    Context context;
    List<SimpleEntry> entries;

    public EntriesRecyclerAdapter(Context context, List<SimpleEntry> entries) {
        this.context = context;
        this.entries = entries;
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

        //todo PROCESS ERRORS

        //todo make as it was made in random
        Glide.with(context)
                .load(entry.getGifURL())
                .into(holder.image);
        //todo setup palette

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
        //todo add toolbar,  progressbar, errors layout and others

        public EntryViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.entry_image);
            title = itemView.findViewById(R.id.entry_title);
            subtitle = itemView.findViewById(R.id.entry_subtitle);
        }
    }
}
