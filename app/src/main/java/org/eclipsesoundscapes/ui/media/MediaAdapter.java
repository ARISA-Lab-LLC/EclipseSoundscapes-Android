package org.eclipsesoundscapes.ui.media;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import org.eclipsesoundscapes.R;
import org.eclipsesoundscapes.ui.main.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

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
 * @see MediaFragment
 * Also see, {@link MediaPlayerActivity}
 *
 */

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {

    private ArrayList<String> eventList;
    private ArrayList<Integer> descriptionList;
    private ArrayList<Integer> eventImgList;
    private ArrayList<Integer> eventAudioList;
    private Context mContext;

    MediaAdapter(Context context, ArrayList<String> events, ArrayList<Integer> descriptions, ArrayList<Integer> eventImgs, ArrayList<Integer> eventAudios) {
        this.mContext = context;
        this.eventList = events;
        this.descriptionList = descriptions;
        this.eventImgList = eventImgs;
        this.eventAudioList = eventAudios;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(org.eclipsesoundscapes.R.layout.item_media, parent, false);
        return new MediaAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.mediaTitle.setText(eventList.get(position));

        Glide.with(holder.mediaIcon.getContext())
                .load(eventImgList.get(position))
                .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(10)))
                .into(holder.mediaIcon);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // setup audio player content for activity
                Intent mediaIntent = new Intent(mContext, MediaPlayerActivity.class);
                mediaIntent.putExtra(MediaPlayerActivity.EXTRA_TITLE, eventList.get(position));
                mediaIntent.putExtra(MediaPlayerActivity.EXTRA_IMG, eventImgList.get(position));
                mediaIntent.putExtra(MediaPlayerActivity.EXTRA_AUDIO, eventAudioList.get(position));
                mediaIntent.putExtra(MediaPlayerActivity.EXTRA_DESCRIPTION, descriptionList.get(position));
                mediaIntent.putExtra(MediaPlayerActivity.EXTRA_LIVE, false);
                mContext.startActivity(mediaIntent);

                if (mContext instanceof MainActivity)
                    ((MainActivity) mContext).overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.list_media_img) ImageView mediaIcon;
        @BindView(R.id.list_media_extra) ImageView mediaExtra;
        @BindView(R.id.list_media_title) TextView mediaTitle;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
