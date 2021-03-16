package org.eclipsesoundscapes.ui.about;

import android.content.res.TypedArray;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.eclipsesoundscapes.ui.custom.CircleTransform;

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
 * Adapter that populates a list of team members
 *
 */

public class TeamAdapter extends RecyclerView.Adapter<PartnerTeamViewHolder> {

    private String[] teamMembers;
    private String[] descriptions;
    private String[] extra; // partner link or team member title
    private TypedArray images;

    TeamAdapter(String[] teamMembers, String extra[], String[] descriptions, TypedArray images) {
        this.teamMembers = teamMembers;
        this.extra = extra;
        this.descriptions = descriptions;
        this.images = images;
    }

    @NonNull
    @Override
    public PartnerTeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(org.eclipsesoundscapes.R.layout.item_partner, parent, false);
        return new PartnerTeamViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PartnerTeamViewHolder holder, int position) {
        holder.partner_name.setText(teamMembers[position]);
        holder.extra_detail.setText(extra[position]);
        holder.partner_description.setText(descriptions[position]);

        final RequestOptions options = new RequestOptions()
                .centerInside()
                .transform(new CircleTransform(holder.itemView.getContext()));

        Glide.with(holder.itemView.getContext())
                .load(images.getDrawable(position))
                .apply(options)
                .into(holder.partnerLogo);
    }

    @Override
    public int getItemCount() {
        return teamMembers.length;
    }
}
