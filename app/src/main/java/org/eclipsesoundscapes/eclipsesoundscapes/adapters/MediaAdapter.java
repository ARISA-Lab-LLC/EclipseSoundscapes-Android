package org.eclipsesoundscapes.eclipsesoundscapes.adapters;

import android.app.DialogFragment;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipsesoundscapes.eclipsesoundscapes.R;
import org.eclipsesoundscapes.eclipsesoundscapes.activity.MainActivity;
import org.eclipsesoundscapes.eclipsesoundscapes.fragments.MediaPlayerFragment;
import org.eclipsesoundscapes.eclipsesoundscapes.fragments.RumbleMapInteractionFragment;

/**
 * Created by horus on 8/8/17.
 */

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {

    private final String[] events;
    private final Integer[] descriptions;
    private final Integer[] eventImgs;
    private final Integer[] eventAudios;
    public Context mContext;

    public MediaAdapter(Context context, String[] events, Integer[] descriptions, Integer[] eventImgs, Integer[] eventAudios) {
        this.mContext = context;
        this.events = events;
        this.descriptions = descriptions;
        this.eventImgs = eventImgs;
        this.eventAudios = eventAudios;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_media, parent, false);
        return new MediaAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.mediaTitle.setText(events[position]);
        holder.mediaIcon.setImageResource(eventImgs[position]);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = MediaPlayerFragment.newInstance(events[position], eventImgs[position], eventAudios[position], descriptions[position]);
                newFragment.show(((MainActivity)mContext).getFragmentManager(), "dialog");
            }
        });
    }

    @Override
    public int getItemCount() {
        return events.length;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mediaIcon;
        TextView mediaTitle;
        ImageView mediaExtra;

        ViewHolder(View view) {
            super(view);
            mediaIcon = (ImageView) view.findViewById(R.id.list_media_img);
            mediaTitle = (TextView) view.findViewById(R.id.list_media_title);
            mediaExtra = (ImageView) view.findViewById(R.id.list_media_extra);

        }
    }

}
