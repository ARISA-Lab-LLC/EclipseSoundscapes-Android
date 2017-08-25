package org.eclipsesoundscapes.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

import org.eclipsesoundscapes.activity.MediaPlayerActivity;

/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see [http://www.gnu.org/licenses/].
  * */


/**
 * @author Joel Goncalves
 *
 * Adapter for MediaFragment's linear recyclerview, handles on click listener to launch media player
 * @see org.eclipsesoundscapes.fragments.MediaFragment
 * Also see, {@link MediaPlayerActivity}
 *
 */

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {

    private ArrayList<String> eventList;
    private ArrayList<Integer> descriptionList;
    private ArrayList<Integer> eventImgList;
    private ArrayList<Integer> eventAudioList;
    public Context mContext;

    public MediaAdapter(Context context, ArrayList<String> events, ArrayList<Integer> descriptions, ArrayList<Integer> eventImgs, ArrayList<Integer> eventAudios) {
        this.mContext = context;
        this.eventList = events;
        this.descriptionList = descriptions;
        this.eventImgList = eventImgs;
        this.eventAudioList = eventAudios;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(org.eclipsesoundscapes.R.layout.item_media, parent, false);
        return new MediaAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mediaTitle.setText(eventList.get(position));
        holder.mediaIcon.setImageResource(eventImgList.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // setup audio player content for activity
                Intent mediaIntent = new Intent(mContext, MediaPlayerActivity.class);
                mediaIntent.putExtra("title", eventList.get(position));
                mediaIntent.putExtra("img", eventImgList.get(position));
                mediaIntent.putExtra("audio", eventAudioList.get(position));
                mediaIntent.putExtra("description", descriptionList.get(position));
                mediaIntent.putExtra("live", false);
                mContext.startActivity(mediaIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mediaIcon;
        TextView mediaTitle;
        ImageView mediaExtra;

        ViewHolder(View view) {
            super(view);
            mediaIcon = (ImageView) view.findViewById(org.eclipsesoundscapes.R.id.list_media_img);
            mediaTitle = (TextView) view.findViewById(org.eclipsesoundscapes.R.id.list_media_title);
            mediaExtra = (ImageView) view.findViewById(org.eclipsesoundscapes.R.id.list_media_extra);

        }
    }
}
